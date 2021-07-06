package com.starmediadev.com.common.enums;

public enum EnumGender {
    MALE("He"), FEMALE("She");

    private final String pronoun;
    EnumGender(String pronoun) {
        this.pronoun = pronoun;
    }

    public String getPronoun() {
        return this.pronoun;
    }
}