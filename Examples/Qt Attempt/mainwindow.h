#ifndef MAINWINDOW_H
#define MAINWINDOW_H

#include <QMainWindow>

#include <QList>

#include "databasemanager.h"
#include "exchange.h"
#include "server.h"
#include "client.h"


namespace Ui {
class MainWindow;
}

class MainWindow : public QMainWindow
{
    Q_OBJECT

public:
    explicit MainWindow(QWidget *parent = 0);
    ~MainWindow();

private slots:
    void on_Button_editSim_clicked();

    void on_Button_addState_clicked();

    void on_pushButton_createUser_clicked();

    void on_pushButton_connect_clicked();

    void on_pushButton_logout_clicked();

private:
    Ui::MainWindow *ui;

    DatabaseManager::DatabaseManager * db;
    Exchange::Exchange * exchange;
    Server::Server * server;
    QList<Client::Client*> clients;
    Client::Client* client;

    /* flags */
    bool is_admin;
    bool is_host;
    bool is_logged_in;

    /* user data */
    QString username;

};

#endif // MAINWINDOW_H
