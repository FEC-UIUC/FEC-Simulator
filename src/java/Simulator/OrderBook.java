package java.Simulator;

/**
 * Orderbook for one security
 * 
 */

public class OrderBook(){
	private final String security
	public OrderBook(String security){
		this.security = security;
		HashMap<double, int> buySide = new HashMap<double, int>;
		HashMap<double, int> sellSide = new HashMap<double, int>;
	}

	public getSecurity(){
		return security;
	}

}