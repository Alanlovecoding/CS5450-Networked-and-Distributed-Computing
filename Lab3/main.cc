#include <unistd.h>
#include <QVBoxLayout>
#include <QApplication>
#include <QDebug>
#include <QUdpSocket>
#include <string>
#include "main.hh"

const int ChatDialog::RUMOR_TIMEOUT = 1000;
const int ChatDialog::ANTI_ENTROPY_TIMEOUT = 1000;

ChatDialog::ChatDialog() {
    textview = new QTextEdit(this);
    textview->setReadOnly(true);
    textline = new QLineEdit(this);
    QVBoxLayout *layout = new QVBoxLayout();
    layout->addWidget(textview);
    layout->addWidget(textline);
    // QPushButton *mButton = new QPushButton(tr(&"Enter"),this);
    // layout->addWidget(mButton);
    setLayout(layout);
    sock = new NetSocket();
    sock->bind();
    setWindowTitle(sock->id);
    connect(textline, SIGNAL(returnPressed()), this, SLOT(gotReturnPressed()));
    // connect(textline, SIGNAL(clicked()), this, SLOT(gotReturnPressed()));
    connect(sock, SIGNAL(readyRead()), this, SLOT(receiveDatagram()));
    rumorTimer = new QTimer(this);
    connect(rumorTimer, SIGNAL(timeout()), this, SLOT(rumorTimeout()));
    entropyTimer = new QTimer(this);
    connect(entropyTimer, SIGNAL(timeout()), this, SLOT(entropyTimeout()));
    entropyTimer->start(ANTI_ENTROPY_TIMEOUT);
}

void ChatDialog::rumorTimeout() {
    qDebug() << "Rumor Timeout";
    return;
}

void ChatDialog::entropyTimeout() {
    qDebug() << "Anti Entropy Timeout";
    entropyTimer->start(ANTI_ENTROPY_TIMEOUT);
    quint16 next = sock->pickNextNeighbor();
    sock->updateStatus(next);
}

void ChatDialog::insertNewOrigin(QString id) {
    sock->messagesMap.insert(id, QVector<QString>());
    sock->seqMap.insert(id, 0);
}

void ChatDialog::gotReturnPressed() {
    QString curMsg = sock->id + ": " + textline->text();
    textview->append(curMsg);
    if (!sock->seqMap.contains(sock->id)) {
        insertNewOrigin(sock->id);
    }
    sock->seqMap[sock->id] = sock->seqMap[sock->id].toUInt() + 1;
    sock->messagesMap[sock->id].append(curMsg);

    quint16 next = sock->pickNextNeighbor();
    rumorTimer->start(RUMOR_TIMEOUT);
    sock->rumor(sock->id, sock->seqMap[sock->id].toUInt() - 1, next);
    textline->clear();
}

void ChatDialog::receiveDatagram() {
    QByteArray datagram;
    if (sock->pendingDatagramSize() == -1) {
        qDebug() << "no datagrams availiable";
        exit(1);
    } else {
        // resize to qint64 const
        datagram.resize(sock->pendingDatagramSize());
        QHostAddress sender;
        quint16 senderPort;
        // now host has sender's host address, port has hostPort
        sock->readDatagram(datagram.data(), datagram.size(), &sender, &senderPort);
        // read the data serialized from datagram
        QDataStream dataStream(&datagram, QIODevice::ReadOnly);
        QVariantMap msgMap;
        dataStream >> msgMap;
        // RUMOR
        if (!msgMap.contains("Want")) {
            handleRumor(msgMap, senderPort);
        } else {
            QMap<QString, QVariant> otherSeqMap = msgMap["Want"].toMap();
            handleStatus(otherSeqMap, senderPort); // STATUS
        }
    }
}

void ChatDialog::handleRumor(QMap<QString, QVariant> msgMap, quint16 senderPort) {
    entropyTimer->stop();
    entropyTimer->start(ANTI_ENTROPY_TIMEOUT);
    QString msgValue = msgMap["ChatText"].toString();
    QString origin = msgMap["Origin"].toString();
    quint32 seqNum = msgMap["SeqNo"].toUInt();
    if (!sock->seqMap.contains(origin)) {
        insertNewOrigin(origin);
    }
    // must match expected seqNum to ensure recive msg in order
    if (sock->seqMap[origin].toUInt() == seqNum) {
        textview->append(msgValue);
        sock->messagesMap[origin].append(msgValue);
        sock->seqMap[origin] = seqNum + 1;
        quint16 next = sock->pickNextNeighbor();
        rumorTimer->start(RUMOR_TIMEOUT);
        sock->rumor(origin, seqNum, next);
    }
    sock->updateStatus(senderPort);
}

void ChatDialog::handleStatus(QMap<QString, QVariant> otherSeqMap, quint16 senderPort) {
    rumorTimer->stop();

    // go through each item in my map
    for (QVariantMap::const_iterator iter = sock->seqMap.begin(); iter != sock->seqMap.end(); iter++) {
        QString origin = iter.key();
        quint32 seq =  iter.value().toUInt();

        // others need from us
        if (!otherSeqMap.contains(origin) || otherSeqMap[origin].toUInt() < seq) {
            sock->lastOrigin = origin;
            if (!otherSeqMap.contains(origin)) {
                sock->lastIdx = 0;
            } else {
                sock->lastIdx = otherSeqMap[origin].toUInt();
            }
            rumorTimer->start(RUMOR_TIMEOUT);
            sock->rumor(origin, sock->lastIdx, senderPort);
            return;
        }
    }

    // go through items in other's map
    for (QVariantMap::const_iterator iter = otherSeqMap.begin(); iter != otherSeqMap.end(); iter++) {
        QString otherOrigin = iter.key();
        quint32 otherSeq =  iter.value().toUInt();

        // we need from others
        if (!sock->seqMap.contains(otherOrigin) || sock->seqMap[otherOrigin].toUInt() < otherSeq) {
            sock->updateStatus(senderPort);
            return;
        }
    }
    if (sock->isRumor == 0) {
        return;
    }

    // flip a coin
    if (qrand() % 2 == 1) {
        if (senderPort == sock->myPort + 1) {
            sock->rumor(sock->lastOrigin, sock->lastIdx, sock->myPort - 1);
        } else {
            sock->rumor(sock->lastOrigin, sock->lastIdx, sock->myPort + 1);
        }
    } else {
        sock->isRumor = 0;
        return;
    }
}

NetSocket::NetSocket() {
    // We use the range from 32768 to 49151 for this purpose.
    myPortMin = 32768 + (getuid() % 4096) * 4;
    myPortMax = myPortMin + 16383;
    isRumor = 0;
}

bool NetSocket::bind() {
    // Try to bind to each of the range myPortMin..myPortMax in turn.
    for (int p = myPortMin; p <= myPortMax; p++) {
        if (QUdpSocket::bind(p)) {
            myPort = p;
            id = "PortID:";
            // portNumber is already unique
            id.append(QString::number(myPort));
            qDebug() << id;
            return true;
        }
    }
    qDebug() << "No available ports in my range " << myPortMin << "-" << myPortMax;
    return false;
}

void NetSocket::rumor(QString origin, quint32 idx, int port) {
    isRumor = 1;
    QVariantMap msgMap;
    QByteArray datagram;
    msgMap.insert("ChatText", messagesMap[origin][idx]);
    msgMap.insert("Origin", origin);
    msgMap.insert("SeqNo", idx);
    QDataStream dataStream(&datagram, QIODevice::WriteOnly);
    dataStream << msgMap;
    lastOrigin = origin;
    lastIdx = idx;
    writeDatagram(datagram, QHostAddress::LocalHost, port);
}

void NetSocket::updateStatus(int port) {
    QVariantMap msgMap;
    QByteArray datagram;
    msgMap.insert("Want", seqMap);
    QDataStream dataStream(&datagram, QIODevice::WriteOnly);
    dataStream << msgMap;
    writeDatagram(datagram, QHostAddress::LocalHost, port);
}

quint16 NetSocket::pickNextNeighbor() {
    if (myPort == myPortMin) {
        return myPort + 1;
    }
    if (myPortMin == myPortMax) {
        return myPortMin - 1;
    }
    return qrand() % 2 ? myPort + 1 : myPort - 1;
}

int main(int argc, char **argv) {
    QApplication app(argc, argv);
    ChatDialog dialog;
    dialog.show();
    return app.exec();
}
