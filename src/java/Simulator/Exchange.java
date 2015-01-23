package Simulator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.LinkedList;

/**
 *
 * @author Greg Pastorek
 */
public abstract class Exchange {
    
    public abstract void nextTick() throws Exception;
    
    public abstract boolean addSecurity(String fname, String symbol)  throws FileNotFoundException, Exception;
    
    public abstract HashMap<String, String> getQuote(String symbol)  throws Exception;
    
    public abstract Set<String> getSymList(); 
    
    public abstract LinkedList<HashMap<String, String>> placeOrder(long orderID, String username, String sym, long price, long qty, int side, int order_type);
    
    public abstract HashMap<String, String> cancelOrder(String username, long orderID);
    
    public abstract long getUserMoney(String username);
    
    public abstract List<Order> getUserOrders(String username); 
    
    public abstract LinkedList<HashMap<String, String>> addUser(String username, String sessionID);
    
    public abstract boolean addSessionID(String username, String algoID);
        
    public abstract boolean removeSessionID(String sessionID);
    
    public abstract boolean removeAlgoFromUser(String username, String algoID);
	
	public abstract LinkedList<HashMap<String, String>> addAlgoToUser(String username, String sessionID, long algoID);
	
	public abstract boolean removeAlgoFromUser(String username, String sessionID, long algoID);
	
	public abstract String getAlgoSessionID(String username, long algoID);
    
    public abstract String getUsername(String sessionID);
    
    public abstract LinkedList<String> getSessionIDs(String username);
    
}
