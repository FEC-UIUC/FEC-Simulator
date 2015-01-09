package Simulator;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.LinkedList;

import javax.websocket.OnOpen;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnError;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/server")
public class Server {

    private static DataFeed dataFeed;
    private static HashMap<String, Session> sessions = new HashMap<String, Session>();
    private static Exchange exchange = new ExchangeComplex();

    FileOutputStream filestream = null;
    File uploadedFile = null;
    final static File filePath = new File("C:\\Users\\Greg Pastorek\\Documents\\NetBeansProjects\\Simulator\\algos");

    
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
            try {
                e.getValue().getBasicRemote().sendText(msg);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    
    public void sendToUser(String msg, String userId) {
        Session session = sessions.get(userId);
        try {
            session.getBasicRemote().sendText(msg);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    
    public void sendToUser(String msg, Session session) {
        try {
            session.getBasicRemote().sendText(msg);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    
    @OnMessage
    public void processUpload(ByteBuffer msg, boolean last, Session session) {
        while (msg.hasRemaining()) {
            try {
                filestream.write(msg.get());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("Message from " + session.getId() + ": " + message);
        HashMap<String, String> message_map = MessageFormatter.parse(message);
        String msgType = message_map.get("message_type");
        if (msgType.equals("message")) {
            broadcastChatMessage(message_map);
        } 
        else if (msgType.equals("admin")) {
            handleAdmin(message_map, session.getId());
        } 
        else if (msgType.equals("order")) {
            handleOrder(message_map, session.getId());
        } 
        else if (msgType.equals("cancel")) {
            handleCancel(message_map, session.getId(), session);
        } 
        else if (msgType.equals("new_user")) {
            handleNewUser(message_map, session.getId(), session);
        }
        else if (msgType.equals("new_algo")) {
            handleNewAlgo(message_map, session.getId(), session);
        }
        else if (msgType.equals("remove_algo")) {
            handleRemoveAlgo(message_map, session.getId(), session);
        }
        else if (msgType.equals("algo-file")) {
            handleUploadFile(message_map, session.getId());
        } 
        else if (msgType.equals("algo-command")) {
            handleAlgoCommand(message_map, session.getId());
        }
    }

    
    @OnClose
    public void onClose(Session session) {
        System.out.println("Session " + session.getId() + " has ended");
        sessions.remove(session.getId());
    }

    
    @OnError
    public void error(Session session, Throwable t) {
        t.printStackTrace();
    }
    
    
    private void broadcastChatMessage(HashMap<String, String> message_map){
        String tosend = MessageFormatter.format(message_map);
        sendToAll(tosend);
    }

    
    private void handleAdmin(HashMap<String, String> message_map, String userID) {
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
        } else if (message_map.get("command").equals("boot")) {
            bootUser(message_map.get("userID"));
        }
    }

    
    private void bootUser(String userID) {
        if (userID == null) {
            return;
        }
        Session session_to_boot = sessions.get(userID);
        exchange.removeUser(userID);
        if (session_to_boot != null) {
            try {
                sendToUser("message_type=message|message=You have been booted.", session_to_boot);
                session_to_boot.close();
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    
    private void handleOrder(HashMap<String, String> message_map, String userID) {
        String symbol = message_map.get("symbol");
        long price = Long.parseLong(message_map.get("price"));
        long qty = Long.parseLong(message_map.get("quantity"));
        int side = Integer.parseInt(message_map.get("side"));
        int order_type = Integer.parseInt(message_map.get("order_type"));
        long orderID = Long.parseLong(message_map.get("orderID"));
        
        LinkedList<HashMap<String, String>> resps = exchange.placeOrder(orderID, userID, symbol, price, qty, side, order_type);
        
        for (HashMap<String, String> resp : resps) {
            String respString = MessageFormatter.format(resp);
            sendToUser(respString, resp.get("userID"));
        }
    }

    
    private void handleCancel(HashMap<String, String> message_map, String userID, Session session) {
        long orderID = Long.parseLong(message_map.get("orderID"));
        LinkedList<HashMap<String, String>> resps = exchange.cancelOrder(userID, orderID);
        for (HashMap<String, String> resp : resps) {
            String respString = MessageFormatter.format(resp);
            sendToUser(respString, resp.get("userID"));
        }
        
    }

    
    private void handleNewUser(HashMap<String, String> message_map, String userID, Session session) {
        String username = message_map.get("username");
        
        LinkedList<HashMap<String, String>> resps = exchange.addUser(userID, username);
        
        for (HashMap<String, String> resp : resps) {
            String respString = MessageFormatter.format(resp);
            sendToUser(respString, session);
        }
    }
    
    
    private void handleNewAlgo(HashMap<String, String> message_map, String userID, Session session) {
        String username = message_map.get("username");
        exchange.addAlgoToUser(userID, username);
    }
    
    
    private void handleRemoveAlgo(HashMap<String, String> message_map, String userID, Session session) {
        String username = message_map.get("username");
        exchange.removeAlgoFromUser(userID, username);
    }

    
    private void handleUploadFile(HashMap<String, String> message_map, String userID) {
        if (!message_map.get("command").equals("end")) {
            String fileName = message_map.get("filename");
            uploadedFile = new File(new File(filePath, userID), fileName);
            uploadedFile.getParentFile().mkdirs();
            try {
                uploadedFile.createNewFile();
                filestream = new FileOutputStream(uploadedFile);
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                filestream.flush();
                filestream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    
    private void handleAlgoCommand(HashMap<String, String> message_map, String userID) {
        String command  = message_map.get("command");
        if(command.equals("run")){
            try {
                ArrayList<String> securities = new ObjectMapper().readValue(message_map.get("securities"), ArrayList.class);
                HashMap<String,String> parameters = new ObjectMapper().readValue(message_map.get("parameters"), HashMap.class);
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if (command.equals("stop")) {
            
        }
        else if (command.equals("remove")) {
            
        }
    }
   

}
