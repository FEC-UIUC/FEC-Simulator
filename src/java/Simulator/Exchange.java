/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Simulator;

import java.io.File;
import java.io.FileNotFoundException;

/**
 *
 * @author Greg Pastorek
 */
public abstract class Exchange {
    
    public abstract void nextTick() throws Exception;
    
    public abstract boolean addSecurity(File marketDataText)  throws FileNotFoundException, Exception;
    
    public abstract String snapShot(String symbol)  throws Exception;
    
    public abstract String[] getSymList(); 
    
    public abstract String placeOrder(String sym, long price, long amount, int side, int type, long orderID);
    
}
