#include "sender.h"


Sender::Sender()
{

}

void Sender::startBroadcasting()
{
    timer->start(1000);
}

void Sender::broadCastMessage(QString msg)
{
    QByteArray datagram = "@server|" + (QByteArray)msg;

    udpSocket->writeDatagram(datagram.data(), datagram.size(), QHostAddress::Broadcast, 45454);
    ++messageNo;
}


