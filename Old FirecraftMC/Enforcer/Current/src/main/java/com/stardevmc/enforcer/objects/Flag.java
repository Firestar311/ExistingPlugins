package com.stardevmc.enforcer.objects;

public enum Flag {
    PUBLIC('p'), SILENT('s'), IGNORE_CONFIRM('c'), IGNORE_TRAINING('t'), WAVE('w'), NORMAL('n'), IGNORE_REASON('r');
    
    private char shortHand;
    
    Flag(char shortHand) {
        this.shortHand = shortHand;
    }
    
    public static Flag matchFlag(String arg) {
        for (Flag flag : Flag.values()) {
            if (arg.startsWith("-" + flag.getShortHand())) {
                return flag;
            }
        }
        
        return null;
    }
    
    public char getShortHand() {
        return shortHand;
    }
}