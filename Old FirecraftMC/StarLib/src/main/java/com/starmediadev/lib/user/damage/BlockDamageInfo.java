package com.starmediadev.lib.user.damage;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import java.util.Map;

@SerializableAs("BlockDamageInfo")
public class BlockDamageInfo extends DamageInfo {
    
    private Material blockType;
    
    public BlockDamageInfo(DamageCause cause, double damageAmount, Material blockType) {
        super(cause, damageAmount);
        this.blockType = blockType;
    }
    
    public BlockDamageInfo(EntityDamageEvent damageEvent) {
        super(damageEvent);
        if (damageEvent instanceof EntityDamageByBlockEvent) {
            Block damager = ((EntityDamageByBlockEvent) damageEvent).getDamager();
            if (damager != null) {
                this.blockType = damager.getType();
            }
        }
    }
    
    public BlockDamageInfo(Map<String, Object> serialized) {
        super(serialized);
        try {
            this.blockType = Material.valueOf((String) serialized.get("blockType"));
        } catch (Exception e) {}
    }
    
    public Material getBlockType() {
        return blockType;
    }
    
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = super.serialize();
        if (this.blockType != null) {
            serialized.put("blockType", blockType.name());
        }
        return serialized;
    }
    
    @Override
    public String toString() {
        if (blockType == null) {
            return "Block Damage";
        }
        return blockType.name();
    }
}