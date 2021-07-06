package com.kingrealms.realms.items;

import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public enum ToolType {
    WOOD(Material.OAK_LOG, Material.IRON_BLOCK), STONE(Material.COBBLESTONE, Material.IRON_BLOCK), GOLD(Material.GOLD_BLOCK, Material.IRON_BLOCK), 
    DIAMOND(Material.DIAMOND_BLOCK, Material.EMERALD_BLOCK), IRON(Material.IRON_BLOCK, Material.DIAMOND_BLOCK), NETHERITE(Material.NETHERITE_BLOCK, Material.DIAMOND_BLOCK);
    
    private Material main, stick;
    ToolType(Material main, Material stick) {
        this.main = main;
        this.stick = stick;
    }
    
    public Material getMain() {
        return main;
    }
    
    public Material getStick() {
        return stick;
    }
    
    public static final Map<ToolType, Material> PICKAXES = new HashMap<>() {{
       put(WOOD, Material.WOODEN_PICKAXE);
       put(STONE, Material.STONE_PICKAXE);
       put(GOLD, Material.GOLDEN_PICKAXE);
       put(DIAMOND, Material.DIAMOND_PICKAXE);
       put(IRON, Material.IRON_PICKAXE);
       put(NETHERITE, Material.NETHERITE_PICKAXE);
    }};
    
    public static final Map<ToolType, Material> SHOVELS = new HashMap<>() {{
        put(WOOD, Material.WOODEN_SHOVEL);
        put(STONE, Material.STONE_SHOVEL);
        put(GOLD, Material.GOLDEN_SHOVEL);
        put(DIAMOND, Material.DIAMOND_SHOVEL);
        put(IRON, Material.IRON_SHOVEL);
        put(NETHERITE, Material.NETHERITE_SHOVEL);
    }};
    
    public static final Map<ToolType, Material> AXES = new HashMap<>() {{
        put(WOOD, Material.WOODEN_AXE);
        put(STONE, Material.STONE_AXE);
        put(GOLD, Material.GOLDEN_AXE);
        put(DIAMOND, Material.DIAMOND_AXE);
        put(IRON, Material.IRON_AXE);
        put(NETHERITE, Material.NETHERITE_AXE);
    }};
}