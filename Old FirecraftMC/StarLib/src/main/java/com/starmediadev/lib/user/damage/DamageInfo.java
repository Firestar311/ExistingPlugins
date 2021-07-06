package com.starmediadev.lib.user.damage;

import com.starmediadev.lib.pagination.IElement;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import java.util.HashMap;
import java.util.Map;

@SerializableAs("DamageInfo")
public class DamageInfo implements ConfigurationSerializable, IElement {
    private DamageCause cause;
    private double damageAmount;
    
    public DamageInfo(DamageCause cause, double damageAmount) {
        this.cause = cause;
        this.damageAmount = damageAmount;
    }
    
    public DamageInfo(EntityDamageEvent damageEvent) {
        this.cause = damageEvent.getCause();
        this.damageAmount = damageEvent.getDamage();
    }
    
    public DamageInfo(Map<String, Object> serialized) {
        this.cause = DamageCause.valueOf((String) serialized.get("cause"));
        this.damageAmount = Double.parseDouble((String) serialized.get("damageAmount"));
    }
    
    public Map<String, Object> serialize() {
        return new HashMap<>() {{
           put("cause", cause.name());
           put("damageAmount", damageAmount + "");
        }};
    }
    
    public DamageCause getCause() {
        return cause;
    }
    
    public double getDamageAmount() {
        return damageAmount;
    }
    
    public String toString() {
        return cause.name();
    }
}