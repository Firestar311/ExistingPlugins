package com.starmediadev.lib.util;

import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public class MaterialNames {
    private static final Map<Material, String> materialNames = new HashMap<>();
    //These are used for instantiation, which is needed for the setting of the names.
    private static MaterialNames instance = new MaterialNames();
    
    private MaterialNames() {
        for (Material material : Material.values()) {
            materialNames.put(material, Utils.capitalizeEveryWord(material.name()));
        }
        
        //This is where custom names can be set.
    }
    
    public static String getName(Material material) {
        if (material == null) {
            return "None";
        }
        return (materialNames.getOrDefault(material, "None"));
    }
    
    private MaterialNames getInstance() {
        return instance;
    }
}