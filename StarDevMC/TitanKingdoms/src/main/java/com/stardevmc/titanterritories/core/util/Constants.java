package com.stardevmc.titanterritories.core.util;

import java.text.SimpleDateFormat;

public final class Constants {
    
    private Constants() {}
    
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a z");
    public static final int TOWN_MAX_CLAIM = 6;
    public static final int TOWN_MAX_MEMBERS = 20;
    
    public static final double KINGDOM_CREATE_COST = 1000;
    public static final double TOWN_CREATE_COST = 100;
    public static final double COLONY_CREATE_COST = 100;
    public static final double KINGDOM_CLAIM_COST = 50;
    public static final double TOWN_CLAIM_COST = 50;
    public static final double COLONY_CLAIM_COST = 50;
    
    public static final double TOWN_CLAIM_DISCOUNT = .25;
    public static final double TOWN_EXP_MULTIPLIER = .15;
}