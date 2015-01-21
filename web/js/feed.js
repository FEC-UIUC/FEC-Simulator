var MAX_FEED_LINES = 200;
var cur_feed_lines = 0;

function writeResponse(text){
	cur_feed_lines++;
	if(cur_feed_lines > MAX_FEED_LINES){
		removeFeedLine();
	}
    if(text.length > CHAT_WINDOW_CHAR_LENGTH){
        var remaining = text.slice(CHAT_WINDOW_CHAR_LENGTH);
        messages.innerHTML += "<br/>" + text.slice(0, CHAT_WINDOW_CHAR_LENGTH);
        writeResponse(remaining);
    } else {
        messages.innerHTML += "<br/>" + text;
    };
	messages.scrollTop = messages.scrollHeight;
}


function clearFeed(){
    messages.innerHTML = "<br/>" 
}


function removeFeedLine(){
    var str = messages.innerHTML;
    messages.innerHTML = str.substring(str.indexOf("<br/>") + 1);
}