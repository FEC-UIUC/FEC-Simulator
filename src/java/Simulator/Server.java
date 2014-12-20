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
            session.getBasicRemote().sendText("type=message|message=Connection Established");
            sessions.put(session.getId(), session);
            System.out.println(session.getId() + " has opened a connection");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void sendToAll(String msg) {
        
        System.out.println(sessions.size());
        
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
        String[] msgList = message.split("\\|");
        if (msgList[0].equals("message")) 
        {
            String tosend = "type=message|message=" + session.getId() + "|" + msgList[1];
            sendToAll(tosend);
        } 
        else if (msgList[0].equals("admin")) 
        {
            handleAdmin(msgList);
        }
        else if (msgList[0].equals("order"))
        {
            String resp = handleOrder(msgList);
            sendToUser(resp, session);
        }
        else if (msgList[0].equals("cancel"))
        {
            String resp = handleCancel(msgList);
            sendToUser(resp, session);
        }
        else if (msgList[0].equals("new_user"))
        {
            String resp = handleNewUser(msgList, session.getId());
            sendToUser(resp, session);
        }
        
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("Session " + session.getId() + " has ended");
        sessions.remove(session.getId());
    }
    
    
    private void handleAdmin(String[] msgList){
        if (msgList.length < 2) {
            return;
        }
        if (msgList[1].equals("start")) {
            try {
                exchange.addSecurity("C:\\Users\\Greg Pastorek\\Documents\\NetBeansProjects\\Simulator\\src\\java\\Simulator\\marketData.txt", "GOOG");
            } catch (Exception ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
            dataFeed = new DataFeed(this, exchange);
            dataFeed.start();
        } else if (msgList[1].equals("stop") && dataFeed != null) {
            dataFeed.end();
        } else if (msgList[1].equals("boot")){
            if(msgList.length >= 3){
                bootUser(msgList[2]);                
            }
        }
    }
    
    private void bootUser(String userID){
        Session session_to_boot = sessions.get(userID);
        exchange.removeUser(userID);
        if(session_to_boot != null){
            try {
                sendToUser("type=message|message=You have been booted.", session_to_boot);
                session_to_boot.close();
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    
    private String handleOrder(String[] msgList) {
        String userID = msgList[1];
        String symbol = msgList[2];
        long price = Long.parseLong(msgList[3]);
        long amount = Long.parseLong(msgList[4]);
        int side = Integer.parseInt(msgList[5]);
        int type = Integer.parseInt(msgList[6]);
        long orderID = Long.parseLong(msgList[7]);
        HashMap<String, String> resp = exchange.placeOrder(userID, symbol, price, amount, side, type, orderID);
        return MessageFormatter.format(resp);
    }
    
    private String handleCancel(String[] msgList){
        String userID = msgList[1];
        long orderID = Long.parseLong(msgList[2]);
        HashMap<String, String> resp = exchange.cancelOrder(userID, orderID);
        return MessageFormatter.format(resp);
    }
    
    private String handleNewUser(String[] msgList, String userID) {
        String username = msgList[1];
        HashMap<String, String> resp = exchange.addUser(userID, username);  /*TODO - add user, return new user message if new*/
        return MessageFormatter.format(resp);
    }
    
}
