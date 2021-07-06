package com.stardevmc.enforcer.modules.reports.enums;

public enum ReportOutcome {
    
    UNDECIDED("&7"), ACCEPTED("&a"), DENIED("&4"), CANCELLED("&c");
    
    private String color;
    ReportOutcome(String color) {
        this.color = color;
    }
    
    public String getColor() {
        return color;
    }
}