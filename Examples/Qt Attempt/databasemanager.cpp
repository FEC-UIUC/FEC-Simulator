#include "databasemanager.h"

#include <QCryptographicHash>


bool DatabaseManager::connect()
{
    // Find QSLite driver
    db = QSqlDatabase::addDatabase("QMYSQL");

    db.setDatabaseName("fec-trader");
    db.setHostName("130.126.112.116");
    db.setUserName("fec_admin");
    db.setPassword("stiffrick");

    // Open database
    return db.open();
}


QSqlError DatabaseManager::lastError()
{
    // If opening database has failed user can ask
    // error description by QSqlError::text()
    return db.lastError();
}

bool DatabaseManager::close()
{
    // Close database
    db.close();

    return true;
}



bool DatabaseManager::addUser(const QString &username, const QString &password)
{
    QCryptographicHash md5(MD5);
    QString hashed_password = QString(md5.hash(password));

    QSqlQuery query;
    query.prepare("INSERT INTO users (username, password, admin, money) "
                      "VALUES (:username, :password, :admin, :money)");
    query.bindValue(":username", username);
    query.bindValue(":password", hashed_password);
    query.bindValue(":admin", admin_flag);
    query.bindValue(":money", 100000);

    return query.exec();
}

UserType DatabaseManager::checkUser(const QString &username, const QString &password)
{
    QCryptographicHash md5(MD5);
    QString hashed_password = QString(md5.hash(password));

    QSqlQuery query;
    query.prepare("SELECT admin FROM users WHERE "
                     "username=:username AND password=:password");
    query.bindValue(":username", username);
    query.bindValue(":password", hashed_password);

    query.exec();

    query.next();

    return (UserType)(query.value(0) + 1);
}

UserType DatabaseManager::checkUsername(const QString &username)
{
    QSqlQuery query;
    query.prepare("SELECT admin FROM users WHERE username=:username");
    query.bindValue(":username", username);

    query.exec();

    query.next();

    return (UserType)(query.value(0) + 1);
}
