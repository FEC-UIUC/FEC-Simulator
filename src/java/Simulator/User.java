/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Simulator;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

/**
 *
 * @author Greg Pastorek
 */
public class User {
    
    LinkedList<String> sessionIDs;
    LinkedList<String> nonAlgoSessionIDs;
    List<Order> orders = new LinkedList<>();
    TreeMap<String, Long> portfolio = new TreeMap<>();
    long money; 
    
    private HashMap<String, File> algoFiles = new HashMap<>();
    private HashMap<Long, String> algoSessionIDs = new HashMap<>();

    public User(String sessionID){
        this.sessionIDs = new LinkedList<>();
        this.sessionIDs.add(sessionID);
    }
    
    public boolean addNonAlgoSessionId(String sessionID){
        this.nonAlgoSessionIDs.add(sessionID);
        return this.sessionIDs.add(sessionID);
    }
    
    public boolean removeSessionId(String sessionID){
        this.nonAlgoSessionIDs.remove(sessionID);
        return this.sessionIDs.remove(sessionID);
    }
	
    public boolean addAlgoSessionId(String sessionID, long algoID){
	this.algoSessionIDs.put(algoID, sessionID);
        return this.sessionIDs.add(sessionID);
    }
    
    public boolean removeAlgoSessionId(String sessionID, long algoID){
	this.algoSessionIDs.remove(algoID, sessionID);
        return this.sessionIDs.remove(sessionID);
    }
    
    public LinkedList<String> getSessionIds(){
        return this.sessionIDs;
    }
    
    public LinkedList<String> getNonAlgoSessionIds(){
        return this.nonAlgoSessionIDs;
    }
	
    public String getAlgoSessionID(long algoID) {
            return algoSessionIDs.get(algoID);
    }

    public long getMoney(){
        return money;
    }

    public void setMoney(Long money){
        this.money = money;
    }
    
    public void addMoney(Long money){
        this.money += money;
    }
    
    public void subtractMoney(Long money){
        this.money -= money;
    }

    public List<Order> getUserOrders(){
        return orders;
    }

    public void addOrder(Order order){
        orders.add(order);
    }

    public void removeOrder(Order order){
        orders.remove(order);
    }

    public void addPosition(String sym, Long qty){
        if(portfolio.containsKey(sym)){
           portfolio.put(sym, portfolio.get(sym)+qty);
        }
        else{
            portfolio.put(sym, qty);
        }
    }
    
    public void subtractPosition(String sym, Long qty){
        if(portfolio.containsKey(sym)){
           portfolio.put(sym, portfolio.get(sym)-qty);
        }
        else{
            portfolio.put(sym, -qty);
        }
    }
    
    public void changePortfolio(String symbol, long quantity){
        portfolio.put(symbol, quantity);
    }

    public TreeMap<String, Long> getPortfolio(){
        return portfolio;
    }
    
    public String getPortfolioSecurities(){
        return portfolio.keySet().toString();
    }
   
    public long getSecurityPosition(String sym){
         if(portfolio.containsKey(sym)){
             return portfolio.get(sym);
         }
         else{
             return 0;
         }
    }

}
