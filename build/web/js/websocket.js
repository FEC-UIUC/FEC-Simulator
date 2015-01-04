
var webSocket;

function openSocket(){
    if(webSocket !== undefined && webSocket.readyState !== WebSocket.CLOSED){
       writeResponse("WebSocket is already opened.");
       return;
    }

    webSocket = new WebSocket("ws://localhost:8080/Simulator/server");

    webSocket.onopen = function(event){
            sendNewUser();
            if(event.data === undefined)
                    return;
            writeResponse(event.data);       
    };

    webSocket.onmessage = messageHandler;

    webSocket.onclose = function(event){
        writeResponse("Connection closed");
    };

}

function closeSocket(){
    webSocket.close();
}


function parseMessage(message_string) {
    var message = {};
    var pairs = message_string.split("|");
    for(var i=0; i < pairs.length; i++){
        var key_val = pairs[i].split("=", 2);
        message[key_val[0]] = key_val[1];
    }
    return message;
}


function formatMessage(message_map) {
    var message_string = "";
    for(var key in message_map){
        message_string += key + "=" + message_map[key] + "|";
    }
    return message_string.slice(0, -1);
}


function send(msg){
    webSocket.send(formatMessage(msg));
}


function messageHandler(event){
    var msg = parseMessage(event.data);
    if(msg['message_type'] === 'message'){
        handleChatMessage(msg);
    } 
    else if (msg['message_type'] == 'order'){
        handleOrder(msg);
    } 
    else if (msg['message_type'] =='cancel'){
        handleCancel(msg);
    } 
    else if (msg['message_type'] =='snapshot'){
        handleSnapshot(msg);
    }
    else if (msg['message_type'] == 'new_user'){
        handleNewUser(msg);
    }
}

