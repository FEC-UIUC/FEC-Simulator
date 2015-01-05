
var orderStatusStyleMap = {
    "Pending" : "background-color:grey;",
    "Resting" : "background-color:white;",
    "Partial Filled" : "background-color:yellow;",
    "Filled" : "background-color:green;",
    "Canceled" : "background-color:red;"
}


var algorithmStatusStyleMap = {
    "stopped" : "background-color:light-grey;",
    "running" : "background-color:light-green;",
    "error" : "background-color:red;"
}


function setMoney(val){
    _money = val;
    $("#money-span").html(_money);
}


function addMoney(val){
    _money += val;
    $("#money-span").html(_money);
}


function updateOrdersTable(order){

    var order_row = order["tablepointer"]; 

    if(order_row == null){
        order_row = $("#templates").find(".current-orders-row").clone();
        order_row.attr("id", "order-row-" + order["orderID"].toString());
        order["tablepointer"] = order_row;
    }

    for(key in order){
      if(key != "status" && key != "tablepointer"){
        order_row.find("." + key).html(order[key]);   
      }
    }

    order_row.attr("style", orderStatusStyleMap[order["status"]]);

    if(order["status"] == "Filled" || order["status"] == "Canceled"){
        $("#past-orders-tbody").append(order_row);
        order_row.find(".cancel-order").remove("button");
        order_row.remove(".cancel-order");
    } else {
        $("#current-orders-tbody").append(order_row);
        var cancel_order_button = order_row.find(".cancel-order").find("button");
        cancel_order_button.click(function(e){ sendCancel(order["orderID"]);});
    }

}


function removeFromOrdersTable(order, tbody){
    var order_row = order["tablepointer"];
    var order_row_id = "order-row-" + order["orderID"].toString();
    tbody.remove("#" + order_row_id);
}


function updateSecurityTable(security) {
    var security_row = security["tablepointer"];

    if (security_row == null) {
        security_row = $("#templates").find(".securities-row").clone();
        security_row.attr("id", "security-row-" + security["symbol"]); 
        var order_button = security_row.find(".order").find("button");
        order_button.click(function(e){ sendOrder(security["symbol"]);});  
        security["tablepointer"] = security_row;
        $("#securities-tbody").append(security_row);
    }

    for(key in security){
      if(key != "price_series" && key != "volume_series" && key != "tablepointer" && key != "paid"){
        security_row.find("." + key).html(security[key]);   
      }
    } 
}


function removeFromSecuritiesTable(security){
    var security_row_id = "securities-row-" + security["symbol"].toString();
    $("#securities-tbody").remove("#" + security_row_id);
}



function updateAlgorithmTable(algorithm) {
    var algorithm_row = algorithm["tablepointer"];

    if (algorithm_row == null) {
        algorithm_row = $("#templates").find(".algorithms-row").clone();
        algorithm_row.attr("id", "algorithms-row-" + algorithm["id"]); 

        var run_button = algorithm_row.find(".run").find("button");
        run_button.click(function(e){ runAlgorithm(algorithm);});

        var stop_button = algorithm_row.find(".stop").find("button");
        stop_button.click(function(e){ stopAlgorithm(algorithm);});  

        var remove_button = algorithm_row.find(".remove").find("button");
        remove_button.click(function(e){ removeAlgorithm(algorithm);});

        var securities_button = algorithm_row.find(".securities").find("button");
        securities_button.click(function(e){ editAlgorithmSecurities(algorithm);});  

        var parameters_button = algorithm_row.find(".parameters").find("button");
        parameters_button.click(function(e){ editAlgorithmParameters(algorithm);});

        var log_button = algorithm_row.find(".log").find("a");
        log_button.attr("href", algorithm["log"]);

        algorithm["tablepointer"] = algorithm_row;
        $("#algorithms-tbody").append(algorithm_row);
    }

    for(key in algorithm){
      if(key != "tablepointer" && key != "securities" && key != "parameters" && key != "file"){
        algorithm_row.find("." + key).html(algorithm[key]);   
      }
    }

    algorithm_row.attr("style", algorithmStatusStyleMap[algorithm["status"]]);

}


function removeFromAlgorithmTable(algorithm){
    var algorithm_row_id = "algorithms-row-" + algorithm["id"].toString();
    $("#algorithms-tbody").remove("#" + algorithm_row_id);
}