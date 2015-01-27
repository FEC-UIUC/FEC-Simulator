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


public class ExchangeComplex extends Exchange {
    
    HashMap<String,BufferedReader> dataFeeds;
    HashMap<String, OrderBook> orderbooks;
    TreeMap<Long, Order> orders;
    TreeMap<String, User> users;
    TreeMap<String, String> sessionIds_to_name;
    
    public ExchangeComplex(){
        dataFeeds = new HashMap<String,BufferedReader>();
        orderbooks = new HashMap<>();
        orders  = new TreeMap<>();
        users  = new TreeMap<>();
        sessionIds_to_name = new TreeMap<>();
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
    public HashMap<String, String> getQuote(String symbol)  throws Exception{
        if(orderbooks.containsKey(symbol)){
             OrderBook orderbook = orderbooks.get(symbol);
             HashMap<String, String> result = new HashMap<>();
             result.put("message_type", "quote");
             result.put("symbol", symbol);
             result.put("bid_price", Long.toString(orderbook.bestBid()));
             result.put("ask_price", Long.toString(orderbook.bestAsk()));
             result.put("last_price", Long.toString((orderbook.bestAsk()+orderbook.bestBid())/2));
             result.put("bid_qty", Long.toString(orderbook.getBestBidQty()));
             result.put("ask_qty", Long.toString(orderbook.getBestAskQty()));
             return result;
         }
         else{
             throw new Exception(symbol + " does not exist");
         }
    }
    
    public Set<String> getSymList(){
        return orderbooks.keySet();
    } 
    
    
    public LinkedList<HashMap<String, String>> placeOrder(long orderID, String username, String sym, long price, long qty, int side, int order_type){
        
        LinkedList<HashMap<String, String>> responses = new LinkedList<>();
        
        Order order = new Order(orderID, username, sym, price, qty, side, order_type);
        
        if(!securityExists(sym)){
            responses.add(orderFailureMessage(order, username));
            return responses;
        }

        orders.put(orderID, order);
        
        if(users.containsKey(username)){
            users.get(username).addOrder(order);
        }
        
        LinkedList<Trade> trades = orderbooks.get(sym).handleOrder(order);
        
        if(trades.size() > 0){
            
            int sidemult = (order.getSide() == 0) ? 1 : -1;

            for(Trade trade : trades){
                System.out.println("Adding trade");
                long trade_money = trade.getFilled() * trade.getPrice();
                
                updateUsers(trade);

                responses.add(makeTradeConfirmation(trade.getMakerUsername(), trade.getMakerOrderID(), 
                                                    trade.getFilled(), trade.getMakerRemaining(), -1*sidemult*trade_money));
                
                responses.add(makeTradeConfirmation(trade.getTakerUsername(), trade.getTakerOrderID(), 
                                                    trade.getFilled(), trade.getTakerRemaining(), sidemult*trade_money));
                
                //remove maker order if empty
                if(trade.getMakerRemaining() == 0){
                    removeOrder(trade.getMakerOrderID());
                }
            }
            
        } else {
            //no trades, add resting message
            System.out.println("Adding resting order");
            responses.add(orderRestingMessage(order));
        }
        
        //remove order if empty
        if(order.getQty() == 0){
            removeOrder(orderID);
        }
        
        return responses;
    }
    
    
    private void updateUsers(Trade trade){
        
        String sym = trade.getSymbol();
        User maker = users.get(trade.getMakerUsername());
        User taker = users.get(trade.getTakerUsername());
        int makerSide = orders.get(trade.getMakerOrderID()).getSide();
        long qty = trade.getFilled();
        long price = trade.getPrice();
        if(makerSide == 0){
            maker.subtractMoney(qty*price);
            taker.addMoney(qty*price);
            maker.addPosition(sym, qty);
            taker.subtractPosition(sym, qty);
        }
        else{
            maker.addMoney(qty*price);
            taker.subtractMoney(qty*price);
            maker.subtractPosition(sym, qty);
            taker.addPosition(sym, qty);
        }
        
    }
    
    
    private void removeOrder(long orderID){
        orders.remove(orderID);
    }
    
    private HashMap<String, String> orderFailureMessage(Order order, String username){
        HashMap<String, String> result = new HashMap<>();
        result.put("message_type", "order");
        result.put("orderID", Long.toString(order.getOrderID()));
        result.put("action", "3");
        result.put("filled", "0");
        result.put("remaining", "0");
        result.put("money", "0");
        result.put("side", Integer.toString(order.getSide()));
        result.put("order_type", Integer.toString(order.getType()));
        result.put("price", Long.toString(order.getPrice()));
        result.put("symbol", order.getSym());
        result.put("username", username);
        return result;
    }
    
    private HashMap<String, String> orderRestingMessage(Order order){
        HashMap<String, String> result = new HashMap<>();
        result.put("message_type", "order");
        result.put("orderID", Long.toString(order.getOrderID()));
        result.put("action", "2");
        result.put("filled", "0");
        result.put("remaining", Long.toString(order.getQty()));
        result.put("money", "0");
        result.put("side", Integer.toString(order.getSide()));
        result.put("order_type", Integer.toString(order.getType()));
        result.put("symbol", order.getSym());
        result.put("price", Long.toString(order.getPrice()));
        result.put("username", order.getUsername());
        return result;
    }
    
    
    private HashMap<String, String> makeTradeConfirmation(String username, long orderID, long filled, long remaining, long money) {
        int side = (money > 0) ? 1 : 0;
        Order order = orders.get(orderID);
        int ordertype = order.getType();
        long orig_price = order.getPrice();
        HashMap<String, String> result = new HashMap<>();
        result.put("message_type", "order");
        result.put("orderID", Long.toString(orderID));
        result.put("action", getActionString(filled, remaining)); 
        result.put("filled", Long.toString(filled));
        result.put("remaining", Long.toString(remaining));
        result.put("money", Long.toString(money));
        result.put("side", Integer.toString(side));
        result.put("price", Long.toString(orig_price));
        result.put("order_type", Integer.toString(ordertype));
        result.put("symbol", orders.get(orderID).getSym());
        result.put("username", username);
        return result;
    }
    
    
    public HashMap<String, String> cancelOrder(String username, long orderID){
        
        Order orderToCancel = orders.get(orderID);
        
        if(orderToCancel == null){
            return makeCancelFailure(username, orderID);
        }
        
        OrderBook orderbook = orderbooks.get(orderToCancel.getSym());
        
        if(orderbook == null){
            return makeCancelFailure(username, orderID);
        }
        
        LinkedList<Order> entries;
        
        if(orderToCancel.getSide() == 0) {
            entries = orderbook.bids.get(orderToCancel.getPrice());
        } else {
            entries = orderbook.asks.get(orderToCancel.getPrice());;
        }
        
        boolean success = false;

        if(entries != null){
            success = entries.remove(orderToCancel);
        }
        
        HashMap<String, String> result = new HashMap<>();
        result.put("message_type", "cancel");
        result.put("orderID", Long.toString(orderID));
        result.put("success", success ? "1":"0");
        result.put("username", username);
        
        return result;
    }
    
    
    private HashMap<String, String> makeCancelFailure(String username, long orderID){

        HashMap<String, String> result = new HashMap<>();
               
        result.put("message_type", "cancel");
        result.put("orderID", Long.toString(orderID));
        result.put("success", "0");
        result.put("username", username);
        
        return result;
    }
    
    
    public long getUserMoney(String username){
        if(users.containsKey(username)){
            return users.get(username).getMoney();
        }
        else{
            System.out.println(username + " does not exist");
            return 0;
        }
    }
    
    
    public List<Order> getUserOrders(String username){
        if(users.containsKey(username)){
            return users.get(username).getUserOrders();
        }
        else{
            System.out.println(username + " does not exist");
            return null;
        }
    }
    
    
    public LinkedList<HashMap<String, String>> addUser(String sessionID, String username){

		LinkedList<HashMap<String, String>> responses = new LinkedList<>();
		
		long money = 0L;
        User user = null;
        if(users.containsKey(username)){
            user = users.get(username);
			money = user.getMoney();
            //TODO - recover user order/portfolio
            
        }
        
        //add new user entry
        sessionIds_to_name.put(sessionID, username);
        
        //add new user object if needed
        if(user == null){
            users.put(username, new User(sessionID));
        }
        HashMap<String, String> initial_response = new HashMap<>();
        initial_response.put("message_type", "new_user");
        initial_response.put("sessionId", sessionID);
        initial_response.put("money", Long.toString(money));
        responses.add(initial_response);
        
        return responses;
    }
    
    public boolean addSessionID(String username, String sessionID){
        if(sessionIds_to_name.containsKey(sessionID)){
            return false;
        }
        
        //add new userid entry
        sessionIds_to_name.put(sessionID, username);
        
        if(!users.containsKey(username)){
            users.put(username, new User(sessionID));
        } else {
            users.get(username).addSessionId(sessionID);
        }
        
        return true;
    }
    
	
	
    public boolean removeSessionID(String username, String sessionID){
        if(!sessionIds_to_name.containsKey(sessionID)){
            return false;
        }

        sessionIds_to_name.remove(sessionID);
        
        if(!users.containsKey(username)){
            return false;
        }
        
        users.get(username).removeSessionId(sessionID);
        
        return true;
    }
       

    public LinkedList<HashMap<String, String>> addAlgoToUser(String username, String sessionID, long algoID) {
		
        if(sessionIds_to_name.containsKey(sessionID)){
            return null;
        }
        
        //no need to "add new userid entry", done in "addUser"
        //sessionIds_to_name.put(sessionID, username);
        
        if(!users.containsKey(username)){
            users.put(username, new User(sessionID));
        } else {
            users.get(username).addAlgoSessionId(sessionID, algoID);
        }
        
        return addUser(sessionID, username);
		
	}
	
    public boolean removeAlgoFromUser(String username, String sessionID, long algoID) {
		
	if(!sessionIds_to_name.containsKey(sessionID)){
            return false;
        }

        sessionIds_to_name.remove(sessionID);
        
        if(!users.containsKey(username)){
            return false;
        }
        
        users.get(username).removeAlgoSessionId(sessionID, algoID);
        
        return true;
	
    }
	
	
    public String getAlgoSessionID(String username, long algoID) {
	if(users.containsKey(username)){
            return users.get(username).getAlgoSessionID(algoID);
        } else {
            return null;
        }
    }
	
    
    public boolean removeUser(String username){
        LinkedList<String> sessionIDs = getSessionIDs(username);
        for(String sessionID : sessionIDs){
            if(sessionID != null){
                sessionIds_to_name.remove(sessionID);
                users.get(username).removeSessionId(sessionID);
            }
        }
        return true;
    }
    
    public LinkedList<String> getSessionIDs(String username){
        if(users.containsKey(username)){
            return users.get(username).getSessionIds();
        }
        else{
            System.out.println(username + " does not exist");
            return null;
        }
    }
    
    
    
    public String getUsername(String sessionID) {
        return sessionIds_to_name.get(sessionID);
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
