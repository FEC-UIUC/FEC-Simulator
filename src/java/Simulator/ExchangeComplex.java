/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Simulator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.LinkedList;
import java.util.TreeMap;

/**
 *
 * @author thibautxiong
 */
public class ExchangeComplex extends Exchange {
    
    HashMap<String,BufferedReader> dataFeeds = new HashMap<String,BufferedReader>();
    HashMap<String, OrderBook> orderbooks = new HashMap<>();
    TreeMap<Long, Order> orders  = new TreeMap<>();
    TreeMap<String, User> users  = new TreeMap<>();
    TreeMap<String, String> usernames_to_ids = new TreeMap<>();
    
    
    public ExchangeComplex(){
        
    }
    public void nextTick() throws Exception{
        
    }
    
    //@TODO: implement exception handling for bad filename
    public boolean addOrderBooks(String fname, String symbol) throws FileNotFoundException, Exception{
        BufferedReader br = new BufferedReader(new FileReader(fname));  
        String line = null;  
        while ((line = br.readLine()) != null)  
        {  
           if(!orderbooks.containsKey(line)){
               orderbooks.put(line, new OrderBook(line));
           }
        } 
        return true;
    }
    
    // @TODO: What is this?
    public boolean addSecurity(String fname, String symbol)  throws FileNotFoundException, Exception{
        //TODO - open file and send parameters to the market bots
        orderbooks.put(symbol, new OrderBook(symbol));
        return true;
    }
    
    //check if security is mapped already
    public boolean securityExists(String symbol){		
            if(orderbooks.containsKey(symbol)){
                    return true;
            }
            return false;
    }
    
    
    // @TODO: This is just a quote? rename method?
    public HashMap<String, String> snapShot(String symbol)  throws Exception{
        if(orderbooks.containsKey(symbol)){
             OrderBook orderbook = orderbooks.get(symbol);
             HashMap<String, String> result = new HashMap<>();
             result.put("message_type", "snapshot");
             result.put("symbol", symbol);
             result.put("bid_price", Long.toString(orderbook.bestBid()));
             result.put("ask_price", Long.toString(orderbook.bestAsk()));
             result.put("last_price", Long.toString((orderbook.bestAsk()+orderbook.bestBid())/2));
             result.put("bid_qty", Long.toString(orderbook.getTotalQty(orderbook.bestBid())));
             result.put("ask_qty", Long.toString(orderbook.getTotalQty(orderbook.bestAsk())));
             return result;
         }
         else{
             throw new Exception(symbol + " does not exist");
         }
    }
    
    public Set<String> getSymList(){
        return orderbooks.keySet();
    } 
    
    
    public LinkedList<HashMap<String, String>> placeOrder(long orderID, String userID, String sym, long price, long qty, int side, int order_type){
        
        LinkedList<HashMap<String, String>> responses = new LinkedList<>();
        
        if(!securityExists(sym)){
            responses.add(orderFailureMessage(orderID, userID));
            return responses;
        }
        
        Order order = new Order(orderID, userID, sym, price, qty, side, order_type);
        
        orders.put(orderID, order);
        
        if(users.containsKey(userID)){
            users.get(userID).addOrder(order);
        }
        
        LinkedList<Trade> trades = orderbooks.get(sym).handleOrder(order);
        
        int sidemult = (order.getSide() == 0) ? 1 : -1;
        
        for(Trade trade : trades){
            long trade_money = trade.getFilled() * trade.getPrice();
            
            responses.add(makeTradeConfirmation(trade.getMakerUserID(), trade.getMakerOrderID(), 
                                                trade.getFilled(), trade.getMakerRemaining(), -1*sidemult*trade_money));
            
            responses.add(makeTradeConfirmation(trade.getTakerUserID(), trade.getTakerOrderID(), 
                                                trade.getFilled(), trade.getTakerRemaining(), sidemult*trade_money));
        }
        return responses;
    }
    
    
    private HashMap<String, String> orderFailureMessage(long orderID, String userID){
        HashMap<String, String> result = new HashMap<>();
        result.put("message_type", "order");
        result.put("orderID", Long.toString(orderID));
        result.put("action", "3");
        result.put("filled", "0");
        result.put("remaining", "0");
        result.put("money", "0");
        result.put("userID", userID);
        return result;
    }
    
    
    private HashMap<String, String> makeTradeConfirmation(String userID, long orderID, long filled, long remaining, long money) {
        HashMap<String, String> result = new HashMap<>();
        result.put("message_type", "order");
        result.put("orderID", Long.toString(orderID));
        result.put("action", getActionString(filled, remaining)); 
        result.put("filled", Long.toString(filled));
        result.put("remaining", Long.toString(remaining));
        result.put("money", Long.toString(money));
        result.put("userID", userID);
        return result;
    }
    
    
    public HashMap<String, String> cancelOrder(String userID, long orderID){
        
        Order orderToCancel = orders.get(orderID);
        
        OrderBook orderbook = orderbooks.get(orderToCancel.getSym());
        
        LinkedList<Order> entries;
        
        if(orderToCancel.getSide() == 0) {
            entries = orderbook.bids.get(orderToCancel.getPrice());
        } else {
            entries = orderbook.asks.get(orderToCancel.getPrice());;
        }
            
        boolean success = entries.remove(orderToCancel);

        HashMap<String, String> result = new HashMap<>();
        
        result.put("message_type", "cancel");
        result.put("orderID", Long.toString(orderID));
        result.put("success", success ? "1":"0");
        
        return result;
    }
    
    
    public long getUserMoney(String userID){
        if(users.containsKey(userID)){
            return users.get(userID).getMoney();
        }
        else{
            System.out.println(userID + " does not exist");
            return 0;
        }
    }
    
    
    public List<Order> getUserOrders(String userID){
        if(users.containsKey(userID)){
            return users.get(userID).getUserOrders();
        }
        else{
            System.out.println(userID + " does not exist");
            return null;
        }
    }
    
    
    public LinkedList<HashMap<String, String>> addUser(String username, String userID){

        if(usernames_to_ids.containsKey(username)){
            User user = users.get(usernames_to_ids.get(username));
            
            //TODO - recover user
            
            //remove old user entry
            users.remove(usernames_to_ids.get(username));
            usernames_to_ids.remove(username);
        }
        
        //add new user entry
        usernames_to_ids.put(username, userID);
        users.put(userID, new User(userID));
        
        return new LinkedList<>();
    }
        
    public boolean removeUser(String username){
        String userID = getUserID(username);
        if(userID == null){
            return false;
        } else {
            usernames_to_ids.remove(username);
            users.remove(userID);
            return true;
        }
    }
    
    public String getUserID(String username){
        if(usernames_to_ids.containsKey(username)){
            return usernames_to_ids.get(username);
        }
        else{
            System.out.println(username + " does not exist");
            return null;
        }
    }
    
    
    private String getActionString(long fill_qty, long remaining){
        if(Math.abs(fill_qty) > 0 && remaining == 0){
            return "0";
        } else if (Math.abs(fill_qty) > 0){
            return "1";
        } else if (fill_qty == 0 && remaining > 0){
            return "2";
        } else {
            return "3";
        }
    }
    
    /* 
    Testing methods
    */

    
    
    

    
}
