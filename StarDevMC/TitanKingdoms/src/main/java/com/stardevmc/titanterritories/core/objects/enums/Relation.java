package com.stardevmc.titanterritories.core.objects.enums;

public enum Relation {
    ALLY("&a"), NEUTRAL("&7"), ENEMY("&c");
    
    private String color;
    Relation(String color) {
        this.color = color;
    }
    
    public String getColor() {
        return color;
    }
}