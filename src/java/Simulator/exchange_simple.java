package Simulator;

import Simulator.Security;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class exchange_simple extends Exchange {
	
	HashMap<String,Security> securityMap = new HashMap<String,Security>();
	HashMap<String,BufferedReader> dataFeeds = new HashMap<String,BufferedReader>();
        
	public boolean addSecurity(String fname, String symbol) throws FileNotFoundException, Exception{
		
                System.out.println(fname);
		BufferedReader marketData= new BufferedReader(new FileReader(fname));
                
                String line = marketData.readLine();
                if(line == null){
                    return false;
                }
                String[] line_arr = line.split(";");
                Security temp = new Security();
                if(line_arr.length != 4){
                    throw new Exception("Invalid data file");
                }
                temp.sym = symbol;
                temp.bid_price = Integer.parseInt(line_arr[0]);
                temp.ask_price = Integer.parseInt(line_arr[1]);
                temp.bid_qty = Integer.parseInt(line_arr[2]);
                temp.ask_qty = Integer.parseInt(line_arr[3]);
                securityMap.put(temp.sym, temp);
                dataFeeds.put(temp.sym, marketData);
                return true;
	}
        
        
        public Set<String> getSymList() {
            return securityMap.keySet();
        }
        
        
        public void nextTick() throws Exception {
            for(String sym : dataFeeds.keySet()){
                BufferedReader marketData = dataFeeds.get(sym);
                Security sec = securityMap.get(sym);
                String line = marketData.readLine();
                if (line != null){
                    String[] line_arr = line.split(";");
                    if(line_arr.length != 4){
                        throw new Exception("Invalid data file, length is " + Integer.toString(line_arr.length));
                    }
                    sec.bid_price = Integer.parseInt(line_arr[0]);
                    sec.ask_price = Integer.parseInt(line_arr[1]);
                    sec.bid_qty = Integer.parseInt(line_arr[2]);
                    sec.ask_qty = Integer.parseInt(line_arr[3]);
                } else {
                    dataFeeds.remove(sym);
                    securityMap.remove(sym);
                }
            }
        }
        
	
	//check if security is mapped already
	public boolean securityExists(String symbol){		
		if(securityMap.containsKey(symbol)){
			return true;
		}
		return false;
	}
	
	
	public HashMap<String, String> snapShot(String symbol) throws Exception {
            if(securityExists(symbol)){
                Security sec = securityMap.get(symbol);
                HashMap<String, String> result = new HashMap<String, String>();
                result.put("message_type", "snapshot");
                result.put("symbol", symbol);
                result.put("bid_price", Long.toString(sec.bid_price));
                result.put("ask_price", Long.toString(sec.ask_price));
                result.put("last_price", Long.toString((sec.ask_price+sec.bid_price)/2));
                result.put("bid_qty", Long.toString(sec.bid_qty));
                result.put("ask_qty", Long.toString(sec.ask_qty));
                return result;
            }
            else{
                throw new Exception(symbol + " does not exist");
            }
	}
	

        public LinkedList<HashMap<String, String>> placeOrder(long orderID, String userID, String sym, long price, long qty, int side, int order_type)
        {
            HashMap<String, String> result = new HashMap<String, String>();
            if(!securityExists(sym)){
                result.put("message_type", "order");
                result.put("orderID", Long.toString(orderID));
                result.put("action", "3");
                result.put("filled", "0");
                result.put("remaining", "0");
                result.put("money", "0");
            } 
            else {
                long fill_qty = 0;
                long fill_price = 0;
                
                Security sec = securityMap.get(sym);
            
                if(side == 1 && price <= sec.bid_price)
                {
                    fill_qty = -1*Math.min(qty, sec.bid_qty);
                    fill_price = sec.bid_price;
                }
                else if (side == 0 && price >= sec.ask_price)
                {
                    fill_qty = Math.min(qty, sec.ask_qty);
                    fill_price = sec.ask_price;
                }

                String action = getActionString(fill_qty, qty);

                String money = Long.toString(-1*fill_qty*fill_price);
                
                result.put("message_type", "order");
                result.put("orderID", Long.toString(orderID));
                result.put("action", action);
                result.put("filled", Long.toString(fill_qty));
                result.put("remaining", "0");
                result.put("money", money);
                
            }
            LinkedList<HashMap<String, String>> resp = new LinkedList<>();
            resp.add(result);
            return resp;
                    
        }
        
        public HashMap<String, String> cancelOrder(String userID, long orderID){
            HashMap<String, String> result = new HashMap<String, String>();
            result.put("message_type", "cancel");
            result.put("orderID", Long.toString(orderID));
            result.put("success", "0");
            return result;
        }
        
        public long getUserMoney(String userID){
            return 0;
        }
        
        public List<Order> getUserOrders(String userID){
            return null;
            /*if(users.containsKey(userID)){
                return users.get(userID).getUserOrders();
            }
            else{
                System.out.println(userID + " does not exist");
                return null;
            }*/
        }
        
        public LinkedList<HashMap<String, String>> addUser(String username, String userID){
            /* TODO:
                check if user already exists
                if already exists:
                    -send series of messages of all active orders
                    -return new_user message with appropriate money
                if new user:
                    -add user to list of active users, make structure
                    -return new_user message with appropriate userID and money
            */
            LinkedList<HashMap<String, String>> resp_list = new LinkedList<HashMap<String, String>>();
            HashMap<String, String> resp = new HashMap<String, String>();
            resp.put("message_type", "new_user");
            resp.put("money", "0");
            resp_list.add(resp);
            return resp_list;
        }
        
        public boolean removeUser(String userID){
            return true;
        }
        
        
        private String getActionString(long fill_qty, long amount){
            if(Math.abs(fill_qty) == amount){
                return "0";
            } else if (Math.abs(fill_qty) > 0){
                return "1";
            } else {
                return "3";
            }
        }

}
