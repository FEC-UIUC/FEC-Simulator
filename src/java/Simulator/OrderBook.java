package Simulator;
import java.util.ArrayList;

/**
 * Orderbook for one security
 * 
 */

public class OrderBook{
	final String sym;                                         // symbol
        long askMin = 50000;                                         // minimum ask price
        long bidMax = 0;                                         // maximum bid price
        int currOrderID = 0;                                   // monotonically-increasing orderID
        pricePoint[] pricePoints = new pricePoint[50001];   // max $500, index represents cents
        
	public OrderBook(String sym){
            this.sym = sym;
	}
        
        public String getSym(){
		return sym;
	}
        
        // Insert a limit order/new orderbook entry at the tail of the price point list
        public void insertOrder(pricePoint price, orderBookEntry order){
            if(price.listHead != null){
                price.listTail.next = order;
            }
            else{
                price.listHead = order;
            }
            price.listTail = order;
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
           long price = order.getPrice()*100; // price in cents
           long qty = order.getQty();
           int side = order.getSide();
           orderBookEntry entry;
           int priceIndex = (int)askMin*100;
           // buy order
           if(side == 0){ 
               pricePoint pp = pricePoints[priceIndex]; // the pricepoint to begin wtih
               // there are orders that can be filled
               if(price >= askMin){
                   do{
                       entry = pp.listHead;
                       while(entry != null){
                           // exhaust the current entry, need more to fill order
                           if(entry.qty < qty){
                               // report the trade
                               System.out.println("Bought");
                               qty -= entry.qty;  // filled some
                               entry = entry.next;
                           }
                           // whole order can be filled by current entry
                           else{
                               // report the trade
                               if(entry.qty > qty){
                                   entry.qty -= qty;
                               }
                               else{
                                   entry = entry.next;
                               }
                               // successfully filled the order in ful
                               pp.listHead = entry;
                               currOrderID += 1;
                               return currOrderID;
                           }
                          
                       }
                       
                       pp.listHead = null;
                       priceIndex += 1;
                       pp = pricePoints[priceIndex];
                       askMin += 1;
                   }while(price >= askMin);
               } 
            // if the order was not filled in full
               entry = new orderBookEntry(qty, userID); // nsert it as a bid limit order - it's remaining qty is now the best bid
               entry.qty = qty;
               insertOrder(pricePoints[(int)price], entry);
               if(bidMax < price){  // update the bidMax
                   bidMax = price;
               }
          }
           return currOrderID;
       }
        
        // A single orderBookEntry (limit order)
        public class orderBookEntry{
            long qty;               // order qty
            orderBookEntry next;    // next entry in the pricePoint list
            String userID;          // the user that placed the order
            public orderBookEntry(long qty, String userID){
                this.qty = qty;
                this.userID = userID;
                this.next = null;
            }
        }
        
        // A single pricepoint in the orderbook
        public class pricePoint{
            orderBookEntry listHead;
            orderBookEntry listTail;
            public pricePoint(){
                this.listHead = null;
                this.listTail = null;
            }
        }


}