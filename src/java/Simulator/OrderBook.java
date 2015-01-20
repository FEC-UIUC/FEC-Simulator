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
            this.bids = new TreeMap<>();
            this.asks = new TreeMap<>();
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
                return asks.firstKey();
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
        public void insertOrder(Order order){
            long price = order.getPrice();
            // orders at price exist, add to end of queue
            TreeMap<Long, LinkedList<Order>> sideBook = getSideBook(order.getSide());
            //System.out.println(sideBook.keySet().toString());        
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
                if(entry.getQty() <= taker.getQty()){
                    //exhaust current order
                    taker.setQty(taker.getQty() - entry.getQty());  // filled some
                    long filled = entry.getQty();
                    entry.setQty(0L);
                    trades.add(new Trade(entry, taker, filled));
                    entries.pop(); // filled up the first order, remove it from linked list
                    entry = entries.peekFirst();
                }
                else{
                    // successfully filled the order in full
                    long filled = taker.getQty();
                    entry.setQty(entry.getQty() - taker.getQty());
                    taker.setQty(0L);
                    trades.add(new Trade(entry, taker, filled));
                }
            }
       }
       
       // gets total volume at a certain price
       public long getLevelQty(long price, int side){
          long qty = 0;
          LinkedList<Order> entries = (side == 0) ? bids.get(price) : asks.get(price);
          if(entries != null){
              for(Order order : entries){
                  qty += order.getQty();
              }
          }
          return qty;
       }
       
       public long getBestBidQty(){
           return getLevelQty(this.bestBid(), 0);
       }
       
       public long getBestAskQty(){
           return getLevelQty(this.bestAsk(), 1);
       }
       
        /* 
        Testing methods
        */
       public void printBidBook(){
           for(Long price : bids.keySet()){
               String temp = "";
               for(Order order : bids.get(price)){
                          temp += " "+ Long.toString(order.getQty());
               }
               System.out.println(Long.toString(price) + " " + temp);
              
           }
       }
       
       public void printAskBook(){
            for(Long price : asks.keySet()){
               String temp = "";
               for(Order order : asks.get(price)){
                          temp += " "+ Long.toString(order.getQty());
               }
               System.out.println(Long.toString(price) + " " + temp);
              
           }
       }
 
    private TreeMap<Long, LinkedList<Order>> getSideBook(int side){
        return (side == 0) ? bids : asks;
    }
    
    public void simpleMarketMaker(){
        boolean running = true;
        while(running){
            try{
                Thread.sleep(100);
            }
            catch(Exception e){
                System.out.println("derp");
            }   
            
            //public Order(long orderID, String userID, String sym, long price, long qty, int side, int order_type){
            insertOrder(new Order((long)12345, "MarketMakerBot", getSym(), bestBid(), (long)1000, 0, 1));
            insertOrder(new Order((long)12345, "MarketMakerBot", getSym(), bestAsk(), (long)1000, 1, 0 ));
            printBidBook();
            System.out.println("\n");
        }
       
    }
    
    
}