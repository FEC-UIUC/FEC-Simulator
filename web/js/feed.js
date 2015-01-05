
function writeResponse(text){
    if(text.length > CHAT_WINDOW_CHAR_LENGTH){
        var remaining = text.slice(CHAT_WINDOW_CHAR_LENGTH);
        messages.innerHTML += "<br/>" + text.slice(0, CHAT_WINDOW_CHAR_LENGTH);
        writeResponse(remaining);
    } else {
        messages.innerHTML += "<br/>" + text;
    }
}


function clearFeed(){
    messages.innerHTML = "<br/>" 
}