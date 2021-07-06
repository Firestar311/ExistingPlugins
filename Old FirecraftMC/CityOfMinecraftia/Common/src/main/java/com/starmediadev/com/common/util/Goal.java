package com.starmediadev.com.common.util;

public class Goal {
    public static final Goal TOWN = new Goal("Lynch every criminal and evildoer.");
    
    private String message;
    
    protected Goal(String message) {
        this.message = message;
    }
    
    public String getMessage() {
        return message;
    }
}