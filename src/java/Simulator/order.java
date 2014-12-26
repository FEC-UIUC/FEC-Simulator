package java.Simulator;


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

public class Order(){

	private String messagetype;
	private String userID;
	private String sym;
	private long price;
	private long qty;
	private int side;
	private int type;

	public Order(String userID, String sym, long price, long qty, int side, int type){
		this.messagetype = 'order';
		this.userID = userID;
		this.sym = sym;
		this.price = price;
		this.qty = qty;
		this.side = side;
		pthis.type = type;
	}

	public String toString(){
		String message = "";
		message += 'msg=' + this.messagetype + '|';
		message += 'userID='+ this.userID + '|';
		message += 'sym='+this.sym + '|';
		message += 'price=' + this.price.toString() + '|';
		message += 'quantity=' + this.qty.toString() + '|';
		message += 'side=' + this.side.toString() + '|';
		message += 'type=' + this.type.toString() + '|';
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

	public void setType(int type){
		this.type = type;
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
		return type;
	}

}

