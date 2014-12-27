/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Simulator;
import java.util.HashMap;

/**
 *
 * @author thibautxiong
 */
public class TestMessages {
    public static void main(String[] args){
        testOrder();
    }
    
    public static void testOrder(){
        Order order = new Order("user123", "AAPL", 100, 100, 1, 1);
        String ordermessage = order.toString();
        System.out.println(ordermessage); // message_type=order|userID=user123|sym=AAPL|price=100|quantity=100|side=1|order_type=1|
        HashMap<String, String>  ordermap = MessageFormatter.parse(ordermessage);
        System.out.println(ordermap.get("message_type"));
        System.out.println(ordermap.get("userID"));
        System.out.println(ordermap.get("sym"));
        System.out.println(ordermap.get("price"));
        System.out.println(ordermap.get("quantity"));
        System.out.println(ordermap.get("side"));
        System.out.println(ordermap.get("order_type"));
    }
}
