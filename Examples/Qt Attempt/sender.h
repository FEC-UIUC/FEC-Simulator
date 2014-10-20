#ifndef SENDER_H
#define SENDER_H

#include <QWidget>

class QUdpSocket;
class QTimer;

class Sender
{
    Q_OBJECT

public:
    Sender(QWidget * parent = 0);


private slots:
    void startBroadcasting();
    void broadcastMessage(QString msg);


private:
    QPushButton *startButton;
    QPushButton *quitButton;
    QUdpSocket *udpSocket;
    QTimer *timer;
    int messageNo;

};

#endif // SENDER_H
