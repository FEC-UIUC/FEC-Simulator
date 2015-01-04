
function startData(){
    send({
        'message_type' : 'admin', 
        'command' : 'start'
    });
 }

 function stopData(){
    send({
        'message_type' : 'admin', 
        'command' : 'stop'
    });
 }