package com.stardevmc.shop.objects;

public class Price {
    
    private double buy;
    private double sell;
    
    public Price(double buy, double sell) {
        this.buy = buy;
        this.sell = sell;
    }
    
    public double buy() {
        return buy;
    }
    
    public void setBuy(double buy) {
        this.buy = buy;
    }
    
    public double sell() {
        return sell;
    }
    
    public void setSell(double sell) {
        this.sell = sell;
    }
}