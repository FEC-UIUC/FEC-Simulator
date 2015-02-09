from numpy.random import randint, normal
from numpy import sign

# default parameters
parameters = {
	"volume": 1.0,
	"frequency" : 0.5,
	"variance" : 1.0,
	"orders-per-action" : 3
}

def randomBool(probability):
    return random.random() < probability
	

def initialize(globals):
	globals['files'] = {}
    cl_symbol_data = sys.argv[4:]
	try:
		for i in xrange(0, len(cl_symbol_data), 2):
			symbol = cl_symbol_data[i]
			filepath = cl_symbol_data[i+1]
			globals['files'][symbol] = open(filepath, "r")
	except:
		raise Exception("Invalid file parameters")

'''
  The main proccessing function.  This is called and passed data
'''
def handle_data(globals, data, orders):
	for security in data:
	
		if not randomBool(parameters['frequency']):
			continue
	
		if not security in globals['files']:
			continue
			
		f = globals['files'][security]
		
		if f.closed:
			continue
		
		nextline = f.readline()
		
		if not nextline:
			f.close()
			continue
			
		bot_bid_price, bot_ask_price, bot_bid_qty, bot_ask_qty = nextline.split(';')
		
		sym_data = data[security]
		
		bid_diff = sym_data['bid_price'] - bot_bid_price
		ask_diff = sym_data['ask_price'] - bot_ask_price
		
		opa = parameters["orders-per-action"]
		
		for i in xrange(0, opa):
			price = bot_bid_price + normal(scale=parameters["variance"])
			qty = int(parameters["volume"] * bot_bid_qty * sign(bid_diff) * -1) / opa
			order(security, qty, price, order_type='limit')	
				
		for i in xrange(0, opa):
			price = bot_ask_price + normal(scale=parameters["variance"])
			qty = int(parameters["volume"] * bot_ask_qty * sign(ask_diff) * -1) / opa
			order(security, qty, price, order_type='limit')	
		
		
		
def onOrderConfirm(globals, data, orders, orderID, fill_price, fill_qty, event):
	print "Order confirmed - " + str((orderID, fill_price, fill_qty, event))
