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
    boolean stop_flag = false;
    
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
        System.out.println("MyThread running");
        while (true) {
            System.out.println("tick");
            long millis = System.currentTimeMillis();
            String msg = String.valueOf(Math.random());
            try{
                session.getBasicRemote().sendText("data|" + msg);
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
