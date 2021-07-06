package com.kingrealms.realms.economy.shop.enums;

@SuppressWarnings("SameParameterValue")
public enum ShopType {
    SIGN(true), GUI, NPC;
    
    private final boolean active;
    
    ShopType() {
        active = false;
    }
    
    ShopType(boolean active) {
        this.active = active;
    }
    
    public boolean isActive() {
        return active;
    }
}