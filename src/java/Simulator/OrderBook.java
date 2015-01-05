package Simulator;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.LinkedList;

/**
 * Orderbook for one security
 * 
 */

public class OrderBook{
	final String sym;                                            // symbol
        long askMin = 1000;                                         // minimum ask price
        long bidMax = 0;                                            // maximum bid price
        int currOrderID = 0;                                       // monotonically-increasing orderID
        TreeMap<Long, LinkedList<orderBookEntry>> pricePoints;
        
	public OrderBook(String sym){
            this.sym = sym;
	}
        
        public String getSym(){
		return sym;
	}
        
        // Insert a limit order into the book
        public void insertOrder(long price, orderBookEntry order){
            // orders at price exist, add to end of queue
            if(pricePoints.containsKey(price)){
                LinkedList<orderBookEntry> entries = pricePoints.get(price);
                entries.add(order);
            }
            // no orders at price exist, create a new queue
            else{
                LinkedList<orderBookEntry> entries = new LinkedList();
                entries.add(order);
                pricePoints.put(price, entries);
            }

        }
       
       // executes a trade and modifies the orderbook accordingly
       public void executeTrade(String buyUserID, String sellUserID, long price, long qty){
           if (qty == 0){
               return; // skips cancelled orders
           }
           
       }
        // Processes an incoming limit order
        // Order(String userID, String sym, long price, long qty, int side, int order_type)
       public int limitOrder(Order order){
           String userID = order.getUserID();
           long price = order.getPrice(); 
           long qty = order.getQty();
           int side = order.getSide();
           // buy order
           if(side == 0){ 
               LinkedList<orderBookEntry> entries = pricePoints.get(askMin);
               orderBookEntry entry = entries.peekFirst(); // first order at askMin price
               // try to fill orders
               if(price >= askMin){
                   do{
                       while(entry != null){
                           // exhaust the current entry, need more to fill order
                           if(entry.getQty() < qty){
                               // report the trade
                               System.out.println("Bought");
                               qty -= entry.getQty();  // filled some
                               entries.pop(); // filled up the first order, remove it from linked list
                               entry = entries.peekFirst();
                               
                           }
                           // whole order can be filled by current entry
                           else{
                               // report the trade
                               System.out.println("Bought");
                                entry.subtractQty(qty);

                               // successfully filled the order in full
                               currOrderID += 1;
                               System.out.println("Filled order " + currOrderID);
                               return currOrderID;
                           }
                          
                       }
                       // exhausted all entries at current price, try next price
                       pricePoints.remove(askMin);
                       askMin = pricePoints.higherKey(askMin);
                       entries = pricePoints.get(askMin); 
                       entry = entries.peekFirst();
                   }while(price >= askMin);
               } 
               // order was not filled or partially filled
               entry = new orderBookEntry(qty, userID); // insert as a bid- it's remaining qty is now the best bid
               insertOrder(price, entry);
               if(bidMax < price){  // update the bidMax
                   bidMax = price;
               }
               System.out.println("Order partially filled");
           }
           // sell limit order
           else{
               LinkedList<orderBookEntry> entries = pricePoints.get(bidMax);
               orderBookEntry entry = entries.peekFirst();
               // try to fill orders
               if(price >= bidMax){
                   do{
                       while(entry != null){
                           // exhaust the current entry, need more to fill order
                           if(entry.getQty() < qty){
                               // report the trade
                               System.out.println("Sold");
                               qty -= entry.getQty();
                               entries.pop();
                               entry = entries.peekFirst();
                           }
                           // whole order can be filled by current entry
                           else{
                               // report the trade
                               System.out.println("Sold");
                               entry.subtractQty(qty);
                               
                               // successfully filled the order in full
                               currOrderID += 1;
                               System.out.println("Filled order " + currOrderID);
                               return currOrderID;
                           }
                       }
                       // exhausted all entries at current price, try next price
                       pricePoints.remove(bidMax);
                       bidMax = pricePoints.lowerKey(bidMax);
                       entries = pricePoints.get(bidMax);
                       entry = entries.peekFirst();
                   }while(price <= bidMax);
               }
               
           }
           return currOrderID;
       }
        
        // A single orderBookEntry (limit order)
        public class orderBookEntry{
            long qty;               // order qty
            String userID;          // the user that placed the order
            public orderBookEntry(long qty, String userID){
                this.qty = qty;
                this.userID = userID;
            }
            
            public long getQty(){
                return qty;
            }
            
            public void setQty(long qty){
                this.qty = qty;
            }
            
            public void addQty(long qty){
                this.qty += qty;
            }
            
            public void subtractQty(long qty){
                this.qty -= qty;
            }
        }


}