package Simulator;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/server")
public class Server {

    DataFeed dataFeed;
    HashMap<String, Session> sessions = new HashMap<String, Session>();

    @OnOpen
    public void onOpen(Session session) {
        System.out.println(session.getId() + " has opened a connection");
        try {
            session.getBasicRemote().sendText("message|Connection Established");
            sessions.put(session.getId(), session);
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

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("Message from " + session.getId() + ": " + message);

        String[] msgList = message.split("\\|");
        if (msgList[0].equals("message")) 
        {
            String tosend = "message|" + session.getId() + "|" + msgList[1];
            sendToAll(tosend);
        } 
        else if (msgList[0].equals("admin")) 
        {
            if (msgList.length < 2) {
                return;
            }
            if (msgList[1].equals("start")) {
                dataFeed = new DataFeed(this);
                dataFeed.setSession(session);
                dataFeed.start();
            } else if (msgList[1].equals("stop") && dataFeed != null) {
                dataFeed.end();
            }
        }
        
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("Session " + session.getId() + " has ended");
        sessions.remove(session.getId());
    }
}
