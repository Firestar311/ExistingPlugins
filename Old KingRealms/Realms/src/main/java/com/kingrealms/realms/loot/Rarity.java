package com.kingrealms.realms.loot;

public enum Rarity {
    COMMON(0.600), UNCOMMON(0.250), RARE(0.100), EPIC(0.030), LEGENDARY(0.010), ULTRA_LEGENDARY(0.001);
    
    double percent;
    Rarity(double percent) {
        this.percent = percent;
    }
    
    public double getPercent() {
        return percent;
    }
}