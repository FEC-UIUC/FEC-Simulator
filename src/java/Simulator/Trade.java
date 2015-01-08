
package Simulator;

public class Trade {
    long makerOrderID;
    long takerOrderID;
    String makerUserID;
    String takerUserID;
    long filled;
    long price;
    long remaining_maker;
    long remaining_taker;

    public Trade(Order maker, Order taker, long quantity){
       makerOrderID = maker.getOrderID();
       takerOrderID = taker.getOrderID();
       makerUserID = maker.getUserID();
       takerUserID = taker.getUserID();
       price = maker.getPrice();
       filled = quantity;
       remaining_maker = maker.getQty();
       remaining_taker = taker.getQty();
    }

    public long getMakerOrderID() {
        return makerOrderID;
    }

    public long getTakerOrderID() {
        return takerOrderID;
    }
    
    public String getMakerUserID() {
        return makerUserID;
    }

    public String getTakerUserID() {
        return takerUserID;
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