package com.stardevmc.enforcer.objects.enums;

import com.stardevmc.enforcer.objects.Colors;
import com.stardevmc.enforcer.objects.punishment.Punishment.Type;
import org.bukkit.Material;

public enum RawType {
    BAN(Material.RED_WOOL, Colors.BAN), MUTE(Material.LIGHT_GRAY_WOOL, Colors.MUTE), WARNING(Type.WARN, Material.YELLOW_WOOL, Colors.WARN),
    KICK(Type.KICK, Material.LIME_WOOL, Colors.KICK), JAIL(Type.KICK, Material.PINK_WOOL, Colors.JAIL),
    BLACKLIST(Type.BLACKLIST, Material.BLACK_WOOL, Colors.BLACKLIST);
    
    private Type type;
    private Material material;
    private String color;
    
    RawType(Material material, String color) {
        this.material = material;
        this.color = color;
    }
    RawType(Type type, Material material, String color) {
        this.type = type;
        this.material = material;
        this.color = color;
    }
    
    public Type getType() {
        return type;
    }
    
    public Material getMaterial() {
        return material;
    }
    
    public String getColor() {
        return color;
    }
}