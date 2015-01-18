
package Simulator;

public class Trade {
    String symbol;
    long makerOrderID;
    long takerOrderID;
    String makerUsername;
    String takerUsername;
    long filled;
    long price;
    long remaining_maker;
    long remaining_taker;

    public Trade(Order maker, Order taker, long quantity){
       symbol = maker.getSym();
       makerOrderID = maker.getOrderID();
       takerOrderID = taker.getOrderID();
       makerUsername = maker.getUsername();
       takerUsername = taker.getUsername();
       price = maker.getPrice();
       filled = quantity;
       remaining_maker = maker.getQty();
       remaining_taker = taker.getQty();
    }

    public String getSymbol(){
        return symbol;
    }
    
    public long getMakerOrderID() {
        return makerOrderID;
    }

    public long getTakerOrderID() {
        return takerOrderID;
    }
    
    public String getMakerUsername() {
        return makerUsername;
    }

    public String getTakerUsername() {
        return takerUsername;
    }

    public long getFilled() {
        return filled;
    }

    public long getPrice() {
        return price;
    }

    public long getMakerRemaining() {
        return remaining_maker;
    }

    public long getTakerRemaining() {
        return remaining_taker;
    }

}