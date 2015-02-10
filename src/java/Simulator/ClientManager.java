/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Simulator;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;




/**
 *
 * @author Greg Pastorek
 */
public class ClientManager {
    
    TreeMap<String, User> users;
    TreeMap<String, String> sessionIds_to_name;
    
    public ClientManager() {
        users  = new TreeMap<>();
        sessionIds_to_name = new TreeMap<>();
    }
    
    
    
    public LinkedList<HashMap<String, String>> addUser(String sessionID, String username){

        LinkedList<HashMap<String, String>> responses = new LinkedList<>();

        long money = 0L;
        User user = null;
        
        if(users.containsKey(username)){
            user = users.get(username);
            money = user.getMoney();
            //TODO - recover user order/portfolio
            
        }
        
        //add new user entry
        sessionIds_to_name.put(sessionID, username);
        
        //add new user object if needed
        if(user == null){
            users.put(username, new User(sessionID));
        }
        
        HashMap<String, String> initial_response = new HashMap<>();
        initial_response.put("message_type", "new_user");
        initial_response.put("sessionId", sessionID);
        initial_response.put("money", Long.toString(money));
        responses.add(initial_response);
        
        return responses;
    }
    
    public boolean addSessionID(String username, String sessionID){
        if(sessionIds_to_name.containsKey(sessionID)){
            return false;
        }
        
        //add new userid entry
        sessionIds_to_name.put(sessionID, username);
        
        if(!users.containsKey(username)){
            users.put(username, new User(sessionID));
        } else {
            users.get(username).addSessionId(sessionID);
        }
        
        return true;
    }
    
	
	
    public boolean removeSessionID(String username, String sessionID){
        if(!sessionIds_to_name.containsKey(sessionID)){
            return false;
        }

        sessionIds_to_name.remove(sessionID);
        
        if(!users.containsKey(username)){
            return false;
        }
        
        users.get(username).removeSessionId(sessionID);
        
        return true;
    }
       

    public LinkedList<HashMap<String, String>> addAlgoToUser(String username, String sessionID, long algoID) {
		
        if(sessionIds_to_name.containsKey(sessionID)){
            return null;
        }
        
        //no need to "add new userid entry", done in "addUser"
        //sessionIds_to_name.put(sessionID, username);
        
        if(!users.containsKey(username)){
            users.put(username, new User(sessionID));
        } else {
            users.get(username).addAlgoSessionId(sessionID, algoID);
        }
        
        return addUser(sessionID, username);
		
    }
	
    public boolean removeAlgoFromUser(String username, String sessionID, long algoID) {
		
	if(!sessionIds_to_name.containsKey(sessionID)){
            return false;
        }

        sessionIds_to_name.remove(sessionID);
        
        if(!users.containsKey(username)){
            return false;
        }
        
        users.get(username).removeAlgoSessionId(sessionID, algoID);
        
        return true;
	
    }
	
	
    public String getAlgoSessionID(String username, long algoID) {
	if(users.containsKey(username)){
            return users.get(username).getAlgoSessionID(algoID);
        } else {
            return null;
        }
    }
	
    
    public boolean removeUser(String username){
        LinkedList<String> sessionIDs = getSessionIDs(username);
        for(String sessionID : sessionIDs){
            if(sessionID != null){
                sessionIds_to_name.remove(sessionID);
                users.get(username).removeSessionId(sessionID);
            }
        }
        return true;
    }
    
    public LinkedList<String> getSessionIDs(String username){
        if(users.containsKey(username)){
            return users.get(username).getSessionIds();
        }
        else{
            System.out.println(username + " does not exist");
            return null;
        }
    }
    
    
    public LinkedList<String> getNonAlgoSessionIDs(String username){
        if(users.containsKey(username)){
            return users.get(username).getNonAlgoSessionIds();
        }
        else{
            System.out.println(username + " does not exist");
            return null;
        }
    }
    
    
    
    public String getUsername(String sessionID) {
        return sessionIds_to_name.get(sessionID);
    }
    
    public User getUser(String username){
        return users.get(username);
    }
    
    public boolean userExists(String username){
        return users.containsKey(username);
    }
    
    
    public long getUserMoney(String username){
        if(users.containsKey(username)){
            return users.get(username).getMoney();
        }
        else{
            System.out.println(username + " does not exist");
            return 0;
        }
    }
    
    
    public List<Order> getUserOrders(String username){
        if(users.containsKey(username)){
            return users.get(username).getUserOrders();
        }
        else{
            System.out.println(username + " does not exist");
            return null;
        }
    }
    
    
    
    
        
}
