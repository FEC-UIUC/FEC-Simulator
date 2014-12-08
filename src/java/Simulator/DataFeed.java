/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Simulator;

import java.io.IOException;
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
        while (true) {
            long millis = System.currentTimeMillis();
            try {
                exchange.nextTick();
            } catch (Exception ex) {
                Logger.getLogger(DataFeed.class.getName()).log(Level.SEVERE, null, ex);
            }
            for(String sym : exchange.getSymList()){
                String snapshot = "";
                try {
                    snapshot = exchange.snapShot(sym);
                } catch (Exception ex) {
                    Logger.getLogger(DataFeed.class.getName()).log(Level.SEVERE, null, ex);
                }
                parent.sendToAll(snapshot);
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
