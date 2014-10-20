#ifndef SERVER_H
#define SERVER_H

#include <QStringList>
#include <QTcpServer>
#include <QUdpSocket>

#include "exchange.h"

#define UDP_PORT 45454

#define TIMEOUT_TIME 2000

#define DELIM "|__c33bl34kvb__|"

class Server : public QTcpServer
{
    Q_OBJECT

public:
    Server(QObject *parent, const QHostAddress &address, int port);
    sendMessage(int user_id, QString msg, QString priority);
    void startBroadcasting();
    bool broadcastMessage(QString msg);
    void addExchange(Exchange * exchange);
    void removeExchange(Exchange * exchange);

protected:
    void incomingConnection(int socketDescriptor);

private slots:
    void readTcpMessage();

private:
    QPushButton *startButton;
    QPushButton *quitButton;
    QUdpSocket *udpSocket;
    QTimer *timer;
    int messageNo;
    quint16 blockSize;
    QMultiHash<QString, Connection*> connections;

    int num_users;
    int last_user_id;

    QList<Exchange*> exchanges;

};

#endif
