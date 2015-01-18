if __name__ == "__main__":    _parameters = json.loads(sys.argv[1])    for p in _parameters:        parameters[p] = _parameters[p]    _username = sys.argv[2]    _algoID = int(sys.argv[3])    setNextOrderID(_algoID * 100000)    globals = {}    initialize(globals)    _orders = {}    data = {}    _last_new_sym = None    _update_sym = None    def on_message(ws, message):        global _last_new_sym, _update_sym        msg = parseMessage(message)        if msg["message_type"] == "quote":            sym = msg["symbol"]            if not sym in data:                data[sym] = {}                data[sym]["paid"] = 0                data[sym]["pnl"] = 0                data[sym]["position"] = 0                _last_new_sym = sym            elif _update_sym is None:                _update_sym = _last_new_sym            data[sym]["bid_price"] = msg["bid_price"]            data[sym]["ask_price"] = msg["ask_price"]            data[sym]["last_price"] = msg["last_price"]            data[sym]["bid_qty"] = msg["bid_qty"]            data[sym]["ask_qty"] = msg["ask_qty"]            # check if we are processing the symbol that marks time for an update            # all other symbols were processed at this tick            if sym == _update_sym:                handle_data(globals, copy.deepcopy(data), copy.deepcopy(_orders))        elif msg["message_type"] == "order":            orderID = msg["orderID"]            sym = msg["symbol"]            if not orderID in _orders:                _orders[orderID] = {}            _orders[orderID]["remaining"] = int(msg["remaining"])            _orders[orderID]["filled"] = _orders[orderID].get("filled", 0) + Math.abs(int(msg["filled"]))            data[sym]["paid"] += int(msg["money"])            data[sym]["position"] += int(msg["filled"])            _orders[orderID]["status"] = int(msg["action"])            data[sym]["pnl"] = data[sym]["position"] * data[sym]["last_price"] - data[sym]["paid"]            addMoney(int(msg["money"]))            ___onOrderConfirm(globals, orderID, price, filled)        elif msg["message_type"] == "cancel":            if int(msg["success"]) == 1:                orderID = int(msg["orderID"])                _orders[orderID]["status"] = 3                _orders[orderID]["remaining"] = 0    def on_error(ws, error):        print error    def on_close(ws):        print "### closed ###"    def on_open(ws):        ws.send("message_type=new_algo|username=" + _username)    websocket.enableTrace(True)    _sim_ws = websocket.WebSocketApp("ws://localhost:8080/Simulator/server",                                     on_message=on_message,                                     on_error=on_error,                                     on_close=on_close)    _sim_ws.on_open = on_open    _sim_ws.run_forever()