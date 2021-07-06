package com.starmediadev.lib.util;

public final class TickUtils {
    private TickUtils(){}
    
    public static int asSeconds(int duration) {
        return duration * 20;
    }
    
    public static int asMinutes(int duration) {
        return asSeconds(1) * 60 * duration;
    }
}