package java.Simulator;

public class exchangereceiver(){

/**
 *
 * @author Thibaut Xiong
 */


	/***
	public void OnMessage(Order order){
		processOrder(order.Price, order.Qty, order.Account);
	}
	***/

	public void OnMessage(String message){
		// get the order type
		String messagetype = ''; 
		for(int i = 0; i < message.length(); i++){
			if (message[i] == '|'){
				break;
			}
			else{
				messagetype += message[i];
			}
		}

		switch (messagetype){
			case 1: messagetype == "order";
				    Order order = parseOrderMessage(message);
				    processOrder(order);
		}

	}

	// order|userID|sym|price|qty|side|type|orderID
	public Order parseOrderMessage(String ordermessage){
		String field = "";
		String userID = "";
		String sym = "";
		Long price;
		Long qty;
		int side;
		int type;
		String orderID = "";

		int delimiter = 0;
		for(int i = 0; i < ordermessage.length(); i++){
			if (ordermessage[i] == '|'){
				delimiter ++;
				if (delimiter == 1){
					// do nothing
				}
				else if (delimiter == 2){
					userID = field;
				}
				else if (delimiter == 3){
					sym = field;
				}
				else if (delimiter == 4){
					price = Long.parseLong(field);
				}
				else if (delimiter == 5){
					qty = Long.parseLong(field);
				}
				else if (delimiter == 6){
					side = Integer.toString(field);
				}
				else if (delimiter == 7){
					type = Integer.toString(field);
				}
				field = "";
			}

			else{
				field += ordermessage[i];
				if (i == ordermessage.length()-1){
					orderID = field;
				}
			}
		}

		Order order = new Order(userID, sym, price, qty, side, type, orderID);
		return order;
	}

}