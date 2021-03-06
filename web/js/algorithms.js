
function addAlgorithm(){
    var algo_name = $("#algorithm-name").val();
    var file = $("#algorithm-browse")[0].files[0];
    if(validAlgorithmExtensions.indexOf(file.name.split(".").pop()) == -1){
       alert("Invalid file extension.  File must be a Python file.");
       return;
    }
    if(algo_name == ""){
        algo_name = file.name.split(".")[0];
    }
    var new_algorithm = {
        "id" : next_algorithm_id,
        "name" : algo_name,
        "file" : file,
        "parameters" : {},
        "status" : "stopped",
        "PnL" : 0,
        "log" : null,
        "tablepointer" : null
    };
    algorithms[next_algorithm_id] = new_algorithm;
    next_algorithm_id++;
    if(uploadAlgorithm(new_algorithm)){
        updateAlgorithmTable(new_algorithm);
    }
}


function uploadAlgorithm(algorithm) {
    
    send({
        "message_type" : "algo-file",
        "command" : "start",
        "algoID" : algorithm["id"],
        "filename" : parseInt(algorithm["id"]) + "_" + algorithm["file"].name
    })
    
    var file = algorithm["file"];
    var reader = new FileReader();
    var rawData = new ArrayBuffer();
    
    reader.onload = function(e) {
        rawData = e.target.result;
        webSocket.send(rawData);
        send({
            "message_type" : "algo-file",
            "command" : "end"
        })       
    }
    
    reader.readAsArrayBuffer(file); 
    
    return true;
}


function runAlgorithm(algorithm){
    var parameters = {
        "window" : 3,
        "aggression" : 2,
        "securities" : ['AAPL', 'GOOGL', 'MSFT', 'AMZN', 'FB']
    }

    var msg = {
        "message_type" : "algo-command",
        "command" : "run",
        "algoID" : algorithm["id"],
        "filename" : parseInt(algorithm["id"]) + "_" + algorithm["file"].name,
        "parameters" : JSON.stringify(parameters)
    }
    send(msg);    
}


function stopAlgorithm(algorithm){
    
    var msg = {
        "message_type" : "algo-command",
        "command" : "stop",
        "algoID" : algorithm["id"]
    }
    send(msg);

}


function removeAlgorithm(algorithm){
    var msg = {
        "message_type" : "algo-command",
        "command" : "remove",
        "algoID" : algorithm["id"]
    }
    send(msg);
    removeFromAlgorithmTable(algorithm);
    delete algorithms[algorithm["id"]];
}


function editAlgorithmParameters(algorithm) {

}


function editAlgorithmSecurities(algorithm) {

}


