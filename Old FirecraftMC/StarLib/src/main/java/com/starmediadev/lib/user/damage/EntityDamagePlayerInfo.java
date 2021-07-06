package com.starmediadev.lib.user.damage;

import com.starmediadev.lib.StarLib;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import java.util.Map;
import java.util.UUID;

public class EntityDamagePlayerInfo extends EntityDamageInfo {
    
    private UUID attacker;
    
    public EntityDamagePlayerInfo(DamageCause cause, double damageAmount, EntityType entityType, UUID attacker) {
        super(cause, damageAmount, entityType);
        this.attacker = attacker;
    }
    
    public EntityDamagePlayerInfo(EntityDamageEvent damageEvent) {
        super(damageEvent);
        if (damageEvent instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent event = ((EntityDamageByEntityEvent) damageEvent);
            if (event.getDamager() instanceof Player) {
                this.attacker = event.getDamager().getUniqueId();
            }
        }
    }
    
    public EntityDamagePlayerInfo(Map<String, Object> serialized) {
        super(serialized);
        this.attacker = UUID.fromString((String) serialized.get("attacker"));
    }
    
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = super.serialize();
        serialized.put("attacker", attacker.toString());
        return serialized;
    }
    
    @Override
    public String toString() {
        return StarLib.getInstance().getUserManager().getUser(this.attacker).getLastName();
    }
}