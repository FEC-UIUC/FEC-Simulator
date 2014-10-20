#include "server.h"
#include "serverthread.h"

#include <stdlib.h>
#include <QMutex>
#include <QMutexLocker>

/*
 * TODO
 * -add remove user
 * -security
 * -add testing
 *
 */

/*
 * message formats:
 * CON_ACK|message|user_id
 * ERROR|message|errno
 *
 *
 * */

Server::Server(QObject *parent, const QHostAddress &address, int port)
    : QTcpServer(parent)
{
    listen(address, port);
    last_user_id = 0;
    num_users = 0;
}

/* incomingConnection
 * DESCRIPTION: Handles a new incoming tcp connection request.
 *              Adds the new connection to the connection list and send acknowledgement.
 * INPUTS: @socketDescriptor - socket for the new connection
 * OUTPUT: None
 */
void Server::incomingConnection(int socketDescriptor)
{
    Connection *connection = new Connection(this);
    connection->setSocketDescriptor(socketDescriptor);

    if(!connections.contains(connections.keys().contains(QString(socketDescriptor)))){
        ++last_user_id;
        connections.insert(QString(last_user_id), connection);
        connection->sendMessage("@server" + DELIM + "CON_ACK" + DELIM + QString(last_user_id));
        connect(connection, SIGNAL(readyRead()), this, SLOT(readTcpMessage));
    } else {
        connection->sendMessage("@server" + DELIM + "CON_ACK" + DELIM + "0";
        delete connection;
    }
}

void Server::readTcpMessage()
{
    QString datagram;

    QDataStream in(this);
    in.setVersion(QDataStream::Qt_4_0);

    if (blockSize == 0) {
        if (this->bytesAvailable() < (int)sizeof(quint16))
            return;
        in >> blockSize;
    }

    if (this->bytesAvailable() < blockSize)
        return;

    in >> datagram;

    QStringList msglist = datagram.split(RegExp(DELIM));
    int user_id = msglist[0].toInt();
    Connection * con = connections.find(user_id);
    QString type = msglist[1];
    QString msg = msglist[2];

    ServerThread *thread = new ServerThread(this, exchanges[0]); //todo, get exchange index
    connect(thread, SIGNAL(finished()), thread, SLOT(deleteLater()));
    thread->start(con, type, msg);

    return;
}

/* sends TCP message to user w/ user_id */
bool Server::sendMessage(int user_id, QString msg, QString priority="normal")
{
    if(msg.contains(DELIM) || priority.contains(DELIM))
        return false;

    Connection * connection = connections.find(user_id);

    if(connection){
       connection->sendMessage("@server" + DELIM + priority + DELIM + msg);
       return true;
    } else {
        return false;
    }
}

void Server::startBroadcasting()
{
    timer->start(1000);
}

/* sends UDP message to all users */
bool Server::broadCastMessage(QString msg, QString priority="normal")
{
    if(msg.contains(DELIM) || priority.contains(DELIM))
        return false;

    ++messageNo;

    QByteArray datagram = "@server" + DELIM + (QByteArray)priority + DELIM + (QByteArray)msg + DELIM + QByteArray::number(messageNo);

    udpSocket->writeDatagram(datagram.data(), datagram.size(), QHostAddress::Broadcast, 45454);

    return true;
}

void Server::addExchange(Exchange * exchange)
{
    exchanges.append(exchange);
}

void Server::removeExchange(Exchange * exchange)
{
    exchanges.removeAll(exchange);
}
