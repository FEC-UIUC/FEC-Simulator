#include "serverthread.h"
#include <QtNetwork>

ServerThread::ServerThread(Server *server, Exchange *_exchange)
    : QThread(server)
{
    exchange = _exchange;
}

/*messages are user_id|DELIM|type|DELIM|message*/
void ServerThread::run(Connection * con, QString type, QString msg)
{


    switch(type){

        case 1:
            return;

        case 2:
            return;

        default:
            return;
    }

}
