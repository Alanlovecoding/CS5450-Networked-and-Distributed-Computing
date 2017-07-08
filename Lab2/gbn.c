#include "gbn.h"

// global value of the state
state_t s;

uint16_t checksum(uint16_t *buf, int nwords) {
    uint32_t sum;
    for (sum = 0; nwords > 0; nwords--)
        sum += *buf++;
    sum = (sum >> 16) + (sum & 0xffff);
    sum += (sum >> 16);
    return (uint16_t) ~sum;
}

uint16_t checksum_hdr(gbnhdr *hdr) {
    int bufLen = sizeof(hdr->type) + sizeof(hdr->seqnum) + sizeof(hdr->data);
    uint16_t buffer[bufLen];
    buffer[0] = (uint16_t)hdr->seqnum;
    buffer[1] = (uint16_t)hdr->type;
    memcpy(buffer + 2, hdr->data, sizeof(hdr->data));
    return checksum(buffer, (bufLen / sizeof(uint16_t)));
}

void fill_header(uint8_t type, uint8_t seqnum, gbnhdr *hdr) {
    memset(hdr->data, '\0', DATALEN);
    hdr->type = type;
    hdr->seqnum = seqnum;
    hdr->checksum = checksum_hdr(hdr);
}

void handleTimeout(int signal) {
    printf("Timeout.\n");
}

// ssize_t min(int a, int b){
//   return (a) < (b) ? a : b ;
// }

int gbn_socket(int domain, int type, int protocol) {
    srand((unsigned)time(0));
    s = *(state_t*)malloc(sizeof(s));
    s.seqnum = (uint8_t)rand();
    s.windowSize = 1;
    s.state = CLOSED;
    signal(SIGALRM,handleTimeout);
    siginterrupt(SIGALRM,1);
    return socket(domain, type, protocol);
}

int gbn_connect(int sockfd, const struct sockaddr *server, socklen_t socklen) {
    s.state = SYN_SENT;
    gbnhdr *syn = malloc(sizeof(*syn));
    gbnhdr *synAck = malloc(sizeof(*synAck));
    gbnhdr *dataAck = malloc(sizeof(*dataAck));
    fill_header(SYN, s.seqnum, syn);
    struct sockaddr from;
    socklen_t fromLen = sizeof(from);
    int attempts = 0;
    while (s.state != ESTABLISHED && s.state != CLOSED) {
        // Send SYN
        if (s.state == SYN_SENT && attempts < 5) {
            int res = maybe_sendto(sockfd, syn, sizeof(*syn), 0, server, socklen);
            if (res == -1) {
                perror("Error sending SYN.");
                s.state = CLOSED;
                return -1;
            }
            printf("SYN sent.\n");
            alarm(TIMEOUT);
            attempts++;
        } else {
            printf("Error in SYN, resetting state to CLOSED.\n");
            s.state = CLOSED;
            alarm(0);
            return -1;
        }

        // Receive ACK
        if (recvfrom(sockfd, synAck, sizeof(*synAck), 0, &from, &fromLen) >= 0) {
            if (synAck->type == SYNACK && synAck->checksum == checksum_hdr(synAck)) {
                printf("SYNACK received.\n");
                s.seqnum = synAck->seqnum;
                s.addr = *server;
                fill_header(DATAACK, synAck->seqnum, dataAck);

                // send ack from client to server for three way handshake
                int res = maybe_sendto(sockfd, dataAck, sizeof(*dataAck), 0, server,socklen);
                if (res == -1) {
                    perror("Error sending DATAACK.");
                    s.state = CLOSED;
                    return -1;
                }
                printf("DATAACK sent.\n");
                s.state = ESTABLISHED;
                alarm(0);
            }
        } else {
            if (errno != EINTR) {
                printf("Error receiving SYNACK.\n");
                s.state = CLOSED;
                return -1;
            }
        }
    }
    free(syn);
    free(synAck);
    free(dataAck);
    if (s.state == ESTABLISHED) {
        printf("ESTABLISHED.\n");
        return 0;
    }
    return -1;
}

int gbn_accept(int sockfd, struct sockaddr *client, socklen_t *socklen){
    s.state = CLOSED;
    gbnhdr *syn = malloc(sizeof(*syn));
    gbnhdr *synAck = malloc(sizeof(*synAck));
    gbnhdr *dataAck = malloc(sizeof(*dataAck));
    fill_header(DATAACK, 0, dataAck);
    int attempts = 0;
    while (s.state != ESTABLISHED) {
        switch (s.state) {
            case CLOSED:
                if (recvfrom(sockfd, syn, sizeof(*syn), 0, client, socklen) >= 0) {
                    if (syn->type == SYN && syn->checksum == checksum_hdr(syn)) {
                        printf("SYN recieved.\n");
                        s.state = SYN_RCVD;
                        s.seqnum = syn->seqnum + (uint8_t) 1;
                    }
                } else {
                    printf("Error receiving SYN.\n");
                    s.state = CLOSED;
                    break; // returnn -1?
                }
                break;
            case SYN_RCVD:
                if (attempts < 5) {
                    fill_header(SYNACK, s.seqnum, synAck);
                    int res = maybe_sendto(sockfd, synAck, sizeof(*synAck), 0, client, *socklen);
                    if (res == -1) {
                        perror("Error sending SYNACK.\n");
                        s.state = CLOSED;
                        return -1;
                    }
                    printf("SYNACK sent.\n");
                    attempts++;
                    alarm(TIMEOUT);
                } else {
                    printf("Error in SYNACK, resetting state to CLOSED.\n");
                    s.state = CLOSED;
                    alarm(0);
                    return -1;
                }

                if (recvfrom(sockfd, dataAck, sizeof(*dataAck), 0, client, socklen) >= 0) {
                    if (dataAck->type == DATAACK && dataAck->checksum == checksum_hdr(dataAck)) {
                        printf("DATAACK received.\n");
                        s.state = ESTABLISHED;
                        s.addr = *client;
                        break;
                    }
                } else {
                    if (errno != EINTR) {
                        printf("Error receiving DATAACK.\n");
                        s.state = CLOSED;
                        return -1;
                    }
                }
                break;
            default: break;
        }
    }
    free(syn);
    free(synAck);
    free(dataAck);
    if (s.state == ESTABLISHED) {
        printf("ESTABLISHED.\n");
        alarm(0);
        return sockfd;
    }
    return -1;
}

int gbn_listen(int sockfd, int backlog) {
    return 0;
}

int gbn_bind(int sockfd, const struct sockaddr *server, socklen_t socklen){
    return bind(sockfd, server, socklen);
}

int gbn_close(int sockfd) {
    gbnhdr *fin1 = malloc(sizeof(*fin1));
    gbnhdr *fin2 = malloc(sizeof(*fin2));
    gbnhdr *finAck1 = malloc(sizeof(*finAck1));
    gbnhdr *finAck2 = malloc(sizeof(*finAck2));
    struct sockaddr from;
    socklen_t fromlen = sizeof(from);
    socklen_t socklen = sizeof(s.addr);
    int attempts = 0;
    while (s.state != CLOSED) {
        switch (s.state) {
            case ESTABLISHED:
                fill_header(FIN, s.seqnum, fin2);
                if (attempts < 5) {
                    int res = maybe_sendto(sockfd, fin2, sizeof(*fin2), 0, &s.addr, socklen);
                    if (res == -1) {
                        printf("Error sending FIN2.\n");
                        s.state = CLOSED;
                        return -1;
                    }
                    printf("FIN2 sent.\n");
                    attempts++;
                    alarm(TIMEOUT);
                } else {
                    printf("Error in FIN2, resetting state to CLOSED.\n");
                    s.state = CLOSED;
                    alarm(0);
                    return -1;
                }

                if (recvfrom(sockfd, finAck2, sizeof(*finAck2), 0, &from, &fromlen) >= 0) {
                    if (finAck2->type == FINACK && finAck2->checksum == checksum_hdr(finAck2)) {
                        printf("FINACK2 received.\n");
                        if(finAck1->type == FINACK && finAck1->checksum == checksum_hdr(finAck1)){
                            s.state = CLOSED;
                            break;
                        } else {
                            printf("Waiting for FIN.\n");
                            s.state = FIN_SENT;
                            break;
                        }
                    }
                } else {
                    if(errno != EINTR) {
                        printf("Error receiving FINACK2");
                        s.state = CLOSED;
                        break;
                    }
                }
                break;
            case FIN_SENT:
                if (recvfrom(sockfd, fin1, sizeof(*fin1), 0, &from, &fromlen) >= 0) {
                    if (fin1->type == FIN && fin1->checksum == checksum_hdr(fin1)) {
                        printf("FIN1 received.\n");
                        s.seqnum = fin1->seqnum + (uint8_t) 1;
                        s.state = FIN_RCVD;
                    }
                } else {
                    if (errno != EINTR) {
                        printf("Error receiving FIN1.\n");
                        s.state = CLOSED;
                        break;
                    }
                }
                break;
            case FIN_RCVD:
                fill_header(FINACK, s.seqnum, finAck1);
                if (maybe_sendto(sockfd, finAck1, sizeof(*finAck1), 0, &s.addr, socklen) >=0) {
                    printf("FINACK1 sent.\n");
                    alarm(0);
                    if (finAck2->type == FINACK && finAck2->checksum == checksum_hdr(finAck2)) {
                        s.state = CLOSED;
                    } else {
                        s.state = ESTABLISHED;
                    }
                } else {
                    printf("Error sending FINACK1.\n");
                    s.state = CLOSED;
                    break;
                }
                break;
            default: break;
        }
    }
    free(fin1);
    free(fin2);
    free(finAck1);
    free(finAck2);
    if (s.state == CLOSED){
        printf("CLOSED.\n");
        return close(sockfd);
    }
    return -1;
}

ssize_t gbn_send(int sockfd, const void *buf, size_t len, int flags) {
    printf("Server sending \n");
    gbnhdr *dataPacket = malloc(sizeof(*dataPacket));
    gbnhdr *dataAck = malloc(sizeof(*dataAck));
    struct sockaddr from;
    socklen_t fromLen = sizeof(from);
    socklen_t serverLen = sizeof(s.addr);
    int attempts = 0;
    int i = 0;
    int j = 0;
    while(i < len) {
        int unACKed = 0;
        switch (s.state) {
            case ESTABLISHED:
                for (j = 0; j < s.windowSize; j++) {
                    if ((len - i - (DATALEN - 2) * j) > 0) {
                      size_t data_length;
                        if(len - i - (DATALEN - 2) * j < DATALEN - 2){
                          data_length=len - i - (DATALEN - 2) * j;
                        }
                        else{
                          data_length=DATALEN - 2;
                        }
                        fill_header(DATA, s.seqnum + (uint8_t) j, dataPacket);
                        memcpy(dataPacket->data, (uint16_t *) &data_length, 2);
                        memcpy(dataPacket->data + 2, buf + i + (DATALEN-2) * j, data_length);
                        dataPacket->checksum = checksum_hdr(dataPacket);
                        if (attempts < 5) {
                            int res = maybe_sendto(sockfd, dataPacket, sizeof(*dataPacket), 0, &s.addr, serverLen);
                            if (res == -1) {
                                printf("Error sending data.\n");
                                s.state = CLOSED;
                                break;
                            }
                        } else {
                            printf("Error in sending, resetting state to CLOSED.\n");
                            s.state = CLOSED;
                            return -1;
                        }
                        printf("Data sent.\n");
                        unACKed++;
                    }
                }
                attempts++;
                size_t ACKed = 0;
                for (j = 0; j < unACKed; j += ACKed) {
                    if (recvfrom(sockfd, dataAck, sizeof(*dataAck), 0, &from, &fromLen) >= 0) {
                        if (dataAck->type == DATAACK && dataAck->checksum == checksum_hdr(dataAck)) {
                            printf("Data received.\n");
                            int diff = ((int)dataAck->seqnum - (int)s.seqnum);
                            if(diff>=0){
                              ACKed = (size_t)(diff);
                            }
                            else{
                              ACKed = (size_t)(diff + 256);
                            }
                            unACKed -= ACKed;
                            s.seqnum = dataAck->seqnum;
                            if(len - i - (DATALEN - 2) * j < DATALEN - 2){
                              i+=len - i - (DATALEN - 2) * j;
                            }
                            else{
                              i+=DATALEN - 2;
                            }
                            if (s.windowSize < 2) {
                                s.windowSize++;
                                printf("window: %d.\n", s.windowSize);
                            }
                            if (unACKed == 0) {
                                alarm(0);//remove alarm
                            } else {
                                //reset alarm
                                alarm(TIMEOUT);
                            }

                        } else if (dataAck->type == FIN && dataAck->checksum == checksum_hdr(dataAck)) {
                            attempts = 0;
                            s.state = FIN_RCVD;
                            alarm(0);
                            break;
                        }
                    } else {
                        if (errno == EINTR) {
                            if (s.windowSize == 2) {
                                s.windowSize =1;
                                printf("window: %d.\n", s.windowSize);
                                break;
                            }
                        } else {
                            s.state = CLOSED;
                            return -1;
                        }
                    }
                }
                break;

            case CLOSED:
                gbn_close(sockfd);
                break;

            case SYN_RCVD:
                gbn_close(sockfd);
                break;

            default: break;
        }
    }
    free(dataPacket);
    free(dataAck);
    if (s.state == ESTABLISHED) {
        return len;
    }
    return -1;
 }

ssize_t gbn_recv(int sockfd, void *buf, size_t len, int flags) {
    gbnhdr *dataPacket = malloc(sizeof(*dataPacket));
    gbnhdr *dataAck = malloc(sizeof(*dataAck));
    struct sockaddr from;
    socklen_t fromLen = sizeof(from);
    socklen_t remoteLen = sizeof(s.addr);
    int received = 0;
    int returnValue = 0;
    while (s.state == ESTABLISHED && received == 0) {
        if (recvfrom(sockfd,dataPacket, sizeof(*dataPacket), 0, &from, &fromLen) >= 0) {
            printf("Data received.\n");
            if (dataPacket->type == DATA && dataPacket->checksum == checksum_hdr(dataPacket)) {
                if (dataPacket->seqnum == s.seqnum) {
                    printf("Data seqnum.\n");
                    s.seqnum = dataPacket->seqnum + (uint8_t) 1;
                    memcpy(buf, dataPacket->data + 2, sizeof(dataPacket->data) - 2);
                    returnValue += sizeof(dataPacket->data);
                    fill_header(DATAACK, s.seqnum, dataAck);
                    received = 1;
                    returnValue += sizeof(dataPacket->data);
                } else {
                    printf("Incorrect data \n");
                    fill_header(DATAACK, s.seqnum, dataAck);
                }
                if (maybe_sendto(sockfd,dataAck, sizeof(*dataAck), 0, &s.addr, remoteLen) == -1) {
                    printf("Error in sending data acknowledgment. %d \n", errno);
                    s.state = CLOSED;
                    break;
                } else {
                    printf("Data sent.\n");
                }
            } else if (dataPacket->type == FIN && dataPacket->checksum == checksum_hdr(dataPacket)) {
                s.seqnum = dataPacket->seqnum + (uint8_t) 1;
                s.state = FIN_RCVD;
                break;
            }
        } else {
            if (errno != EINTR) {
                s.state = CLOSED;
                return -1;
            }
        }
    }
    free(dataPacket);
    free(dataAck);
    if(s.state == CLOSED){
        return -1;
    }
    return returnValue;
}

ssize_t maybe_sendto(int  s, const void *buf, size_t len, int flags, const struct sockaddr *to, socklen_t tolen) {
	char *buffer = malloc(len);
	memcpy(buffer, buf, len);

	/*----- Packet not lost -----*/
	if (rand() > LOSS_PROB*RAND_MAX){
		/*----- Packet corrupted -----*/
		if (rand() < CORR_PROB*RAND_MAX){

			/*----- Selecting a random byte inside the packet -----*/
			int index = (int)((len-1)*rand()/(RAND_MAX + 1.0));

			/*----- Inverting a bit -----*/
			char c = buffer[index];
			if (c & 0x01)
				c &= 0xFE;
			else
				c |= 0x01;
			buffer[index] = c;
		}

		/*----- Sending the packet -----*/
		int retval = sendto(s, buffer, len, flags, to, tolen);
		free(buffer);
		return retval;
	}
	/*----- Packet lost -----*/
	else
		return(len);  /* Simulate a success */
}
