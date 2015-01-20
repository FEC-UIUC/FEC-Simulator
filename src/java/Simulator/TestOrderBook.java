package Simulator;

/*

 */

/**
 *
 * @author thibautxiong
 */
public class TestOrderBook {
    public static void main(String[] args){
        OrderBook book = new OrderBook("AAPL");
        // long orderID, String userID, String sym, long price, long qty, int side, int order_type
        Order order1 = new Order(123, "test", "AAPL", 100, 1000, 0, 1);
        Order order2 = new Order(123, "test", "AAPL", 101, 2000, 0, 1); 
        Order order3 = new Order(123, "test", "AAPL", 102, 3000, 0, 1);
        Order order4 = new Order(123, "test", "AAPL", 103, 4000, 0, 1); 
        book.insertOrder(order1);
        book.insertOrder(order2);
        book.insertOrder(order3);
        book.insertOrder(order4);
        book.printBidBook();
        

        System.out.println("\n");
        Order marketorder1 = new Order(123, "test", "AAPL", 500, 500, 1, 0); // mkt sell 500 AAPL  
        book.handleOrder(marketorder1);
        book.printBidBook();
        System.out.println("\n");
        Order marketorder2 = new Order(123, "test", "AAPL", 500, 1500, 1, 0); // mkt sell 1500 AAPL 
        book.handleOrder(marketorder2);
        book.printBidBook();
        book.simpleMarketMaker();
        
    }
    
}
