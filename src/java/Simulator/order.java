package Simulator;

import java.util.HashMap;
import java.util.Map;


/**
 *
 * @author Thibaut Xiong
 */

/***

order|userID|sym|price|qty|side|type|orderID

Note: 
side (0 - buying, 1 - selling)
type (0 - market order, 1 - limit order)|

***/

public class Order{

        private long orderID;
	private String userID;
	private String sym;
	private long price;
	private long qty;
	private int side;
	private int order_type;

	public Order(long orderID, String userID, String sym, long price, long qty, int side, int order_type){
                this.orderID = orderID;
		this.userID = userID;
		this.sym = sym;
		this.price = price;
		this.qty = qty;
		this.side = side;
		this.order_type = order_type;
	}
        
        public void setOrderID(long orderID){
		this.orderID = orderID;
	}

	public void setUserID(String userID){
		this.userID = userID;
	}

	public void setSym(String sym){
		this.sym = sym;
	}

	public void setPrice(Long price){
		this.price = price;
	}

	public void setQty(Long qty){
		this.qty = qty;
	}

	public void setSide(int side){
		this.side = side;
	}

	public void setType(int order_type){
		this.order_type = order_type;
	}
        
        public long getOrderID(){
		return orderID;
	}

	public String getUserID(){
		return userID;
	}

	public String getSym(){
		return sym;
	}

	public long getPrice(){
		return price;
	}

	public long getQty(){
		return qty;
	}

	public int getSide(){
		return side;
	}

	public int getType(){
		return order_type;
	}

}

