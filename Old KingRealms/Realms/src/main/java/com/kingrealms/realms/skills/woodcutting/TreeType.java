package com.kingrealms.realms.skills.woodcutting;

import org.bukkit.Material;

public enum TreeType {
    OAK(Material.OAK_LOG, Material.OAK_SAPLING), BIRCH(Material.BIRCH_LOG, Material.BIRCH_SAPLING), SPRUCE(Material.SPRUCE_LOG, Material.SPRUCE_SAPLING), 
    JUNGLE(Material.JUNGLE_LOG, Material.JUNGLE_SAPLING), DARK_OAK(Material.DARK_OAK_LOG, Material.DARK_OAK_SAPLING), 
    ACACIA(Material.ACACIA_LOG, Material.ACACIA_SAPLING);
    
    private Material log;
    private Material sapling;
    TreeType(Material log, Material sapling) {
        this.log = log;
        this.sapling = sapling;
    }
    
    public Material getSapling() {
        return sapling;
    }
    
    public Material getLog() {
        return log;
    }
}