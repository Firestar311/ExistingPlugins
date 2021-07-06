package com.kingrealms.realms.territory.enums;

public enum Privacy {
    OPEN("Open", "&a", false), PRIVATE("Private", "&c", false), WORKER("Worker", "&8", true), GUEST("Guest", "&9", true);
    
    private boolean disabled;
    private String name, color;
    
    Privacy() {
        disabled = false;
    }
    
    Privacy(boolean disabled) {
        this.disabled = disabled;
    }
    
    Privacy(String name, String color, boolean disabled) {
        this.name = name;
        this.color = color;
        this.disabled = disabled;
    }
    
    public boolean isDisabled() {
        return disabled;
    }
    
    public String getDisplayName() {
        return this.color + this.name;
    }
    
    public String getRawDisplayName() {
        return this.color + this.name();
    }
    
    public String getName() {
        return name;
    }
    
    public String getColor() {
        return color;
    }
}