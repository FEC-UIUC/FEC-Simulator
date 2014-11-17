package java.Simulator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class exchange_simple {
	
	static HashMap<String,security> securityMap= new HashMap<String,security>();
	
	
	public static void addSecurity(File marketDataText) throws FileNotFoundException{
		
		//String symName;
		//HashMap<String,security> securityMap= new HashMap<String,security>();
		
		Scanner marketData= new Scanner(new File("marketData.txt"));
		
		while(marketData.hasNext()){
			String line = marketData.nextLine();
			String[] line_arr = line.split(";");
			
			String sym = line_arr[0];
			long bid_price = Integer.parseInt(line_arr[1]);
			long ask_price = Integer.parseInt(line_arr[2]);
			
			long bid_quant = Integer.parseInt(line_arr[3]);
			long ask_quant = Integer.parseInt(line_arr[4]);
			
			security temp= new security();
			temp.sym=sym;
			temp.bid_price=bid_price;
			temp.ask_price=ask_price;
			temp.bid_quant=bid_quant;
			temp.ask_quant=ask_quant;
			
			securityMap.put(sym, temp);
			//security temp= new security(sym,bid_price,ask_price,bid_quant,ask_quant);
		}
	}
	
	//check if security is mapped already
	public boolean securityExists(String symbol){
		
		if(securityMap.containsKey(symbol)){
			return true;
		}
		return false;
	}
	
	public long getAskQuant(String symbol){
		
		if(securityExists(symbol)){
			return securityMap.get(symbol).ask_quant;
		}
		return 0;
	}
	
	public long getBidQuant(String symbol){
		
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
	
	public String snapShot(String symbol){
		
		if(securityExists(symbol)){
			String snapShot;
			snapShot = securityMap.get(symbol).sym + ";" + securityMap.get(symbol).bid_price+ ";" 
			+ securityMap.get(symbol).ask_price + ";" + securityMap.get(symbol).bid_quant +";"
			+ securityMap.get(symbol).ask_quant;
			return snapShot;
		}
		else{
		String tempNo="Security Doesnt Exist";
		return tempNo;
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
