package Simulator;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
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
    private static final HashMap<String, Session> sessions = new HashMap<String, Session>();
    private static final ClientManager clientManager = new ClientManager();
    private static Exchange exchange;
    
    FileOutputStream filestream = null;
    File uploadedFile = null;
    boolean uploadFileSuccess = true;
    
    final static File algoFilesDirectory = new File("C:\\Users\\Greg Pastorek\\Documents\\NetBeansProjects\\Simulator\\algos");
    final static File logFilesDirectory = new File("C:\\Users\\Greg Pastorek\\Documents\\NetBeansProjects\\Simulator\\web\\algo-logs");
    private static final String PYTHON_EXE = "C:\\Python27\\Python.exe";

    public Server(){
        exchange = new Exchange(clientManager);
    }
    
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
            } 
            catch (IllegalStateException ex) {
                sessions.remove(e);
            } 
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    
    public void sendToUser(String msg, String sessionID) {
        System.out.println("Sending to " + sessionID + ": " + msg);
        Session session = sessions.get(sessionID);
        if(session == null){
            System.out.println("Session " + sessionID + " is null");
            sessions.remove(sessionID);  //remove null sessions from mapping
            return;
        }
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
                uploadFileSuccess = false;
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
	else if (msgType.equals("algo-status")) {
            handleAlgoStatus(message_map, session.getId());
        }
    }

    
    @OnClose
    public void onClose(Session session) {
        System.out.println("Session " + session.getId() + " has ended");
        String username = clientManager.getUsername(session.getId());
        clientManager.removeSessionID(username, session.getId());
        //TODO - currently algo sessionID will remain in algoSessionIDs map in User object if program is closed forcibly, need to remove
        sessions.remove(session.getId());
		
	//onClose does not completely wipe user data, accounting for potential of accidental close
    }

    
    @OnError
    public void error(Session session, Throwable t) {
        t.printStackTrace();
    }
    
    
    private void broadcastChatMessage(HashMap<String, String> message_map){
        String tosend = MessageFormatter.format(message_map);
        sendToAll(tosend);
    }

    
    private void handleAdmin(HashMap<String, String> message_map, String sessionID) {
        //TODO - check if sessionID is the adminID
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
            bootUser(message_map.get("sessionID"));
        }
    }

    
    private void bootUser(String sessionID) {
        if (sessionID == null) {
            return;
        }
        
        AlgoProcessManager.removeUser(clientManager.getUsername(sessionID));
        
        clientManager.removeUser(sessionID);
    
        Session session_to_boot = sessions.get(sessionID);
        if (session_to_boot != null) {
            try {
                sendToUser("message_type=message|message=You have been booted.", session_to_boot);
                session_to_boot.close();
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    
    private void handleOrder(HashMap<String, String> message_map, String sessionID) {
        String symbol = message_map.get("symbol");
        long price = Long.parseLong(message_map.get("price"));
        long qty = Long.parseLong(message_map.get("quantity"));
        int side = Integer.parseInt(message_map.get("side"));
        int order_type = Integer.parseInt(message_map.get("order_type"));
        long orderID = Long.parseLong(message_map.get("orderID"));
        String username = clientManager.getUsername(sessionID);
        
        LinkedList<HashMap<String, String>> resps = exchange.placeOrder(orderID, username, symbol, price, qty, side, order_type);
        
        for (HashMap<String, String> resp : resps) {
            String _username = resp.get("username");
            String respString = MessageFormatter.format(resp);
            for(String sID : clientManager.getSessionIDs(_username)){
                sendToUser(respString, sID);
            }
        }
    }

    
    private void handleCancel(HashMap<String, String> message_map, String sessionID, Session session) {
        long orderID = Long.parseLong(message_map.get("orderID"));
        String username = clientManager.getUsername(sessionID);
        //System.out.println("username = " + username);
        HashMap<String, String> resp = exchange.cancelOrder(username, orderID);
        String respString = MessageFormatter.format(resp);
        for(String sID : clientManager.getSessionIDs(username)){
            sendToUser(respString, sID);
        }
    }

    
    private void handleNewUser(HashMap<String, String> message_map, String sessionID, Session session) {
        String username = message_map.get("username");
        
        //kill running algos if user rebooting
        AlgoProcessManager.removeUser(username);
        
        //TODO - what if user opens second? Currently will kill first websocket
        LinkedList<HashMap<String, String>> resps = clientManager.addUser(sessionID, username);
        
        for (HashMap<String, String> resp : resps) {
            String respString = MessageFormatter.format(resp);
            sendToUser(respString, session);
        }
    }
    
    
    private void handleNewAlgo(HashMap<String, String> message_map, String sessionID, Session session) {
        String username = message_map.get("username");
	long algoID = Long.parseLong(message_map.get("id"));
        System.out.println("Adding algo sessionId to " + username);
        LinkedList<HashMap<String, String>> resps = clientManager.addAlgoToUser(username, sessionID, algoID);

        for (HashMap<String, String> resp : resps) {
            String respString = MessageFormatter.format(resp);
            sendToUser(respString, session);
        }
    }
    
    
    private void handleRemoveAlgo(HashMap<String, String> message_map, String sessionID, Session session) {
        String username = message_map.get("username");
		long algoID = Long.parseLong(message_map.get("id"));
		AlgoProcessManager.stopAlgo(username, algoID);
        clientManager.removeAlgoFromUser(sessionID, username, algoID);
    }

    
    private void handleUploadFile(HashMap<String, String> message_map, String sessionID) {
        if (!message_map.get("command").equals("end")) {
            uploadFileSuccess = true;
            String fileName = message_map.get("filename");
            uploadedFile = new File(new File(new File(algoFilesDirectory, "user-algos"), sessionID), fileName);
            uploadedFile.getParentFile().mkdirs();
            try {
                uploadedFile.createNewFile();
                filestream = new FileOutputStream(uploadedFile, true);
                
                /* insert algo-wrapper.py to head of new file */
                File algoHeaderFile = new File(algoFilesDirectory, "algo-header.py");
                appendFileContent(algoHeaderFile, filestream);
                
            } catch (IOException ex) {
                uploadFileSuccess = false;
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                File algoFooterFile = new File(algoFilesDirectory, "algo-footer.py");
                appendFileContent(algoFooterFile, filestream);
                filestream.flush();
                filestream.close();
                int success = (uploadFileSuccess) ? 1 : 0;
                this.sendToUser("message_type=algo-upload|success=" + Integer.toString(success), sessionID);
            } catch (IOException e) {
                uploadFileSuccess = false;
                e.printStackTrace();
            }
        }
    }
    
    
    private void appendFileContent(File contentToAdd, FileOutputStream fstream) throws FileNotFoundException, IOException{
        
        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(contentToAdd).getChannel();
            destination = fstream.getChannel();
            destination.transferFrom(source, destination.size(), source.size());
        } 
        finally {
            if(source != null) {
                source.close();
            }
        }
    }
    
    
    private void handleAlgoCommand(HashMap<String, String> message_map, String sessionID) {
        String command  = message_map.get("command");
        if(command.equals("run")){
            try {
                String params = message_map.get("parameters");
                String username = clientManager.getUsername(sessionID);
                String fileName = message_map.get("filename");
		String logFileName = fileName.replaceFirst("[.][^.]+$", "") + ".log";  //remove file extension and append .log
                Long algoID = Long.parseLong(message_map.get("id"));
                String filePath = new File(new File(new File(algoFilesDirectory, "user-algos"), sessionID), fileName).getAbsolutePath();
		File logFile = new File(new File(logFilesDirectory, sessionID), logFileName);
                logFile.getParentFile().mkdirs();
                logFile.createNewFile();
		ProcessBuilder pb = new ProcessBuilder(PYTHON_EXE, filePath, "\"" + params + "\"", username, Long.toString(algoID), ">", logFile.getAbsolutePath());
                System.out.println(pb.toString());
                Process p = pb.start();
                AlgoProcessManager.addAlgo(username, algoID, p);
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else if (command.equals("stop")) {
            String username = clientManager.getUsername(sessionID);
            Long algoID = Long.parseLong(message_map.get("id"));
			
            //send stop command to running algo and wait, kill on timeout
            String algoSessionID = clientManager.getAlgoSessionID(username, algoID);
            HashMap<String, String> response = new HashMap<>();
            response.put("message_type", "stop");
            String respString = MessageFormatter.format(response);
            sendToUser(respString, algoSessionID);

            //wait for 1 second for safe process shutdown
            try{
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
			
            //remove algo from user object
            clientManager.removeAlgoFromUser(username, sessionID, algoID);

            //remove algo from process manager and kill if still active
            AlgoProcessManager.stopAlgo(username, algoID);

            //send stopped status update to user
            response = new HashMap<>();
            response.put("message_type", "algo-status");
            response.put("status", "stopped");
            response.put("id", Long.toString(algoID));

            username = clientManager.getUsername(sessionID);
            respString = MessageFormatter.format(message_map);
            for(String sID : clientManager.getSessionIDs(username)){
                sendToUser(respString, sID);
            }
				
        }
        else if (command.equals("remove")) {
            String username = clientManager.getUsername(sessionID);
            Long algoID = Long.parseLong(message_map.get("id"));
            AlgoProcessManager.stopAlgo(username, algoID);
            //TODO - remove file
        }
    }
	
	
    private void handleAlgoStatus(HashMap<String, String> message_map, String sessionID) {
		
        //commented out because stopping an algo sends "stopped" status update, which will hide the error status
        /*
        if(message_map.get("status").equals("error")){
                String algoID = message_map.get("algoID");
                String username = exchange.getUsername(sessionID);
                AlgoProcessManager.stopAlgo(username, algoID);
        }
        */

        //relay algo status message to other clients linked to this algo
        //TODO - currently relays to all linked websockets, we only want it to go to non-algo websockets
        String username = clientManager.getUsername(sessionID);
        String respString = MessageFormatter.format(message_map);
        for(String sID : clientManager.getNonAlgoSessionIDs(username)){
            sendToUser(respString, sID);
        }
    }
	
   
    private static class AlgoProcessManager {
        
        private static HashMap<String, HashMap<Long, Process>> runningAlgos = new HashMap<>();
       
        
        public static boolean addAlgo(String username, Long algoID, Process p){
            if(!runningAlgos.containsKey(username)) {
                runningAlgos.put(username, new HashMap<Long, Process>());
            }
            HashMap<Long, Process> userAlgos = runningAlgos.get(username);
            userAlgos.put(algoID, p);
            return true;
        }
        
        public static boolean stopAlgo(String username, Long algoID){
            if(!runningAlgos.containsKey(username)) {
                return false;
            }
            HashMap<Long, Process> userAlgos = runningAlgos.get(username);
            Process p = userAlgos.get(algoID);
            if(p != null){
                p.destroy();
                userAlgos.remove(algoID);
            }
            return true;
        }
        
        public static boolean removeUser(String username){
            if(!runningAlgos.containsKey(username)) {
                return false;
            }
            HashMap<Long, Process> userAlgos = runningAlgos.get(username);
            for(Long algoID : userAlgos.keySet()){
                stopAlgo(username, algoID);
            }
            runningAlgos.remove(username);
            return true;
        }
         
    }

}
