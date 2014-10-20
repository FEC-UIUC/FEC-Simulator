#include "exchange.h"
#include <QCryptographicHash>
#include <time.h>
#include <math.h>

#define MAX_PRICE 100000
#define MIN_PRICE 0

/* Modes:
 *
 * SIMPLE - The price of a security is driven by a hidden markov model.
 *          The security only has a price and a spread
 *
 * NORMAL - The price of a security is driven by the market participants (including bots).
 *          The security has an order book. Orders are filled as soon as possible, FCFS
 *
 * BLOCK -  Similar to NORMAL (w/ order book), except orders are queued and prioritized by best price.
 *          Every tick the queue of orders is then swapped in to be executed while the other is filled (double buffering).
 *
 */


Exchange::Exchange(Server* _server)
{
    this->server = _server;
    this->mode = NORMAL;
}


Exchange::Exchange(Server* _server, Mode _mode)
{
    this->server = _server;
    this->mode = _mode;
}

/* FUNCTION: addUser
 * DESCRIPTION: adds a new user to the users list
 * INPUT: @user_id - the user_id of the user (given by server)
 * OUTPUT: returns 0 on success, -1 on error
 */
int Exchange::addUser(int user_id){

    if(users.contains(user_id))
        return -1;

    User * new_user = new User(user_id);
    users.insert(user_id, new_user);

    return 0;
}


/* FUNCTION: removeUser
 * DESCRIPTION: removes an existing user from the users list, deletes the user object and all the user's orders
 * INPUT: @user_id - the user_id of the user (given by server)
 * OUTPUT: returns 0 on success, -1 on error
 */
int Exchange::removeUser(int user_id){

    if(!users.contains(user_id))
        return -1;

    User * remove_user = users[user_id];
    users.remove(user_id);

    for(QList<Order*>::iterator iter = remove_user->order_list.begin(); remove_user->order_list.end(); ++iter)
        delete *iter;

    delete remove_user;

    return 0;
}


/* FUNCTION: addOrder
 * DESCRIPTION: create an order and add it to the order priority queue to be executed later or execute immediately (depending on mode)
 * INPUT: @user_id - the user_id of the user placing the order
 *        @symbol - the symbol of the order security (i.e stock - "GOOG", option "GOOG-P-250-13-25-12")
 *        @quantity - quantity of securities the order wants to buy (positive) or sell (negative)
 *        @price - the price the order wishes to buy or sell at
 *        @type - indicates that this is a MARKET, LIMIT, or STOP order
 * OUTPUT: returns order_id on success
 */
QByteArray Exchange::addOrder(int user_id, QString symbol, int quantity, double price, OrderType type)
{
    if(quantity <= 0)
        return NULL;

    Order new_order = new Order(user_id, type, symbol, quantity, price);

    QCryptographicHash md5(MD5);
    new_order->order_id = md5.hash(symbol+QString::number(clock())+QString::number(user_id));

    if(mode == NORMAL){
        executeOrder(new_order);
    } else if (mode == BLOCK){
        queueOrder(new_order);
    } else if (mode == SIMPLE){
        executeOrder(new_order);
    }

    return new_order->order_id;
}


/* FUNCTION: processOrders
 * DESCRIPTION: swaps active (filling) order queue and executes all orders on the swapped queue
 * THREADING: should run on a thread separate from the incoming order handler
 * INPUT: none
 * OUTPUT: none
 */
void Exchange::processOrders()
{
    int p = cur_queue;
    cur_queue ^= 1;
    int buysell = 0;

    while(!order_queue[p].empty())
    {
        Order * next_order = order_queue[p].top();
        order_queue[p].pop();

        if(executeOrder(next_order) == -1){
            QTextStream(stdout) << "Error filling order - " << next_order->symbol << next_order->order_id << endl;
        }
    }
}





/* FUNCTION: executeOrder
 * DESCRIPTION: execute an order immediately
 * INPUT: @new_order - object of the order to execute
 * OUTPUT: returns 1 on order placed in book, 0 on order filled, -1 on error
 */
int Exchange::executeOrder(Order * new_order)
{

    Security * security = new_order->getSecurity();
    QMap<double, QQueue<Order*>*> * order_book;
    double best_price;
    bool is_sell = security->quantity < 0;

    if(is_sell){
        if(type == MARKET) new_order->price = MIN_PRICE;
        order_book = &security->order_book_selling;
        best_price = security->bid();
    } else {
        if(type == MARKET) new_order->price = MAX_PRICE;
        order_book = &security->order_book_buying;
        best_price = security->ask();
    }

    QQueue<Order*>* order_queue;

    auto meets = [] (double p, double best, bool pos) { return (p > best) ^ pos; };

    while(new_order->quantity > 0 && security->quantity > 0 && meets(price, best_price, is_sell))
    {
        order_queue = order_book[best_price];
        matched_order = order_queue->head();
        _fillOrder(new_order, matched_order);
        (is_sell) ? best_price = security->bid() : best_price = security->ask();
    }

    if(qty == 0){
        return -1;
    }

    if(new_order->quantity != 0){
        _insertOrder(new_order);
    }

    return 0;

}


int Exchange::_fillOrder(Order * new_order, Order * matched_order)
{

    int qty;

    Security * security = new_order->getSecurity();

    if(matched_order->quantity > new_order->quantity){
        qty = new_order->quantity;
        matched_order->quantity -= qty;
        new_order->quantity = 0;
        removeOrder(new_order);
    } else {
        qty = matched_order->quantity;
        new_order->quantity -= qty;
        matched_order->quantity = 0;
        removeOrder(order_queue->dequeue());
    }

    int user1 = new_order->user_id;
    int user2 = matched_order->user_id;
    QString msg1 = "@filled|" + (QString)security->order_id + "|" + QString::number(matched_order->price) + "|" + QString::number(qty);
    QString msg2 = "@filled|" + (QString)security->order_id + "|" + QString::number(matched_order->price) + "|" + QString::number(-1*qty);
    server->sendMessage(user1, msg1, "normal");
    server->sendMessage(user2, msg2, "normal");

    security->quantity -= qty;

    return qty;
}



/* Helper function, add an order to the order book */
int Exchange::_insertOrder(Order * new_order)
{
    Security * security = new_order->getSecurity();
    QMap<double, QQueue<Order*>*> * order_book;
    double price = new_order->price;
    bool is_sell = security->quantity < 0;

    if(is_sell){
        order_book = &security->order_book_selling;
    } else {
        order_book = &security->order_book_buying;
    }

    QQueue<Order*>* order_queue;

    if(!order_book->contains(price)){
        order_queue =  new Queue<Order*>();
        order_book->insert(price, order_queue);
    } else {
        order_queue = order_book[price];
    }

    order_queue->enqueue(new_order);

}

/* FUNCTION: queueOrder
 * DESCRIPTION: add order to the order queue
 * INPUT: @new_order - object of the order to execute
 * OUTPUT: 0 on success -1 on error
 */
int Exchange::queueOrder(Order * new_order)
{
    order_queue[cur_queue].add(new_order);
    return 0;
}


/* FUNCTION: removeOrder
 * DESCRIPTION: remove an existing order from book
 * INPUT: @user_id - user_id of user removing their order
 *        @order_id - order_id of order to remove
 * OUTPUT: returns 0 on success, -1 on error
 */
int Exchange::removeOrder(int user_id, QByteArray order_id)
{
    Order* cancelled_order = _findOrder(user_id, order_id);
    return removeOrder(cancelled_order);
}


/* FUNCTION: removeOrder
 * DESCRIPTION: remove an existing order from book
 * INPUT: @new_order - object of the order to remove
 * OUTPUT: returns 0 on success, -1 on error
 */
int Exchange::removeOrder(Order * cancelled_order)
{

    if(cancelled_order == NULL)
        return -1;

    int user_id = cancelled_order->user_id;

    if(!users[user_id]->order_list.removeAll(cancelled_order)){
        return -1;
    }

    Security * security = cancelled_order->getSecurity();

    QMap<double, QQueue<Order*>*>* order_book;

    if(cancelled_order->quantity > 0){
        order_book = &security->order_book_buying;
    } else {
        order_book = &security->order_book_selling;
    }

    /* if order_book is empty at price return error */
    if(!order_book[price])
        return -1;

    QQueue<Order*>* order_queue = order_book[price];

    if(order_queue->removeAll(cancelled_order))
        return -1;

    if(order_queue->isEmpty()){
        delete order_queue;
        order_book->remove(price);
    }

    delete cancelled_order;

    return 0;

}


/* helper function, retrieve order object by user_id and order_id*/
Exchange::Order* Exchange::_findOrder(int user_id, QByteArray order_id)
{
    User* user = users[user_id];

    QList<Order*>::iterator iter;
    for(iter = user->order_list.begin(); user->order_list.end(); ++iter)
    {
        if((*iter)->order_id == order_id)
            return *iter;
    }

    return NULL;
}

bool Exchange::Order_Compare::operator()(Order* order1, Order* order2){
    double val1;
    double val2;
    if(order1->quantity > 0){
       val1 = order1->price - order1->getSecurity()->bid();
    } else {
       val1 = order1->getSecurity()->ask() - order1->price;
    }
    if(order2->quantity > 0){
       val2 = order2->price - order2->getSecurity()->bid();
    } else {
       val2 = order2->getSecurity()->ask() - order2->price;
    }
    return val1 >= val2;
}
