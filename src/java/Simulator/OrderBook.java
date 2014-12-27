package Simulator;
import java.util.HashMap;

/**
 * Orderbook for one security
 * 
 */

public class OrderBook{
	private final String security;
	public OrderBook(String security){
            this.security = security;
            HashMap<Double, Integer> buySide = new HashMap<Double, Integer>();
            HashMap<Double, Integer> sellSide = new HashMap<Double, Integer>();
	}

	public String getSecurity(){
		return this.security;
	}

}