package net.firecraftmc.api.enums;

import net.firecraftmc.api.model.player.FirecraftPlayer;

public enum Rank {
    
    FIRECRAFT_TEAM(0, "§4", "FIRECRAFT TEAM", "Firecraft Team", "FirecraftTeam"),
    HEAD_ADMIN(1, "§4", "HEAD ADMIN", "Head Admin", "HeadAdmin"),
    ADMIN(2, "§c", "ADMIN", "Admin", "Admin"),
    TRIAL_ADMIN(3, "§c", "TRIAL ADMIN", "Trial Admin", "TrialAdmin"),
    MOD(4, "§2", "MOD", "Mod", "Mod"),
    TRIAL_MOD(5, "§2", "TRIAL MOD", "Trial Mod", "Trial Mod"),
    BUILD_TEAM(6, "§1", "BUILD TEAM", "Build Team", "BuildTeam"),
    
    VIP(11, "§3", "VIP", "VIP", "VIP"), FAMOUS(12, "§3", "FAMOUS", "Famous", "Famous"),
    
    PHOENIX(30, "§6", "PHOENIX", "Phoenix", "Phoenix"),
    INFERNO(31, "§6", "INFERNO", "Inferno", "Inferno"),
    EMBER(32, "§6", "EMBER", "Ember", "Ember"),
    DEFAULT(100, "§8", "", "Default", "Default");
    
    private final int order;
    private final String baseColor;
    private final String displayName;
    private final String teamName;
    
    private final String prefix;
    
    Rank(int order, String baseColor, String prefixName, String displayName, String teamName) {
        this.order = order;
        this.baseColor = baseColor;
        this.displayName = displayName;
        this.teamName = teamName;
        this.prefix = baseColor + "§l" + prefixName;
    }
    
    public boolean isHigher(Rank compareTo) {
        return order < compareTo.order;
    }
    
    public boolean isEqualToOrHigher(Rank compare) {
        return (equals(compare) || isHigher(compare));
    }
    
    public String getDisplayName() {
        return baseColor + displayName;
    }
    
    public String getPrefix() {
        return prefix;
    }
    
    public String getBaseColor() {
        if (Rank.isStaff(this)) {
            return "§l" + baseColor;
        }
        return baseColor;
    }
    
    public String getTeamName() {
        return teamName;
    }
    
    public static boolean isStaff(Rank rank) {
        return rank.isEqualToOrHigher(Rank.TRIAL_MOD);
    }
    
    public static Rank getRank(String value) {
        Rank rank = null;
        try {
            rank = Rank.valueOf(value.toUpperCase());
        } catch (Exception e) {
            if (value.equalsIgnoreCase("headadmin") || value.equalsIgnoreCase("ha")) {
                rank = Rank.HEAD_ADMIN;
            } else if (value.equalsIgnoreCase("trialadmin") || value.equalsIgnoreCase("ta")) {
                rank = Rank.TRIAL_ADMIN;
            } else if (value.equalsIgnoreCase("buildteam") || value.equalsIgnoreCase("bt")) {
                rank = Rank.BUILD_TEAM;
            } else if (value.equalsIgnoreCase("moderator") || value.equalsIgnoreCase("mod")) {
                rank = Rank.MOD;
            } else if (value.equalsIgnoreCase("trialmod") || value.equalsIgnoreCase("tm")) {
                rank = Rank.TRIAL_MOD;
            }
        }
        return rank;
    }
    
    public static boolean bothFT(FirecraftPlayer player1, FirecraftPlayer player2) {
        return player1.getMainRank().equals(FIRECRAFT_TEAM) && player2.getMainRank().equals(FIRECRAFT_TEAM);
    }
}
