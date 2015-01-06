package Simulator;
import java.util.HashMap;
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
        TreeMap<Long, LinkedList<Order>> bids;
        TreeMap<Long, LinkedList<Order>> asks;
        
        
	public OrderBook(String sym){
            this.sym = sym;         
	}
        
        public String getSym(){
		return sym;
	}

        public long bestBid(){
            if(!bids.isEmpty()){
                return bids.lastKey();
            } else {
                return Long.MIN_VALUE;
            }
        }
        
        public long bestAsk(){
            if(!asks.isEmpty()){
                return asks.lastKey();
            } else {
                return Long.MAX_VALUE;
            }
        }

       
        // Processes an incoming limit order
        // Order(String userID, String sym, long price, long qty, int side, int order_type)
       public LinkedList<Trade> handleOrder(Order order){
           
           LinkedList<Trade> trades = new LinkedList<>();
           boolean isMarket = (order.getType() == 0);
                   
           //get opposing side book
           TreeMap<Long, LinkedList<Order>> sideBook = getSideBook(1 - order.getSide());
           
           //side multiplier so for use in inequalities comparing price
           long sidemult = (order.getSide() == 0) ? 1 : -1;
           
           //bestBid or bestAsk, best price for matching order
           long bestPrice = (order.getSide() == 1) ? bestBid() : bestAsk();
               
            // try to fill orders
            while(!sideBook.isEmpty() && order.getQty() > 0 &&
                    (order.getPrice()*sidemult >= bestPrice*sidemult || isMarket)){
                LinkedList<Order> entries = sideBook.get(bestPrice);
                fillOrder(order, entries, trades);
                if(entries.isEmpty()){
                    sideBook.remove(bestPrice);
                }
                bestPrice = (order.getSide() == 1) ? bestBid() : bestAsk();
            } 
            
            //place order if remaining and not a market order
            if(order.getQty() > 0 && !isMarket){
                insertOrder(order);
            }
            
            //no resting orders for market order
            if(isMarket){
                order.setQty(0L);
            }
            
            return trades;
       }
       
       
       // Insert a limit order into the book
        private void insertOrder(Order order){
            long price = order.getPrice();
            // orders at price exist, add to end of queue
            TreeMap<Long, LinkedList<Order>> sideBook = getSideBook(order.getSide());
            if(sideBook.containsKey(price)){
                LinkedList<Order> entries = sideBook.get(price);
                entries.add(order);
            }
            // no orders at price exist, create a new queue
            else{
                LinkedList<Order> entries = new LinkedList();
                entries.add(order);
                sideBook.put(price, entries);
            }
        }
       
       // executes a trade and modifies the orderbook accordingly
       private void fillOrder(Order taker, LinkedList<Order> entries, LinkedList<Trade> trades){
           Order entry = entries.peekFirst(); // first order at bestPrice
           while(entry != null && taker.getQty() > 0){
                if(entry.getQty() < taker.getQty()){
                    //exhaust current order
                    trades.add(new Trade(entry, taker, entry.getQty()));
                    taker.setQty(taker.getQty() - entry.getQty());  // filled some
                    entries.pop(); // filled up the first order, remove it from linked list
                    entry = entries.peekFirst();
                }
                else{
                    // successfully filled the order in full
                    trades.add(new Trade(entry, taker, taker.getQty()));
                    entry.setQty(entry.getQty() - taker.getQty());
                    taker.setQty(0L);
                }
            }
       }
       
       public long getTotalQty(long price){
          long qty = 0;
          LinkedList<Order> entries = bids.get(price);
          for(Order order : entries){
              qty += order.getQty();
          }
          return qty;
       }
 
    private TreeMap<Long, LinkedList<Order>> getSideBook(int side){
        return (side == 0) ? bids : asks;
    }
    
    
}