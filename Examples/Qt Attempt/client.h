#ifndef CLIENT_H
 #define CLIENT_H

 #include <QDialog>
 #include <QTcpSocket>
 #include <stdlib.h>

 #define UDP_PORT 45454

 #define TIMEOUT_TIME 10000

 #define DELIM "|__c33bl34kvb__|"


/*
 * connection error codes:
 * 1 - connection time out, could not establish
 * 2 - socket descriptor already in use
 *
 *
 *
 *
 * */

 class QDialogButtonBox;
 class QLabel;
 class QLineEdit;
 class QPushButton;
 class QTcpSocket;
 class QNetworkSession;
 class std;

 class Client : public QObject
 {
     Q_OBJECT

 public:
     Client(QWidget *parent = 0);
     bool Client::newMessageReady();
     QString getNextMessage();
     bool sendRequest(const QString &message);
     bool connectToServer(const QHostAddress &hostAddress, int port);

 private slots:
     bool readTcpMessage();
     void displayError(QAbstractSocket::SocketError socketError);
     void enableGetFortuneButton();
     void sessionOpened();
     void processPendingDatagrams();

 private:

     QString username;
     int user_id;

     QUdpSocket *serverUdpSocket;
     QTcpSocket *serverTcpSocket;
     quint16 blockSize;

     QNetworkSession *networkSession;

     QString tcp_msg;
     volatile bool tcp_flag = false;

     std::queue<QString> highpriority_messages;
     std::queue<QString> messages;

     QTimer *timer;

     int lastMsgNo;


 };

 #endif
