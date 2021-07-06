package com.stardevmc.enforcer.objects.enums;

import org.bukkit.Material;

public enum Visibility {
    PUBLIC("&9[PUBLIC] ", "Make this punishment a public punishment, where all players can see the notification", Material.DIAMOND),
    STAFF_ONLY("&9[STAFF ONLY] ", "Make this punishment a normal punishment to where only staff members with the notify permission will be able to see the notification.", Material.QUARTZ),
    SILENT("&9[SILENT] ", "Make this punishment a silent punishment to where only staff members with the permission for your group or higher (Permission inheritance is key) will be able to see the notification.", Material.REDSTONE);
    
    private String prefix, description;
    private Material material;
    Visibility(String prefix, String description, Material material) {
        this.prefix = prefix;
        this.description = description;
        this.material = material;
    }
    
    public String getPrefix() {
        return prefix;
    }
    
    public Material getMaterial() {
        return material;
    }
    
    public String getDescription() {
        return description;
    }
}