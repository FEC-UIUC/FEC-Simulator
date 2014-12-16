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
	
	public Order(String userID, String sym, long price, long qty, int side, int type){
		messagetype = 'order';
		userID = userID;
		sym = sym;
		price = price;
		qty = qty;
		side = side;
		type = type;
	}

	public void sendOrder(Sender sender){
		String message = "";
		message += this.messagetype + '|';
		message += this.userID + '|';
		message += this.sym + '|';
		message += this.price.toString() + '|';
		message += this.qty.toString() + '|';
		message += this.side.toString() + '|';
		message += this.type.toString() + '|';

		sender.send(message);
		return;

	}

	public void setuserID(String userID){
		this.userID = userID;
	}

	public void setSym(String sym){
		this.sym = sym;
	}

	public void setPrice(Long price){
		this.price = price;
	}

	public void setqty(Long qty){
		this.qty = qty;
	}

	public void setSide(int side){
		this.side = side;
	}

	public void setType(int type){
		this.type = type;
	}

}

