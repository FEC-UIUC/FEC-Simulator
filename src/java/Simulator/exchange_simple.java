package Simulator;

import Simulator.Security;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
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
                result.put("type", "snapshot");
                result.put("symbol", symbol);
                result.put("bid_price", Long.toString(sec.bid_price));
                result.put("ask_price", Long.toString(sec.ask_price));
                result.put("bid_qty", Long.toString(sec.bid_qty));
                result.put("ask_qty", Long.toString(sec.ask_qty));
                return result;
            }
            else{
                throw new Exception(symbol + " does not exist");
            }
	}
	

        public HashMap<String, String> placeOrder(String userID, String sym, long price, long amount, int side, int type, long orderID)
        {
            HashMap<String, String> result = new HashMap<String, String>();
            if(!securityExists(sym)){
                result.put("type", "order");
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
            
                if(side == 1 && price >= sec.bid_price)
                {
                    fill_qty = -1*Math.min(amount, sec.bid_qty);
                    fill_price = sec.bid_price;
                }
                else if (side == 0 && price <= sec.ask_price)
                {
                    fill_qty = Math.min(amount, sec.ask_qty);
                    fill_price = sec.ask_price;
                }

                String action = getActionString(fill_qty, amount);

                String money = Long.toString(-1*fill_qty*fill_price);
                
                result.put("type", "order");
                result.put("orderID", Long.toString(orderID));
                result.put("action", action);
                result.put("filled", Long.toString(fill_qty));
                result.put("remaining", "0");
                result.put("money", money);
                
            }
            
            return result;
                    
        }
        
        public HashMap<String, String> cancelOrder(String userID, long orderID){
            HashMap<String, String> result = new HashMap<String, String>();
            result.put("type", "cancel");
            result.put("orderID", Long.toString(orderID));
            result.put("success", "0");
            return result;
        }
        
        public long getUserMoney(String userID){
            return 0;
        }
        
        public HashMap<String, String> addUser(String username, String userID){
            /* TODO:
                check if user already exists
                if already exists:
                    -send series of messages of all active orders
                    -return new_user message with appropriate userID and money
                if new user:
                    -add user to list of active users, make structure
                    -return new_user message with appropriate userID and money
            */
            HashMap<String, String> resp = new HashMap<String, String>();
            resp.put("type", "new_user");
            resp.put("userID", userID);
            resp.put("money", "0");
            return resp;
        }
        
        public boolean removeUser(String userID){
            return true;
        }
        
        public List<String> getUserOrders(String userID) {
            return new ArrayList<String>();
        }
        
        
        private String getActionString(long fill_qty, long amount){
            if(fill_qty == amount){
                return "0";
            } else if (Math.abs(fill_qty) > 0){
                return "1";
            } else {
                return "3";
            }
        }

}
