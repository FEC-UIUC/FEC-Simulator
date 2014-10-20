#ifndef EXCHANGE_TEST_H
#define EXCHANGE_TEST_H

#include "exchange.h"
#include <QtTest/QTest>

class ExchangeTest: public Exchange
 {
     Q_OBJECT
 private slots:
     void initTestCase()
     { qDebug("called before everything else"); }
     int test_queueOrder();
     int test_fillOrder();
     int test_InsertRemoveOrder();
     void cleanupTestCase()
     { qDebug("called after myFirstTest and mySecondTest"); }

private:
     int dump_all_order_books(Exchange * exchange);
     int dump_order_book(Security * security);
     int dump_order_queue(Exchange * exchange);
     void dump_order(Order * order);

 };





#endif // EXCHANGE_TEST_H
