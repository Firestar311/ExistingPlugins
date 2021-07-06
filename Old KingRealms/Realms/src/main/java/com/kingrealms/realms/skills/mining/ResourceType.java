package com.kingrealms.realms.skills.mining;

import com.starmediadev.lib.cooldown.Cooldown;
import com.starmediadev.lib.util.Unit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("SameParameterValue")
public enum ResourceType {
    
    COBBLESTONE(Material.COBBLESTONE, 1), STONE(Material.STONE, Unit.MINUTES, 1, true), BLACKSTONE(Material.BLACKSTONE, 1), 
    COAL(Material.COAL_ORE, Material.COAL, 1), IRON(Material.IRON_ORE, Material.IRON_INGOT, 1), 
    GOLD(Material.GOLD_ORE, Material.GOLD_INGOT, 1), REDSTONE(Material.REDSTONE_ORE, Material.REDSTONE, 1), 
    LAPIS(Material.LAPIS_ORE, Material.LAPIS_LAZULI, 1), DIAMOND(Material.DIAMOND_ORE, Material.DIAMOND, 1), 
    EMERALD(Material.EMERALD_ORE, Material.EMERALD, 1), NETHERITE(Material.ANCIENT_DEBRIS, 5);
    
    private Material material, drop;
    private Cooldown cooldown;
    private boolean silkTouch;
    
    ResourceType(Material material, int length) {
        this(material, Unit.MINUTES, length, false);
    }
    
    ResourceType(Material material, Material drop, int length) {
        this.material = material;
        this.cooldown = new Cooldown(Unit.MINUTES, length);
        this.silkTouch = false;
        this.drop = drop;
    }
    
    ResourceType(Material material, Unit unit, int length, boolean silkTouch) {
        this.material = material;
        this.cooldown = new Cooldown(unit, length);
        this.silkTouch = silkTouch;
        this.drop = material;
    }
    
    public static ResourceType getType(ItemStack hand, Material block) {
        boolean hasSilkTouch = false;
        if (hand != null) {
            if (hand.getItemMeta() != null) {
                hasSilkTouch = hand.getItemMeta().hasEnchant(Enchantment.SILK_TOUCH);
            }
        }
        for (ResourceType type : values()) {
            if (type.getMaterial() == block) {
                if (!type.requiresSilkTouch()) {
                    return type;
                } else {
                    if (hasSilkTouch) return type;
                }
            }
        }
        
        return null;
    }
    
    public Material getMaterial() {
        return material;
    }
    
    public Cooldown getCooldown() {
        return cooldown;
    }
    
    public boolean requiresSilkTouch() {
        return silkTouch;
    }
    
    public Material getDrop() {
        return drop;
    }
}