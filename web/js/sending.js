
function sendOrder(symbol){
    var security = securities[symbol];
    var security_row = security["tablepointer"];
    var quantity = security_row.find(".quantity").find("input").val();
    var price = Math.round(security_row.find(".price").find("input").val()*100);
    var side_input = security_row.find(".side").find("select");
    var BS = side_input[0].options[side_input[0].selectedIndex].value;
    var order_type_input = security_row.find(".order-type").find("select");
    var order_type = order_type_input[0].options[order_type_input[0].selectedIndex].value;
    var orderID = next_orderID;
    var message = {
        "message_type" : "order",
        "symbol" : symbol,
        "price" : price,
        "side" : BS,
        "quantity" : quantity,
        "order_type" : order_type,
        "orderID" : orderID,
    }

    if(price > 0){
        webSocket.send(formatMessage(message));
        orders[orderID] = {
            "orderID" : orderID,
            "symbol" : symbol,
            "price" : parseInt(price),
            "side" : (parseInt(BS) == 0) ? "BUY" : "SELL",
            "order_type" : (parseInt(order_type) == 0) ? "Market" : "Limit",
            "filled" : 0,
            "remaining" : parseInt(quantity),
            "status" : "Pending",
            "tablepointer" : null
        };
        updateOrdersTable(orders[orderID]);
    } else {
        writeResponse("Invalid Parameters for Order");
    }
    next_orderID++;            
}


function sendCancel(orderID){
    if(orderID != null){
        writeResponse("Sending cancel order for " + orderID);
        var msg = {
            "message_type" : "cancel",
            "orderID" : orderID
        }
        webSocket.send(formatMessage(msg));
    }
}


function sendNewUser() {
    var message = {
        'message_type' : "new_user",
        'username' : username
    }
    send(message);
}


function sendChatMessage(){
    var message_text = $("#messageinput").val();
    msg = {
        "message_type" : "message",
        "from" : username,
        "message" : message_text
    }
    send(msg);
    $("#messageinput").val("");
}