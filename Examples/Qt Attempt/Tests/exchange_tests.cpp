#define EXCHANGE_TESTS 1
#include "exchange_test.h"
#include <QTimer>
#include <QTextStream>
#include <QtTest/QTest>

/*
 *   Order * queueOrder(int user_id, QString symbol, int quantity, double price, OrderType type);
 *   int removeOrder(Order * cancelled_order);
 *   Order* insertOrder(int user_id, QString symbol, int quantity, double price, OrderType type);
 *   Order* insertOrder(Order * new_order);
 */

#define MAX_LOOPS 15

const QHostAddress HOST_SOCKET = 0;
int PORT = 0;

int ExchangeTest::test_queueOrder()
{
    Exchange * exchange = new Exchange(this, HOST_SOCKET, PORT);
    exchange->addUser(1);

    QTimer * timer = new QTimer();

    while(loops < MAX_LOOPS){
        timer->start(1000);
        while(timer->remainingTime())
        {

        }
    }

    return 0;

}


int ExchangeTest::test_fillOrder()
{
    Server * server = new Server(this, HOST_SOCKET, PORT);
    Exchange * exchange = new Exchange();
    exchange->addUser(1);
    exchange->addUser(2);

    QTimer * timer = new QTimer();

    QByteArray order_id11 = exchange->addOrder(1, "GOOG", -10, 100, MARKET);
    QByteArray order_id12 = exchange->addOrder(1, "GOOG", -10, 101, MARKET);
    QByteArray order_id13 = exchange->addOrder(1, "GOOG", -10, 102, MARKET);
    QByteArray order_id14 = exchange->addOrder(1, "GOOG", -10, 103, MARKET);
    QByteArray order_id15 = exchange->addOrder(1, "GOOG", -10, 104, MARKET);

    QByteArray order_id21 = exchange->addOrder(2, "GOOG", 10, 99, MARKET);

    QByteArray order_id22 = exchange->addOrder(2, "GOOG", 10, 104, MARKET);
    QVERIFY(order_id22 == 0); /*fill 10 at 100*/
    QVERIFY(order_id11 == 0); /*fill 10 at 100*/

    QByteArray order_id23 = exchange->addOrder(2, "GOOG", 10, 103, MARKET);
    QVERIFY(order_id23 == 0); /*fill 10 at 101*/
    QVERIFY(order_id12 == 0); /*fill 10 at 101*/

    QByteArray order_id24 = exchange->addOrder(2, "GOOG", 5, 101, MARKET);

    QByteArray order_id25 = exchange->addOrder(2, "GOOG", 5, 105, MARKET);
    QVERIFY(order_id25 == 0); /*fill 5 at 102*/
    QVERIFY(order_id13 == 0); /*fill 5 at 102*/

    QByteArray order_id26 = exchange->addOrder(2, "GOOG", 5, 103, MARKET);
    QVERIFY(order_id26 == 0); /*fill 5 at 102*/
    QVERIFY(order_id13 == 0); /*fill 5 at 102*/

    QByteArray order_id27 = exchange->addOrder(2, "GOOG", 10, 106, MARKET);
    QVERIFY(order_id27 == 0); /*fill 10 at 104*/
    QVERIFY(order_id14 == 0); /*fill 10 at 104*/

    QByteArray order_id28 = exchange->addOrder(2, "GOOG", 10, 100, MARKET);

    QByteArray order_id16 = exchange->addOrder(1, "GOOG", -30, 100, MARKET);
    QVERIFY(order_id28 == 0); /*fill 10 at 100*/
    QVERIFY(order_id16 == 0); /*fill 10 at 100*/

    /*TODO - make dummy client to receive order acknowledgement signals */
    /* verifies below show expected output, needs to be fixed */
    QVERIFY(order_id21 == 0); /*no fill*/
    QVERIFY(order_id24 == 0); /*no fill*/

    return 0;

}


int ExchangeTest::test_InsertRemoveOrder()
{

    Exchange * exchange = new Exchange(this, HOST_SOCKET, PORT);
    exchange->addUser(1);
    QByteArray order_id1 = exchange->addOrder(1, "GOOG", 10, 100, MARKET);
    QByteArray order_id2 = exchange->addOrder(1, "GOOG", 10, 100, MARKET);
    QByteArray order_id3 = exchange->addOrder(1, "GOOG", 10, 50, MARKET);
    QByteArray order_id4 = exchange->addOrder(1, "GOOG", 10, 50, MARKET);
    QByteArray order_id5 = exchange->addOrder(1, "GOOG", 10, 50, MARKET);
    QByteArray order_id6 = exchange->addOrder(1, "GOOG", 10, 100, MARKET);
    QByteArray order_id7 = exchange->addOrder(1, "GOOG", 10, 100, MARKET);
    QByteArray order_id8 = exchange->addOrder(1, "GOOG", 10, 100, MARKET);

    int result1 = exchange->removeOrder(1, order_id1);
    int result6 = exchange->removeOrder(1, order_id6);
    int result8 = exchange->removeOrder(1, order_id8);
    int result2 = exchange->removeOrder(1, order_id2);

    int result4 = exchange->removeOrder(1, order_id4);
    int result3 = exchange->removeOrder(1, order_id3);
    int result5 = exchange->removeOrder(1, order_id5);
    int result7 = exchange->removeOrder(1, order_id7);



    return 0;
}



int ExchangeTest::dump_order_queue(Exchange * exchange)
{
    std::priority_queue<Order*, Order_Compare> copy_queue;

    QTextStream(stdout) << endl << "---Dumping Order Queues ---" << endl;

    QTextStream(stdout) << "Order ID";
    QTextStream(stdout) << "\t" << "Order Type";
    QTextStream(stdout) << "\t" << "Position:";
    QTextStream(stdout) << "\t" << "Price";
    QTextStream(stdout) << "\t" << "Quantity";
    QTextStream(stdout) << "\t" << "Symbol";
    QTextStream(stdout) << "\t" << "User ID:";
    QTextStream(stdout) << endl;

    for(int i=0; i < 2; i++)
    {
        copy_queue = exchange->order_queue[i];
        QTextStream(stdout) << "Dumping Order Queue" << i << endl;
        while(!copy_queue.empty())
        {
            Order * order = copy_queue.top();
            copy_queue.pop();
            dump_order(order);
        }
    }

    return 0;
}


int ExchangeTest::dump_all_order_books(Exchange * exchange)
{
    std::priority_queue<Order*, Order_Compare> copy_queue;

    QTextStream(stdout) << endl << "---Dumping Order Book ---" << endl;

    QTextStream(stdout) << "Order ID";
    QTextStream(stdout) << "\t" << "Order Type";
    QTextStream(stdout) << "\t" << "Position:";
    QTextStream(stdout) << "\t" << "Price";
    QTextStream(stdout) << "\t" << "Quantity";
    QTextStream(stdout) << "\t" << "Symbol";
    QTextStream(stdout) << "\t" << "User ID:";
    QTextStream(stdout) << endl;

    QHash<QString, Security*>::iterator security_iter;

    for(security_iter = securities.begin(); securities.end(); ++security_iter)
    {
        Security * security = securities[*security_iter];
        dump_order_book(security);
    }

    return 0;
}

int ExchangeTest::dump_order_book(Security * security)
{
    QTextStream(stdout) << endl << "Dumping Order Book for " << security->symbol << endl;

    QMap<double, QQueue<Order*>*> order_book_iter;

    /* iterate through buys */
    for(order_book_iter = security->order_book_buying.begin(); security->order_book_buying.end(); ++order_book_iter)
    {
        QQueue<Order*> copy_queue = *security->order_book_buying[*order_book_iter];
        while(!copy_queue.empty())
        {
            Order * order = copy_queue.dequeue();
            dump_order(order);
        }
    }

    /* iterate through sells */
    for(order_book_iter = security->order_book_selling.begin(); security->order_book_selling.end(); ++order_book_iter)
    {
        QQueue<Order*> copy_queue = *security->order_book_selling[*order_book_iter];
        while(!copy_queue.empty())
        {
            Order * order = copy_queue.dequeue();
            dump_order(order);
        }
    }
}

void ExchangeTest::dump_order(Order * order)
{
    Order * order = copy_queue.top();
    copy_queue.pop();
    QTextStream(stdout) << order->order_id;
    QTextStream(stdout) << "\t" << order->order_type;
    QTextStream(stdout) << "\t" << ((order->quantity > 0) ? "Long" : "Short");
    QTextStream(stdout) << "\t" << order->price;
    QTextStream(stdout) << "\t" << order->quantity;
    QTextStream(stdout) << "\t" << order->symbol;
    QTextStream(stdout) << "\t" << order->user_id;
    QTextStream(stdout) << endl;
}
