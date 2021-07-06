package com.starmediadev.com.common.enums;

public enum EnumAttack {
    NONE("None"), BASIC("Basic"), POWERFUL("Powerful"), UNSTOPPABLE("Unstoppable"), DIVINE("Divine");

    private final String name;
    EnumAttack(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
