
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
        "securities" : [],
        "parameters" : {},
        "status" : "running",
        "PnL" : 0,
        "log-file" : "log/user_logs/" + username + "/" + algo_name.replace(" ", "_") + ".log",
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
        "content" : "start",
        "filename" : parseInt(algorithm["id"]) + "_" + algorithm["file"]
    })
    
    var file = algorithm["file"];
    var reader = new FileReader();
    var rawData = new ArrayBuffer();
    
    reader.onload = function(e) {
        rawData = e.target.result;
        ws.send(rawData);
        send({
            "message_type" : "algo-file",
            "content" : "end"
        })
        alert("The algorithm has been transferred.")
        
    }
    
    reader.readAsArrayBuffer(file); 
    
    
    
    return true;
}


function runAlgorithm(algorithm){

}


function stopAlgorithm(algorithm){

}


function removeAlgorithm(algorithm){
    removeAlgorithmFromTable(algorithm);
    delete algorithms[algorithm["id"]];
}


function editAlgorithmParameters(algorithm) {

}


function editAlgorithmSecurities(algorithm) {

}


