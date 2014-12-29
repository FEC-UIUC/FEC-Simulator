package Simulator;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/server")
public class Server {

    private static DataFeed dataFeed;
    private static HashMap<String, Session> sessions = new HashMap<String, Session>();
    private static Exchange exchange = new exchange_simple();
    
    
    @OnOpen
    public void onOpen(Session session) {
        try {
            session.getBasicRemote().sendText("message_type=message|from=Server|message=Connection Established");
            sessions.put(session.getId(), session);
            System.out.println(session.getId() + " has opened a connection");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void sendToAll(String msg) {
        
        for (Entry<String, Session> e : sessions.entrySet()) {
            try{
                e.getValue().getBasicRemote().sendText(msg);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public void sendToUser(String msg, String userId){
        Session session = sessions.get(userId);
        try{
            session.getBasicRemote().sendText(msg);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public void sendToUser(String msg, Session session){
        try{
            session.getBasicRemote().sendText(msg);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("Message from " + session.getId() + ": " + message);
        HashMap<String, String> message_map = MessageFormatter.parse(message);
        String msgType = message_map.get("message_type");
        if (msgType.equals("message")) 
        {
            String tosend = "message_type=message|from=" + message_map.get("from") + "|message=" + message_map.get("message");
            sendToAll(tosend);
        } 
        else if (msgType.equals("admin")) 
        {
            handleAdmin(message_map, session.getId());
        }
        else if (msgType.equals("order"))
        {
            String resp = handleOrder(message_map, session.getId());
            sendToUser(resp, session);
        }
        else if (msgType.equals("cancel"))
        {
            String resp = handleCancel(message_map, session.getId());
            sendToUser(resp, session);
        }
        else if (msgType.equals("new_user"))
        {
            String resp = handleNewUser(message_map, session.getId());
            sendToUser(resp, session);
        }
        
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("Session " + session.getId() + " has ended");
        sessions.remove(session.getId());
    }
    
    
    private void handleAdmin(HashMap<String, String> message_map, String userID){
        //TODO - check if userID is the adminID
        if (message_map.get("command").equals("start")) {
            try {
                exchange.addSecurity("C:\\Users\\Greg Pastorek\\Documents\\NetBeansProjects\\Simulator\\src\\java\\Simulator\\marketData.txt", "GOOG");
                exchange.addSecurity("C:\\Users\\Greg Pastorek\\Documents\\NetBeansProjects\\Simulator\\src\\java\\Simulator\\marketData2.txt", "AAPL");
                exchange.addSecurity("C:\\Users\\Greg Pastorek\\Documents\\NetBeansProjects\\Simulator\\src\\java\\Simulator\\marketData3.txt", "MSFT");
                exchange.addSecurity("C:\\Users\\Greg Pastorek\\Documents\\NetBeansProjects\\Simulator\\src\\java\\Simulator\\marketData4.txt", "FB");
                exchange.addSecurity("C:\\Users\\Greg Pastorek\\Documents\\NetBeansProjects\\Simulator\\src\\java\\Simulator\\marketData5.txt", "AMZN");
            } catch (Exception ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
            dataFeed = new DataFeed(this, exchange);
            dataFeed.start();
        } else if (message_map.get("command").equals("stop") && dataFeed != null) {
            dataFeed.end();
        } else if (message_map.get("command").equals("boot")){
            bootUser(message_map.get("userID"));                
        }
    }
    
    private void bootUser(String userID){
        if(userID == null) {
            return;
        }
        Session session_to_boot = sessions.get(userID);
        exchange.removeUser(userID);
        if(session_to_boot != null){
            try {
                sendToUser("message_type=message|message=You have been booted.", session_to_boot);
                session_to_boot.close();
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    
    private String handleOrder(HashMap<String, String> message_map, String userID) {
        String symbol = message_map.get("symbol");
        long price = Long.parseLong(message_map.get("price"));
        long qty = Long.parseLong(message_map.get("quantity"));
        int side = Integer.parseInt(message_map.get("side"));
        int order_type = Integer.parseInt(message_map.get("order_type"));
        long orderID = Long.parseLong(message_map.get("orderID"));
        HashMap<String, String> resp = exchange.placeOrder(userID, symbol, price, qty, side, order_type, orderID);
        return MessageFormatter.format(resp);
    }
    
    private String handleCancel(HashMap<String, String> message_map, String userID){
        long orderID = Long.parseLong(message_map.get("orderID"));
        HashMap<String, String> resp = exchange.cancelOrder(userID, orderID);
        return MessageFormatter.format(resp);
    }
    
    private String handleNewUser(HashMap<String, String> message_map, String userID) {
        String username = message_map.get("username");
        HashMap<String, String> resp = exchange.addUser(userID, username);  /*TODO - add user, return new user message if new*/
        return MessageFormatter.format(resp);
    }
    
}
