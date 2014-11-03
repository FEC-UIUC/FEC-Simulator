package Simulator;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
 
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
 

@ServerEndpoint("/server") 
public class Server {
    
    DataFeed dataFeed  = new DataFeed();
    

    @OnOpen
    public void onOpen(Session session){
        System.out.println(session.getId() + " has opened a connection"); 
        try {
            session.getBasicRemote().sendText("message|Connection Established");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
 

    @OnMessage
    public void onMessage(String message, Session session){
        System.out.println("Message from " + session.getId() + ": " + message);
        try {
            System.out.println("GOT HERE");
            String[] msgList = message.split("\\|");
            System.out.println("Message type: " + msgList[0]);
            if(msgList[0].equals("message")) {
                System.out.println("In Messages");
                session.getBasicRemote().sendText(message);
            } 
            else if (msgList[0].equals("admin")){
                System.out.println("in admin");
                if (msgList.length < 2){
                   return;   
                }
                System.out.println("trynna run");
                System.out.println(msgList[1]);
                if (msgList[1].equals("start")) {
                   dataFeed  = new DataFeed();
                   System.out.println("starting");
                   dataFeed.setSession(session);
                   dataFeed.start();
                } 
                else if (msgList[1].equals("stop") && dataFeed != null){
                   dataFeed.end();
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
 

    @OnClose
    public void onClose(Session session){
        System.out.println("Session " +session.getId()+" has ended");
    }
}