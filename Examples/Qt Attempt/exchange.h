#ifndef EXCHANGE_H
#define EXCHANGE_H

#include <server.h>

#include <QHash>
#include <QMap>
#include <QQueue>
#include <QList>
#include <QDateTime>
#include <stdlib.h>

#define CONTRACT_QTY 100

enum Mode { SIMPLE, NORMAL, BLOCK};

enum OrderType { MARKET, LIMIT, STOP };

class Exchange : public QObject
{
    Q_OBJECT
public:

    Exchange(Server* _server);
    Exchange(Server* _server, Mode _mode);
    int addUser(int user_id);
    int removeUser(int user_id);
    QByteArray addOrder(int user_id, QString symbol, int quantity, double price, OrderType type);
    void processOrders();
    int removeOrder(int user_id, QByteArray order_id);

protected:

    enum class OptionType : char { CALL = 'C', PUT = 'P' };

    enum BuySell { BUY, SELL };

    struct Security{
        QString symbol;
        int quantity;
        QMap<double, QQueue<Order*>*> order_book_selling;
        QMap<double, QQueue<Order*>*> order_book_buying;
        double bid() const {return order_book_buying.lastKey();}
        double ask() const {return order_book_selling.firstKey();}
    };

    /*format example: GOOG-P-250-13-12-25 put option for GOOG strike @ 250 expiring 12/25/2013 */
    struct Option : public Security{
        Option(QString stock_sym, OptionType _type, double _strike, QDateTime _exp) :
            symbol(stock_sym + QString::fromLatin1((char*)&_type) + QString::number(strike) + _exp.toString()),
            type(_type), strike(_strike), expiration(_exp), quantity(0){}
        OptionType type;
        QDateTime expiration;
        double strike;
    };

    /*format: GOOG */
    struct Stock : public Security{
        Stock(QString _symbol) : symbol(_symbol), quantity(0) {}
    };

    struct User {
        User(int id) : user_id(id){}
        int user_id;
        double profit_loss;
        QHash<QString, int> stock_portfolio;
        QHash<QString, int> option_portfolio;
        QList<Order*> order_list;
    };

    struct Order {
        Order(int _user_id, OrderType _type, QString _symbol, int _quantity, double _price) :
            user_id(_user_id), type(_type), symbol(_symbol), quantity(_quantity), price(_price) {}
        QByteArray order_id;
        int user_id;
        OrderType order_type;
        QString symbol;
        int quantity;
        double price;
        Security * getSecurity() { return securities[symbol]; }
    };


    class Order_Buy_Compare{
    public:
        bool operator()(Order* order1, Order* order2){return order1->price >= order2->price;}
    };

    class Order_Sell_Compare{
    public:
        bool operator()(Order* order1, Order* order2){return order1->price >= order2->price;}
    };

    class Order_Compare{
    public:
        bool operator()(Order* order1, Order* order2);
    };

    /* private methods */
    int executeOrder(Order * new_order);
    int queueOrder(Order * new_order);
    int removeOrder(Order * cancelled_order);
    QHash<int, User*> users;
    QHash<QString, Security*> securities;
    std::priority_queue<Order*, Order_Buy_Compare> order_queue[2];

private:

    int cur_queue;
    Mode mode;

    Server* server;

    /* helper functions */
    Order* _findOrder(int user_id, QByteArray order_id);
    int _fillOrder(Order * new_order, Order * matched_order);
    int _insertOrder(Order * new_order);
};

#endif // EXCHANGE_H

