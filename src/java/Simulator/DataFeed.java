/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Simulator;

import java.io.IOException;
import javax.websocket.Session;

/**
 *
 * @author Greg Pastorek
 */
public class DataFeed extends Thread {

    Session session;
    Server parent;
    boolean stop_flag = false;
    
    DataFeed(Server parent_){
        parent = parent_;
    }
    
    public void setSession(Session session_){
        session = session_;
    }
    
    public void end(){
        stop_flag = true;
    }
    
    public boolean isRunning(){
        return !stop_flag;
    }

    public void run() {
        stop_flag = false;
        double val = 1;
        while (true) {
            long millis = System.currentTimeMillis();
            val += 2*Math.random() - 1;
            String msg = String.valueOf(val);
            try{
                session.getBasicRemote().sendText("data|" + msg);
                parent.sendToAll("data|" + msg);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            try{
                Thread.sleep(1000 - millis % 1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            if(stop_flag){
                return;
            }
        }
    }
}
