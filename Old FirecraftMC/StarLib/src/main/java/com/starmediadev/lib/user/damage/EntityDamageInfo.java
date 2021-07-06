package com.starmediadev.lib.user.damage;

import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import java.util.Map;

@SerializableAs("EntityDamageInfo")
public class EntityDamageInfo extends DamageInfo {
    
    private EntityType entityType;
    
    public EntityDamageInfo(DamageCause cause, double damageAmount, EntityType entityType) {
        super(cause, damageAmount);
        this.entityType = entityType;
    }
    
    public EntityDamageInfo(EntityDamageEvent damageEvent) {
        super(damageEvent);
        if (damageEvent instanceof EntityDamageByEntityEvent) {
            this.entityType = ((EntityDamageByEntityEvent) damageEvent).getDamager().getType();
        }
    }
    
    public EntityDamageInfo(Map<String, Object> serialized) {
        super(serialized);
        this.entityType = EntityType.valueOf((String) serialized.get("entityType"));
    }
    
    public EntityType getEntityType() {
        return entityType;
    }
    
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = super.serialize();
        serialized.put("entityType", entityType.name());
        return serialized;
    }
    
    @Override
    public String toString() {
        return entityType.toString();
    }
}