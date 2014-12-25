/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Simulator;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.Session;

/**
 *
 * @author Greg Pastorek
 */
public class DataFeed extends Thread {

    Session session;
    Server parent;
    Exchange exchange;
    boolean stop_flag = false;
    
    DataFeed(Server parent_, Exchange exchange_){
        parent = parent_;
        exchange = exchange_;
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
        while (!stop_flag) {
            long t0 = System.currentTimeMillis();
            try {
                exchange.nextTick();
            } catch (Exception ex) {
                Logger.getLogger(DataFeed.class.getName()).log(Level.SEVERE, null, ex);
            }
            for(String sym : exchange.getSymList()){
                try {
                    HashMap<String, String> snapshot = exchange.snapShot(sym);
                    parent.sendToAll(MessageFormatter.format(snapshot));
                } catch (Exception ex) {
                    Logger.getLogger(DataFeed.class.getName()).log(Level.SEVERE, null, ex);
                }
                
            }
            long t1 = System.currentTimeMillis();
            try{
                if(t1-t0 < 2000){
                    Thread.sleep(2000 - (t1 - t0));
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            if(stop_flag){
                return;
            }
        }
    }
}
