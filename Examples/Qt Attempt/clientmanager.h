#ifndef CLIENTMANAGER_H
 #define CLIENTMANAGER_H

 #include <QByteArray>
 #include <QList>
 #include <QObject>
 #include <QTimer>
 #include <QUdpSocket>

 class Client;
 class Connection;

 class ClientManager : public QObject
 {
     Q_OBJECT

 public:
     ClientManager(Client *client);

     void setServerPort(int port);
     QByteArray userName() const;
     void startBroadcasting();
     bool isLocalHostAddress(const QHostAddress &address);

 signals:
     void newConnection(Connection *connection);

 private slots:
     void sendBroadcastDatagram();
     void readBroadcastDatagram();
     void sendUnicastDatagram();

 private:
     void updateAddresses();

     Client *client;
     QList<QHostAddress> broadcastAddresses;
     QList<QHostAddress> ipAddresses;
     QHostAddress host_address;
     QUdpSocket broadcastSocket;
     QTimer broadcastTimer;
     QByteArray username;
     int serverPort;
 };

 #endif
