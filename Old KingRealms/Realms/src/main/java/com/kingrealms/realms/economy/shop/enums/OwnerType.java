package com.kingrealms.realms.economy.shop.enums;

@SuppressWarnings("SameParameterValue")
public enum OwnerType {
    PLAYER, SERVER(true), TERRITORY;
    
    private final boolean active;
    OwnerType() {
        active = false;
    }
    
    OwnerType(boolean active) {
        this.active = active;
    }
    
    public boolean isActive() {
        return active;
    }
}