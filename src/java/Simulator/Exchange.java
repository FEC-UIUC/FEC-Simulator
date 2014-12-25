/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Simulator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Greg Pastorek
 */
public abstract class Exchange {
    
    public abstract void nextTick() throws Exception;
    
    public abstract boolean addSecurity(String fname, String symbol)  throws FileNotFoundException, Exception;
    
    public abstract HashMap<String, String> snapShot(String symbol)  throws Exception;
    
    public abstract Set<String> getSymList(); 
    
    public abstract HashMap<String, String> placeOrder(String userID, String sym, long price, long amount, int side, int type, long orderID);
    
    public abstract HashMap<String, String> cancelOrder(String userID, long orderID);
    
    public abstract long getUserMoney(String userID);
    
    public abstract List<String> getUserOrders(String userID); 
    
    public abstract HashMap<String, String> addUser(String username, String userID);
        
    public abstract boolean removeUser(String userID);
    
    
    
}
