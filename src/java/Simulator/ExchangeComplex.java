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
    HashMap<String, OrderBook> orderbooks;
    TreeMap<Long, Order> orders;
    TreeMap<String, User> users;
    TreeMap<String, String> usernames;
    
    
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
            responses.add(orderFailureMessage(orderID));
            return responses;
        }
        
        Order order = new Order(orderID, userID, sym, price, qty, side, order_type);
        
        // @TODO: Check if user exists already?
        if(users.containsKey(userID)){
            users.get(userID).addOrder(order);
        } // else add new user or just throw exception??
        else{
           return null;
        }
        orders.put(orderID, order);
        LinkedList<Trade> trades = orderbooks.get(sym).handleOrder(order);
        for(Trade trade : trades){
                HashMap<String, String> result = new HashMap<>();
                result.put("message_type", "order");
                result.put("orderID", Long.toString(trade.getMakerOrderID()));
                result.put("action", "0"); // @TODO:  0 - filled, 1 - partial fill, 2 - placed, 3 - cancelled
                result.put("filled", Long.toString(trade.getFilled()));
                result.put("remaining", Long.toString(trade.getMakerRemaining()));
                result.put("money", "0");
                responses.add(result);
                
                result = new HashMap<>();
                result.put("message_type", "order");
                result.put("orderID", Long.toString(trade.getTakerOrderID()));
                result.put("action", "0"); // @TODO:  0 - filled, 1 - partial fill, 2 - placed, 3 - cancelled
                result.put("filled", Long.toString(trade.getFilled()));
                result.put("remaining", Long.toString(trade.getTakerRemaining()));
                result.put("money", "0");
                responses.add(result);
        }
        return responses;
    }
    
    private HashMap<String, String> orderFailureMessage(long orderID){
        HashMap<String, String> result = new HashMap<>();
        result.put("message_type", "order");
        result.put("orderID", Long.toString(orderID));
        result.put("action", "3");
        result.put("filled", "0");
        result.put("remaining", "0");
        result.put("money", "0");
        return result;
    }
    
    public HashMap<String, String> cancelOrder(String userID, long orderID){
        
        Order orderToCancel = orders.get(orderID);
        LinkedList<Order> entries = (orderToCancel.getSide() == 0) ? orderbooks.get(orderToCancel).bids.get(orderToCancel.getPrice()) : orderbooks.get(orderToCancel).asks.get(orderToCancel.getPrice());
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
    
    public HashMap<String, String> addUser(String username, String userID){
        /* TODO:
            check if user already exists
            if already exists:
                -send series of messages of all active orders
                -return new_user message with appropriate money
            if new user:
                -add user to list of active users, make structure
                -return new_user message with appropriate userID and money
        */
        usernames.put(username, userID);
        users.put(userID, new User(userID));
        
        return new HashMap<>();
    }
        
    public boolean removeUser(String userID){    
        return (users.remove(userID)!= null );
    }
    
    public String getUserID(String username){
        if(usernames.containsKey(username)){
            return usernames.get(username);
        }
        else{
            System.out.println(username + " does not exist");
            return null;
        }
    }
    /* 
    Testing methods
    */

    public class User {
        String userID;
        List<Order> orders;
        long money;

        public User(String userID){
            this.userID = userID;
        }
        
        private long getMoney(){
            return money;
        }
        
        private void setMoney(Long money){
            this.money = money;
        }
        
        private List<Order> getUserOrders(){
            return orders;
        }
        
        private void addOrder(Order order){
            orders.add(order);
        }
    
    
    }
    
    

    
}
