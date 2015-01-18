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
    
    String userID;
    List<Order> orders = new LinkedList<>();
    TreeMap<String, Long> portfolio = new TreeMap<>();
    long money; 
    
    private HashMap<String, File> algoFiles = new HashMap<>();

    public User(String userID){
        this.userID = userID;
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
