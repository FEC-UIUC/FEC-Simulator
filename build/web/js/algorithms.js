
function addAlgorithm(){
    var algo_name = $("#algorithm-name").val();
    var filename = $("#algorithm-browse").val();
    if(validAlgorithmExtensions.indexOf(filename.split(".").pop()) == -1){
       alert("Invalid file extension.  File must be a Python file.");
       return;
    }
    if(algo_name == ""){
        algo_name = filename.split(".")[0];
    }
    var new_algorithm = {
        "id" : next_algorithm_id,
        "name" : algo_name,
        "file" : filename,
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
        "msg_type" : "algo-file",
        "filename" : file
    })
    
    var file = algorithm["file"];
    var reader = new FileReader();
    var rawData = new ArrayBuffer();
    
    reader.onload = function(e) {
        rawData = e.target.result;
        ws.send(rawData);
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


