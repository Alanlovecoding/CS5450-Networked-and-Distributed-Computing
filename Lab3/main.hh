#ifndef P2PAPP_MAIN_HH
#define P2PAPP_MAIN_HH
#include <QDialog>
#include <QTextEdit>
#include <QLineEdit>
#include <QUdpSocket>
#include <QTimer>

class NetSocket : public QUdpSocket {
    Q_OBJECT

public:
    QString id, lastOrigin;
    quint32 lastIdx;
    int myPortMin, myPortMax, myPort, isRumor;
    QMap<QString, QVector<QString> > messagesMap;
    QMap<QString, QVariant> seqMap;
    NetSocket();
    bool bind();
    void rumor(QString origin, quint32 idx,  int port);
    void updateStatus(int rcvPort);
    quint16 pickNextNeighbor();
};

class ChatDialog : public QDialog {
    Q_OBJECT

public:
	ChatDialog();
	void handleRumor(QMap<QString, QVariant> msgMap, quint16 senderPort);
	void handleStatus(QMap<QString, QVariant> otherSeqMap, quint16 senderPort);

public slots:
	void insertNewOrigin(QString id);
    void gotReturnPressed();
    void rumorTimeout();
    void receiveDatagram();
    void entropyTimeout();

private:
    QTextEdit *textview;
    QLineEdit *textline;
    NetSocket *sock;
    QTimer *rumorTimer;
    QTimer *entropyTimer;

    static const int RUMOR_TIMEOUT;
    static const int ANTI_ENTROPY_TIMEOUT;
};

#endif // P2PAPP_MAIN_HH
