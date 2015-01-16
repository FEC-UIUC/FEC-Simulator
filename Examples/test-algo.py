from numpy.random import randint


# default parameters
parameters = {
	"window" : 1,
	"aggression" : 1,
	"securities" : ["AAPL", "GOOG", "MSFT"]
}

def initialize(globals):
    # Keep track of the current month.
    globals.currentMonth = None

    # The current stock being held
    globals.currentStock = None
    
    # The next stock that needs to get purchased (once the sell order
    # on the current stock is filled
    globals.nextStock = None

'''
  The main proccessing function.  This is called and passed data
'''
def handle_data(globals, data, orders):
	for s in data:
		oID = order(s, randint(-10, 10), price=randint(80,120), order_type='limit')
		print "OrderID = " + str(oID)
		
		
def onOrderConfirm(orderID, price, filled):
	print "Order confirmed - " + str((orderID, price, filled))