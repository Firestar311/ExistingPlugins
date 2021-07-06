package com.kingrealms.realms.supplydrops;

public class SupplyDrop {
    
    private long date;
    private String startedBy;
    
    public SupplyDrop(long date, String startedBy) {
        this.date = date;
        this.startedBy = startedBy;
    }
    
    public long getDate() {
        return date;
    }
    
    public void setDate(long date) {
        this.date = date;
    }
    
    public String getStartedBy() {
        return startedBy;
    }
    
    public void setStartedBy(String startedBy) {
        this.startedBy = startedBy;
    }
}