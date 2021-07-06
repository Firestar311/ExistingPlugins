package com.starmediadev.com.common.enums;

import org.bukkit.Material;

import java.util.List;

public enum EnumFaction {
    TOWN(11, Material.GREEN_CONCRETE, Material.CACTUS_GREEN), MAFIA(2, Material.RED_CONCRETE, Material.ROSE_RED),
    COVEN(2, Material.PURPLE_CONCRETE, Material.PURPLE_DYE), NEUTRAL(3, Material.YELLOW_CONCRETE, Material.DANDELION_YELLOW),
    APOCALYPSE(2, Material.GRAY_CONCRETE, Material.GRAY_DYE), ANARCHY(3, Material.CYAN_CONCRETE, Material.CYAN_DYE),
    MYTHICAL(2, Material.ORANGE_CONCRETE, Material.ORANGE_DYE);

    private final int defaultAmount;
    private final Material material;
    private final Material roleMaterial;
    EnumFaction(int defAmount, Material material, Material roleMaterial) {
        this.defaultAmount = defAmount;
        this.material = material;
        this.roleMaterial = roleMaterial;
    }

    public int getDefaultAmount() {
        return this.defaultAmount;
    }

    public List<EnumRole> getRoles() {
        switch (this) {
            case TOWN: return EnumRole.TOWN_ROLES();
            case MAFIA: return EnumRole.MAFIA_ROLES();
            case COVEN: return EnumRole.COVEN_ROLES();
            case NEUTRAL: return EnumRole.NEUTRAL_ROLES();
            case APOCALYPSE: return EnumRole.APOCALYPSE_ROLES();
            case ANARCHY: return EnumRole.ANARCHY_ROLES();
            case MYTHICAL: return EnumRole.MYTHICAL_ROLES();
        }
        return null;
    }
    
    public Material getMaterial() {
        return material;
    }
    
    public Material getRoleMaterial() {
        return roleMaterial;
    }
}
