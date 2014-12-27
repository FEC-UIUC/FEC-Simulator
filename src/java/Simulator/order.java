package Simulator;


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

	private String message_type;
	private String userID;
	private String sym;
	private long price;
	private long qty;
	private int side;
	private int order_type;

	public Order(String userID, String sym, long price, long qty, int side, int order_type){
		this.message_type = "order";
		this.userID = userID;
		this.sym = sym;
		this.price = price;
		this.qty = qty;
		this.side = side;
		this.order_type = order_type;
	}

	public String toString(){
		String message = "";
		message += "message_type=" + this.message_type + "|";
		message += "userID="+ this.userID + "|";
		message += "sym="+this.sym + "|";
		message += "price=" + String.valueOf(this.price) + "|";
		message += "quantity=" + String.valueOf(this.qty) + "|";
		message += "side=" + String.valueOf(this.side) + "|";
		message += "order_type=" + String.valueOf(this.order_type) + "|";
                
                return message;
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

	public String getUserID(){
		return userID;
	}

	public String getSym(){
		return sym;
	}

	public Long getPrice(){
		return price;
	}

	public Long getQty(){
		return qty;
	}

	public int getSide(){
		return side;
	}

	public int getType(){
		return order_type;
	}

}

