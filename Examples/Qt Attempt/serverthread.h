#ifndef THREAD_H
#define THREAD_H

#include <QThread>
#include <QTcpSocket>

#include "connection.h"
#include "exchange.h"
#include "server.h"

#define DELIM "|__c33bl34kvb__|"

class ServerThread : public QThread
{
    Q_OBJECT

public:
    ServerThread(Server *server, Exchange *_exchange);

    void run(Connection * con, QString type, QString msg);

signals:
    void error(QTcpSocket::SocketError socketError);

private:
    int socketDescriptor;
    QString text;
};

#endif
