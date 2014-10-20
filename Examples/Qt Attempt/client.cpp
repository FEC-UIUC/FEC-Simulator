#include <QtGui>
 #include <QtNetwork>

 #include "client.h"

 Client::Client(QWidget *parent)
 :   QDialog(parent), networkSession(0)
 {

     // find out which IP to connect to
     QString ipAddress;
     QList<QHostAddress> ipAddressesList = QNetworkInterface::allAddresses();
     // use the first non-localhost IPv4 address
     for (int i = 0; i < ipAddressesList.size(); ++i) {
         if (ipAddressesList.at(i) != QHostAddress::LocalHost &&
             ipAddressesList.at(i).toIPv4Address()) {
             ipAddress = ipAddressesList.at(i).toString();
             break;
         }
     }
     // if we did not find one, use IPv4 localhost
     if (ipAddress.isEmpty())
         ipAddress = QHostAddress(QHostAddress::LocalHost).toString();

     quitButton = new QPushButton(tr("Quit"));

     serverTcpSocket = new QTcpSocket(this);
     connect(serverTcpSocket, SIGNAL(readyRead()),
             this, SLOT(readTcpMessage()));
     QObject::connect(serverTcpSocket, SIGNAL(disconnected()), &pingTimer, SLOT(stop()));

     serverUdpSocket = new QUdpSocket(this);

     connect(serverUdpSocket, SIGNAL(readyRead()),
             this, SLOT(processPendingDatagrams()));

     connect(quitButton, SIGNAL(clicked()), this, SLOT(close()));

     connect(quitButton, SIGNAL(clicked()), this, SLOT(close()));
     connect(serverTcpSocket, SIGNAL(readyRead()), this, SLOT(readFortune()));
     connect(serverTcpSocket, SIGNAL(error(QAbstractSocket::SocketError)),
             this, SLOT(displayError(QAbstractSocket::SocketError)));


     QNetworkConfigurationManager manager;
     if (manager.capabilities() & QNetworkConfigurationManager::NetworkSessionRequired) {
         // Get saved network configuration
         QSettings settings(QSettings::UserScope, QLatin1String("Trolltech"));
         settings.beginGroup(QLatin1String("QtNetwork"));
         const QString id = settings.value(QLatin1String("DefaultNetworkConfiguration")).toString();
         settings.endGroup();

         // If the saved network configuration is not currently discovered use the system default
         QNetworkConfiguration config = manager.configurationFromIdentifier(id);
         if ((config.state() & QNetworkConfiguration::Discovered) !=
             QNetworkConfiguration::Discovered) {
             config = manager.defaultConfiguration();
         }

         networkSession = new QNetworkSession(config, this);
         connect(networkSession, SIGNAL(opened()), this, SLOT(sessionOpened()));

         getFortuneButton->setEnabled(false);
         statusLabel->setText(tr("Opening network session."));
         networkSession->open();
     }

     lastMsgNo = 0;
 }


bool Client::connectToServer(const QHostAddress &hostAddress, int port)
 {
    connection_successful = 0;

     /* connect Udp Socket */
     serverUdpSocket->abort();
     serverUdpSocket->bind(hostAddress, port);

     /* connect Tcp Socket */
     blockSize = 0;
     serverTcpSocket->abort();
     serverTcpSocket->connectToHost(hostAddress, port);

     /* test connection */
     if(!serverTcpSocket->waitForConnected(TIMEOUT_TIME))
         return false;

     /* get user data and verify validity*/
     clock_t timeout = clock() + TIMEOUT_TIME;
     while(clock() < timeout){
        if(connection_successful == 1){
            return true;
        } else if (connection_successful == -1){
            serverTcpSocket->close();
            serverUdpSocket->close();
            return false;
        }
     }

     return false;

 }

bool Client::sendRequest(const QString &type, const QString &message)
{

    QByteArray data = "@" + QString(user_id) + DELIM + type + DELIM + message;

    if(!serverTcpSocket->isOpen())
        return false;

    return serverTcpSocket->write(data) == data.size();

}

void Client::readTcpMessage()
 {
     QString msg;
     QDataStream in(serverTcpSocket);
     in.setVersion(QDataStream::Qt_4_0);

     if (blockSize == 0) {
         if (serverTcpSocket->bytesAvailable() < (int)sizeof(quint16))
             return;

         in >> blockSize;
     }

     if (serverTcpSocket->bytesAvailable() < blockSize)
         return;

     in >> msg;

     if(!QString::compare(msg[0], "@server")){
         if(QString::compare(msg[1], "CON_ACK")){
             if(QString::compare(msg[2], "0")){
                connection_successful = -1;
             } else {
                user_id = QString::toInt(msg[2]);
                connection_successful = 1;
             }
         } else {
             tcp_flag = true;
             tcp_msg = msg[2];
         }
     }

     return;
 }


 void Client::sessionOpened()
 {
     // Save the used configuration
     QNetworkConfiguration config = networkSession->configuration();
     QString id;
     if (config.type() == QNetworkConfiguration::UserChoice)
         id = networkSession->sessionProperty(QLatin1String("UserChoiceConfiguration")).toString();
     else
         id = config.identifier();

     QSettings settings(QSettings::UserScope, QLatin1String("Trolltech"));
     settings.beginGroup(QLatin1String("QtNetwork"));
     settings.setValue(QLatin1String("DefaultNetworkConfiguration"), id);
     settings.endGroup();

 }

/* message guideline
 * message should be in format of "@sender|__c33bl34kvb__|priority|__c33bl34kvb__|message_contents"
 * priority = high or normal
 * sender = server or username
 * */
 void Client::processPendingDatagrams()
 {
     while(serverUdpSocket->hasPendingDatagrams()){

         QByteArray datagram;
         datagram.resize(serverUdpSocket->pendingDatagramSize());
         serverUdpSocket->readDatagram(datagram.data(), datagram.size());

         QStringList msg = datagram.data().split(QRegExp(DELIM));

         if(!QString::compare(msg[0], "@server")){
             if(!QString::compare(msg[1], "high")){
                 highpriority_messages.push(msg[2]);
             } else {
                 if(msg[3].toInt() > lastMsgNo){
                     messages.push(msg[2]);
                     lastMsgNo = msg[3].toInt();
                 }
             }
         }

     }

 }

/* getNextMessage
 * get the next message in the queue
 * priority goes TCP > high priority UDP > normal priority UDP
 *
 */
 QString Client::getNextMessage()
 {
     QString ret;
     if(tcp_flag){
         tcp_flag = false;
         return tcp_msg;
     } else if(!highpriority_messages.empty()){
        ret = highpriority_messages.front();
        highpriority_messages.pop();
        return ret;
     } else if (!messages.empty()) {
         ret = messages.front();
         messages.pop();
         return ret;
     } else {
         return NULL;
     }
 }


 bool Client::newMessageReady()
 {
     return !highpriority_messages.empty() || !messages.empty() || tcp_flag;
 }
