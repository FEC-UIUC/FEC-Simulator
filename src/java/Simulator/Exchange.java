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
    
    public abstract HashMap<String, String> snapShot(String symbol)  throws Exception;
    
    public abstract Set<String> getSymList(); 
    
    public abstract LinkedList<HashMap<String, String>> placeOrder(long orderID, String userID, String sym, long price, long qty, int side, int order_type);
    
    public abstract HashMap<String, String> cancelOrder(String userID, long orderID);
    
    public abstract long getUserMoney(String userID);
    
    public abstract List<Order> getUserOrders(String userID); 
    
    public abstract HashMap<String, String> addUser(String username, String userID);
        
    public abstract boolean removeUser(String userID);
    
    
    
}
