package Simulator;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.LinkedList;

/**
 * Orderbook for one security
 * 
 * @TODO: Trade reporting
 *
 */

public class OrderBook{
	final String sym;                                            // symbol
        long askMin = 1000;                                         // minimum ask price
        long bidMax = 0;                                            // maximum bid price
        int currOrderID = 0;                                       // monotonically-increasing orderID
        TreeMap<Long, LinkedList<OrderBookEntry>> pricePoints;
        
	public OrderBook(String sym){
            this.sym = sym;
            this.pricePoints.put(askMin, new LinkedList<>());
            this.pricePoints.put(bidMax, new LinkedList<>());
            
	}
        
        public String getSym(){
		return sym;
	}
        // Makes sure there are always 1 million orders at 0 and 1000
        public void setLiquidity(){
            long liquid = 1000000;
            pricePoints.get((long)1000).peekFirst().setQty(liquid);
            pricePoints.get((long)0).peekFirst().setQty(liquid);
        }
        
        private int handleOrder(Order order){
            int type = order.getType();
            if(type == 0){
                marketOrder(order);
            }
            else{
                limitOrder(order);
            }
            setLiquidity();
            return currOrderID;
        }
        
        // Insert a limit order into the book
        private void insertOrder(long price, OrderBookEntry order){
            // orders at price exist, add to end of queue
            if(pricePoints.containsKey(price)){
                LinkedList<OrderBookEntry> entries = pricePoints.get(price);
                entries.add(order);
            }
            // no orders at price exist, create a new queue
            else{
                LinkedList<OrderBookEntry> entries = new LinkedList();
                entries.add(order);
                pricePoints.put(price, entries);
            }

        }
       
       // executes a trade and modifies the orderbook accordingly
       private void executeTrade(String buyUserID, String sellUserID, long price, long qty){
           if (qty == 0){
               return; // skips cancelled orders
           }
           
       }
        // Processes an incoming limit order
        // Order(String userID, String sym, long price, long qty, int side, int order_type)
       private int limitOrder(Order order){
           String userID = order.getUserID();
           long price = order.getPrice(); 
           long qty = order.getQty();
           int side = order.getSide();
           // buy order
           if(side == 0){ 
               LinkedList<OrderBookEntry> entries = pricePoints.get(askMin);
               OrderBookEntry entry = entries.peekFirst(); // first order at askMin price
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
                               // successfully filled the order in full
                               System.out.println("Bought");
                               entry.subtractQty(qty);
                               System.out.println("Filled order " + currOrderID + "for user " + userID);
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
               entry = new OrderBookEntry(qty, userID); // insert as a bid
               insertOrder(price, entry);
               if(bidMax < price){  // update the bidMax
                   bidMax = price;
               }
               System.out.println("Order not filled or partially filled");
           }
           // sell limit order
           else{
               LinkedList<OrderBookEntry> entries = pricePoints.get(bidMax);
               OrderBookEntry entry = entries.peekFirst();
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
                               // successfully filled the order in full
                               System.out.println("Sold");
                               entry.subtractQty(qty);
                               System.out.println("Filled order " + currOrderID + "for user " + userID);
                           }
                       }
                       // exhausted all entries at current price, try next price
                       pricePoints.remove(bidMax);
                       bidMax = pricePoints.lowerKey(bidMax);
                       entries = pricePoints.get(bidMax);
                       entry = entries.peekFirst();
                   }while(price <= bidMax);
               }
               // order was not filled or partially filled
               entry = new OrderBookEntry(qty, userID); // insert as an ask
               insertOrder(price, entry);
               if(askMin > price){
                   askMin = price;
               }
               System.out.println("Order not filled or partially filled");
               
           }
           return getOrderID();
       }
        
       private int marketOrder(Order order){
           String userID = order.getUserID();
           long price = order.getPrice(); 
           long qty = order.getQty();
           int side = order.getSide();
           boolean completed = false;
           // buy order
           if(side == 0){ 
               LinkedList<OrderBookEntry> entries = pricePoints.get(askMin);
               OrderBookEntry entry = entries.peekFirst(); // first order at askMin price
               // try to fill order
               while(completed == false){
                   if(entry == null){
                            pricePoints.remove(askMin);
                            askMin = pricePoints.higherKey(askMin);
                            entries = pricePoints.get(askMin);
                            entry = entries.peekFirst();
                        }
                    else{
                        // exhaust the current entry, need more to fill order
                        if(entry.getQty() < qty){
                            // report the trade
                            System.out.println("Bought some");
                            qty -= entry.getQty();  // filled some
                            entries.pop(); // filled up the first order, remove it from linked list
                            entry = entries.peekFirst();
                        }
                        // whole order can be filled by current entry
                        else{
                            // successfully filled the order in full
                            System.out.println("Completed the buy market order");
                            entry.subtractQty(qty);
                            System.out.println("Filled order " + currOrderID + "for user " + userID);
                            completed = true;
                        } 
                    }  
               }
           }
           // sell order
           else{ 
               LinkedList<OrderBookEntry> entries = pricePoints.get(bidMax);
               OrderBookEntry entry = entries.peekFirst(); // first order at askMin price
               // try to fill order
               while(completed == false){
                   if(entry == null){
                            pricePoints.remove(bidMax);
                            bidMax = pricePoints.lowerKey(bidMax);
                            entries = pricePoints.get(bidMax);
                            entry = entries.peekFirst();
                        }
                    else{
                        // exhaust the current entry, need more to fill order
                        if(entry.getQty() < qty){
                            // report the trade
                            System.out.println("Sold some");
                            qty -= entry.getQty();  // filled some
                            entries.pop(); // filled up the first order, remove it from linked list
                            entry = entries.peekFirst();
                        }
                        // whole order can be filled by current entry
                        else{
                            // successfully filled the order in full
                            System.out.println("Completed the buy market order");
                            entry.subtractQty(qty);
                            System.out.println("Filled order " + currOrderID + "for user " + userID);
                            completed = true;
                        } 
                    }  
               }
           }
           return getOrderID();
       }
       
       private int getOrderID(){
           currOrderID += 1;
           return currOrderID;
       }
        // A single OrderBookEntry (limit order)
        class OrderBookEntry{
            long qty;               // order qty
            String userID;          // the user that placed the order
            public OrderBookEntry(long qty, String userID){
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