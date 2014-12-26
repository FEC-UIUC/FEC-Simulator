package java.Simulator;

public class ExchangeReceiver(){

/**
 * Receives messages from the client
 * @author Thibaut Xiong
 */

	public void OnMessage(String message){
		// get the order type
		HashMap<String, String> ordermap = MessageFormatter.parse(message);
		messagetype = message.get(messagetype);

		switch (messagetype){
			case 1: messagetype == "order";
				    Order order = buildOrder(message);
				    //have the order object, process it on the exchange side
		}

	}

	public Order buildORder(HashMap<String, String> ordermap){
		Order order = new Order(ordermap.get(userID), ordermap.get(sym), ordermap.get(price), ordermap.get(qty), ordermap.get(side), ordermap.get(type), ordermap.get(orderID));
		return order;
	}


}