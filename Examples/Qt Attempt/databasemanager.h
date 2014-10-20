#ifndef DATABASE_H
#define DATABASE_H

#include <QString>
#include <QObject>
#include <QSqlError>
#include <QFile>
#include <QSqlDatabase>


enum UserType { NONE, NORMAL, ADMIN };

class DatabaseManager : public QObject
    {
    public:
        DatabaseManager(QObject *parent = 0);
        ~DatabaseManager();

    public:
        bool connect();
        bool close();
        QSqlError lastError();

        bool addUser(const QString &username, const QString &password);
        UserType checkUser(const QString &username, const QString &password);
        UserType checkUsername(const QString &username);

    private:
        void initialize();

    private:
        QSqlDatabase db;
        int admin_flag;
    };

#endif // DATABASE_H
