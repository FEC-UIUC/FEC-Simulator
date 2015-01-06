/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Simulator;

import com.sun.xml.ws.util.StringUtils;
import java.util.HashMap;

/**
 *
 * @author Greg Pastorek
 */
public class MessageFormatter {
    
    public static String format(HashMap<String, String> message_map) {
        if(message_map.isEmpty()){
            return "";
        }
        String message_string = "";
        for(String key : message_map.keySet()) {
            message_string += key + "=" + message_map.get(key) + "|";
        }
        return message_string.substring(0, message_string.length()-1);
    }
    
    
    public static HashMap<String, String> parse(String message){
        HashMap<String, String> message_map = new HashMap<String, String>();
        String[] pairs = message.split("\\|");
        for(String p : pairs){
            String[] key_val = p.split("\\=", 2);
            message_map.put(key_val[0], key_val[1]);
        }
        return message_map;
    }
    
}
