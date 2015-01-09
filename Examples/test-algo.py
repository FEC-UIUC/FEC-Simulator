

from numpy.random import randint

securities = ["AAPL", "GOOG", "MSFT"]  # default securities

parameters = {
	"window" : 1,
	"aggression" : 1
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
def handle_data(globals, data):
	for s in securities:
		order(s, randint(-10, 10))