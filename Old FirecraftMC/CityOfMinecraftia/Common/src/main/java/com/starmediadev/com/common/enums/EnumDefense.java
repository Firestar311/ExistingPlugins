package com.starmediadev.com.common.enums;

public enum EnumDefense {
    NONE("None"), BASIC("Basic"), POWERFUL("Powerful"), INVINCIBLE("Invincible");


    private final String name;
    EnumDefense(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
