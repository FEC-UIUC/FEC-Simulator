package Simulator;

import Simulator.Security;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
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
                temp.bid_quant = Integer.parseInt(line_arr[2]);
                temp.ask_quant = Integer.parseInt(line_arr[3]);
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
                    sec.bid_quant = Integer.parseInt(line_arr[2]);
                    sec.ask_quant = Integer.parseInt(line_arr[3]);
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
	
	public long getAskQuantity(String symbol){
		if(securityExists(symbol)){
			return securityMap.get(symbol).ask_quant;
		}
		return 0;
	}
	
	public long getBidQuantity(String symbol){	
		if(securityExists(symbol)){
			return securityMap.get(symbol).bid_quant;
		}
		return 0;
	}
	
	public long getBidPrice(String symbol){	
		if(securityExists(symbol)){
			return securityMap.get(symbol).bid_price;
		}
		return 0;
	}
	
	public long getAskPrice(String symbol){
		
		if(securityExists(symbol)){
			return securityMap.get(symbol).ask_price;
		}
		return 0;
	}
	
	public String snapShot(String symbol) throws Exception {
            if(securityExists(symbol)){
                Security sec = securityMap.get(symbol);
                return sec.getSnapshotString();
            }
            else{
                throw new Exception(symbol + " does not exist");
            }
	}
	
	public void updateBidPrice(String symbol, long value){
		
		if(!securityExists(symbol))
			return;
	}
	
	public void updateAskPrice(String symbol, long value){
		
		if(!securityExists(symbol))
			return;
	}
        
	public void updateBidQuant(String symbol, long value){
		
		if(!securityExists(symbol))
			return;
	}
        
	public void updateAskQuant(String symbol, long valueAdd){
		
		if(!securityExists(symbol))
			return;

		long value=securityMap.get(symbol).ask_quant;
		securityMap.get(symbol).ask_quant= value + valueAdd;
	}

        public String placeOrder(long userID, String sym, long price, long amount, int side, int type, long orderID)
        {
            long fill_qty = 0;
            long fill_price = 0;
            if(!securityExists(sym)){
                return "order|" + Long.toString(orderID) + "|3|0|0|0";
            }
            if(side == 1 && price >= getBidPrice(sym))
            {
                fill_qty = -1*Math.min(amount, getBidQuantity(sym));
                fill_price = getBidPrice(sym);
            }
            else if (side == 0 && price <= getAskPrice(sym))
            {
                fill_qty = Math.min(amount, getAskQuantity(sym));
                fill_price = getAskPrice(sym);
            }
            
            String action;
            if(fill_qty == amount){
                action = "0";
            } else if (Math.abs(fill_qty) > 0){
                action = "1";
            } else {
                action = "3";
            }
            
            String money = Long.toString(-1*fill_qty*fill_price);
            
            return "order|" + Long.toString(orderID) + "|" + action + "|" + Long.toString(fill_qty) + "|0|" + money;
                    
        }
        
        public String cancelOrder(long userID, long orderID){
            return "cancel|" + Long.toString(orderID) + "|0";
        }
	
	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		//Scanner marketData= new Scanner(new File("marketData.txt"));
		
		//mapSecurity(marketData);
		
		/*while(marketData.hasNext()){
			String line = marketData.nextLine();
			String[] line_arr = line.split(";");
			String sym = line_arr[0];
			long price = Integer.parseInt(line_arr[1]);
			long quantity = Integer.parseInt(line_arr[2]);
			
		}*/
		
	}

}
