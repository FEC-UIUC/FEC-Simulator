
function handleOrder(msg){
    
    var security = securities[msg["symbol"]];

    addMoney(parseInt(msg["money"]));

    order = orders[msg["orderID"]];
    order["remaining"] = parseInt(msg["remaining"]);
    order["filled"] += Math.abs(parseInt(msg["filled"]));

    security["paid"] += parseInt(msg["money"]);

    var side = (order["side"] == "BUY") ? 1 : -1;
    security['position'] += parseInt(msg["filled"]);
    var action = parseInt(msg["action"]);

    security['value'] = (security['position'] * security["last_price"])/100;
    security['total_pnl'] = (security['value'] - security['paid'])/100;

    if(action === 0){
        writeResponse("Order " + msg["orderID"] + " was filled for " + -1*side*msg["money"]/100);
        order["status"] = "Filled";
    } 
    else if(action === 1){
        writeResponse("Order " + msg["orderID"] + " was partially filled for " + -1*side*msg["money"]/100 + " and has " + msg["remaining"] + " remaining");
        order["status"] = "Partial Filled";
    } 
    else if(action === 2){
        writeResponse("Order " + msg["orderID"] + " was successfully placed");
        order["status"] = "Resting";
    } 
    else if(action === 3){
        writeResponse("Order " + msg["orderID"] + " was canceled");
        order["status"] = "Canceled";
    }

    orders[msg["orderID"]] = order;

    updateOrdersTable(order);

}


function handleCancel(msg){
    if(!orders.hasOwnProperty(msg["orderID"])){
        writeResponse("Got order " + msg["orderID"] + ", what the hell.");
        return;
    }
    if(msg["success"] == 0){
        writeResponse("Cancel Failed on order " + msg["orderID"]);
    } else if(msg["success"] == 1){
        writeResponse("Cancel Success on order " + msg["orderID"]);
        orders[msg["orderID"]]["status"] = "Canceled";
        orders[msg["orderID"]]["remaining"] = 0;
        updateOrdersTable(orders[msg["orderID"]]);
    }
}


function handleNewUser(msg){
    var money = parseInt(msg['money']);
    setMoney(money);
    $("#username-span").html(username);
}


function handleQuote(msg){

    if(!securities.hasOwnProperty(msg['symbol'])){
        securities[msg['symbol']] = {
            'symbol' : "",
            'price_series' : [],
            'volume_series' : [],
            'bid_price' : 0,
            'ask_price' : 0,
            'bid_qty' : 0,
            'ask_qty' : 0,
            "last_price" : 0,
            'position' : 0,
            'paid' : 0,
            'value' : 0,
            "total_pnl" : 0,
            'tablepointer' : null
        };
    } 

    var t = (new Date()).getTime()
    var bid_price = parseInt(msg['bid_price']);
    var ask_price = parseInt(msg['ask_price']);
    var last_price = parseInt(msg['last_price']);
    var bid_qty = parseInt(msg['bid_qty']);
    var ask_qty = parseInt(msg['ask_qty']);
    var open_price = (bid_price+ask_price)/2 + (50 - Math.round(100*Math.random()));
    var close_price = (bid_price+ask_price)/2 + (50 - Math.round(100*Math.random()));

    var bar = [t, open_price/100, open_price/100, close_price/100, close_price/100];

    var security = securities[msg['symbol']];

    security['symbol'] = msg['symbol'];
    security['price_series'].push([t, bar]);
    security['volume_series'].push([t,bid_qty+ask_qty]);
    security['value'] = (security['position'] * last_price)/100;
    security['bid_price'] = bid_price/100;
    security['ask_price'] = ask_price/100;
    security['bid_qty'] = bid_qty;
    security['ask_qty'] = ask_qty;
    security['last_price'] = last_price/100;
    security['total_pnl'] = (security['value'] - security['paid'])/100; 

    updateSecurityTable(securities[msg['symbol']]);

    if(msg['symbol'] == chart_security){
        stockchart.series[0].addPoint(bar, true, false);
        stockchart.series[1].addPoint([t, bid_qty+ask_qty], true, false);
    }
}


function handleChatMessage(msg) {
    var m = "<b>" + msg['from'] + ":</b> " + msg['message'].replace(/___bar___/g, "|"); 
    writeResponse(m);
}


function handleAlgoUpload(msg) {
    if(msg["success"] == "1"){
        alert("Upload successful.");
    } else {
        alert("Upload failed.");
    }
}