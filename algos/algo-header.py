import json
import sys
import websocket
import copy


def parseMessage(msg_str):
    result = {}
    msg_split = msg_str.split("|")
    for pair in msg_split:
        k, v = pair.split("=", 1)
        result[k] = v
    return result


def formatMessage(msg_map):
    result = ""
    for k in msg_map:
        result += k + "=" + str(msg_map[k]) + "|"
    return result[:-1]


___money = 0


def getMoney():
    return ___money


def addMoney(amt):
    global ___money
    ___money += amt


___next_orderID = 0


def getNextOrderID():
    global ___next_orderID
    ___next_orderID += 1
    return ___next_orderID


def setNextOrderID(val):
    global ___next_orderID
    ___next_orderID = val - 1



def order(symbol, quantity, price=0, order_type='limit'):
    global _sim_ws
    if order_type != 'market' and not price:
        raise Exception("Must give a price for non-market orders.")
    side = "0" if amount > 0 else "1"
    message = {
        "message_type": "order",
        "symbol": symbol,
        "price": str(price),
        "side": side,
        "quantity": quantity,
        "order_type": order_type,
        "orderID": str(getNextOrderID())
    }
    _sim_ws.send(formatMessage(message))



def cancel(orderID):
    message = {
        "message_type": "cancel",
        "orderID": orderID
    }
    _sim_ws.send(formatMessage(message));




def ___onOrderConfirm(orderID, price, filled):
    try:
        onOrderConfirm(orderID, price, filled)
    except Exception as e:
        print e



'''''''''''''''''''''''''''
user code goes below here
'''''''''''''''''''''''''''

