package java.Simulator;

public class ExchangeReceiver(){

/**
 *
 * @author Thibaut Xiong
 */

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
				    //have the order object, process it on the exchange side
		}

	}

	public Order parseOrderMessage(String ordermessage){
		HashMap<String, String> ordermap = MessageFormatter.parse(ordermessage);
		Order order = new Order(ordermap.get(userID), ordermap.get(sym), ordermap.get(price), ordermap.get(qty), ordermap.get(side), ordermap.get(type), ordermap.get(orderID));
		return order;
	}


}