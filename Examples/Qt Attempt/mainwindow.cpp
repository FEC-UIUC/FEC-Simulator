#include "mainwindow.h"
#include "ui_mainwindow.h"



MainWindow::MainWindow(QWidget *parent) :
    QMainWindow(parent),
    ui(new Ui::MainWindow)
{
    ui->setupUi(this);
    db = new DatabaseManager();

    ui->pushButton_logout->hide();
}

MainWindow::~MainWindow()
{
    delete ui;
    delete db;
    if(exchange) delete exchange;
    if(clients)  delete clients;
    if(client)   delete client;
}

void MainWindow::on_Button_editSim_clicked()
{
    //open new window here
}

void MainWindow::on_Button_addState_clicked()
{
    /*double prob  = ui->textEdit_stateProb->toPlainText().toDouble();
    double drift = ui->textEdit_stateDrift->toPlainText().toDouble();
    double vol   = ui->textEdit_stateVol->toPlainText().toDouble();

    sim->market->addState(prob, drift, vol);*/

}

void MainWindow::on_pushButton_createUser_clicked()
{
    QString username = ui->lineEdit_username->text();
    QString password = ui->lineEdit_password->text();

    db->connect();
    if(!db->checkUser(username)){
        if(db->addUser(username, password))
            return;
    }

    /* error adding user */
    ui->Label_connectError->setText("Error - Username already exists.");

}

/* attempts login and creates / connects to server */
void MainWindow::on_pushButton_connect_clicked()
{
    /* get UI parameters */
    QString username = ui->lineEdit_username->text();
    QString password = ui->lineEdit_password->text();
    QString address = ui->lineEdit_address->text();
    QString port = ui->lineEdit_port->text();

    /* open the database connection */
    db->connect();

    /* check user credentials and handle intialization by user type */
    switch(db->checkUser(username, password)){

        /* NONE - invalid login */
        /* prompt error, do nothing else */
        case UserType::NONE:
            ui->Label_connectError->setText("Error - Invalid login.");
            return;

        /* NORMAL - normal user */
        /* set up client object and attempt connection, prompt error on failure*/
        case UserType::NORMAL:
            if(ui->checkBox_isHost->isChecked()){
               ui->Label_connectError->setText("Error - User does not have hosting priviledges.");
               return;
            } else {
                client = new Client(this);
                if(!client->connectToServer(address, QString::toInt(port))){
                    ui->Label_connectError->setText("Error - Could not connect to server.");
                    delete client;
                    return;
                }
               is_host = false;
            }
            is_admin = false;
            break;

        /* ADMIN - user with admin priviledges */
        /* set up server if "connect as host" is checked */
        /* else set up client object and attempt connection, prompt error on failure*/
        case UserType::ADMIN:
            if(ui->checkBox_isHost->isChecked()){
               server = new Server(this, address, QString::toInt(port));
               is_host = true;
            } else {
                client = new Client(this);
                if(!client->connectToServer(address, QString::toInt(port))){
                    ui->Label_connectError->setText("Error - Could not connect to server.");
                    delete client;
                    return;
                }
               is_host = false;
            }
            is_admin = true;
            break;
    }

    /*successful login */
    ui->Label_connectError->clear();
    is_logged_in = true;
    username = ui->lineEdit_username->text();

    ui->pushButton_logout->show();
    ui->pushButton_createUser->hide();
    ui->pushButton_connect->hide();
    ui->checkBox_isHost->hide();

    ui->lineEdit_username->setDisabled();
    ui->lineEdit_password->setDisabled();
    ui->lineEdit_address->setDisabled();
    ui->lineEdit_port->setDisabled();

    return;

}

void MainWindow::on_pushButton_logout_clicked()
{
    if(is_host){
        delete server;
        server = NULL;
    } else {
        delete client;
        client = NULL;
    }

    db->close();

    is_admin = false;
    is_host = false;
    username = "";
    is_logged_in = false;

    ui->pushButton_logout->hide();
    ui->pushButton_createUser->show();
    ui->pushButton_connect->show();
    ui->checkBox_isHost->show();

    ui->lineEdit_username->setEnabled();
    ui->lineEdit_password->setEnabled();
    ui->lineEdit_address->setEnabled();
    ui->lineEdit_port->setEnabled();

    ui->lineEdit_password->clear();
}
