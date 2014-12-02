package java.Simulator;

public class security {
	String sym;//=symbol;
	
	long bid_price;//=bid_price1;
	long ask_price;//=ask_price1;
	
	long bid_quant;//=bid_quant1;
	long ask_quant;//=ask_quant1;
        
        /* sym|bid_price|ask_price|bid_qty|ask_qty */
        public String getSnapshotString() {
            return sym + "|" + Long.toString(bid_price) + "|" + Long.toString(ask_price) + "|" + 
                    Long.toString(bid_quant) + "|" + Long.toString(ask_quant);
        }
        
}
