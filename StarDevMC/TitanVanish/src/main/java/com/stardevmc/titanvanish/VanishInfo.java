package com.stardevmc.titanvanish;

import com.firestar311.lib.util.Utils;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.lang.reflect.Field;
import java.util.*;

public class VanishInfo implements ConfigurationSerializable, Cloneable {
    
    private UUID player;
    private boolean active, itemPickup, itemDrop, entityInteract, damage, chat, collision, entityTarget,
            blockBreak, blockPlace, silent, fly, nightVision;
    private long date;
    
    public VanishInfo(UUID player) {
        this.player = player;
    }
    
    public VanishInfo(boolean itemPickup, boolean itemDrop, boolean entityInteract, boolean damage, boolean chat, boolean collision, boolean entityTarget, boolean blockBreak, boolean blockPlace, boolean silent, boolean fly, boolean nightVision) {
        this.itemPickup = itemPickup;
        this.itemDrop = itemDrop;
        this.entityInteract = entityInteract;
        this.damage = damage;
        this.chat = chat;
        this.collision = collision;
        this.entityTarget = entityTarget;
        this.blockBreak = blockBreak;
        this.blockPlace = blockPlace;
        this.silent = silent;
        this.fly = fly;
        this.nightVision = nightVision;
    }
    
    public VanishInfo() {
    
    }
    
    public void setPlayer(UUID player) {
        this.player = player;
    }
    
    public UUID getPlayer() {
        return player;
    }
    
    public long getDate() {
        return date;
    }
    
    public void setDate(long date) {
        this.date = date;
    }
    
    public void setItemDrop(boolean itemDrop) {
        this.itemDrop = itemDrop;
    }
    
    public void setItemPickup(boolean itemPickup) {
        this.itemPickup = itemPickup;
    }
    
    public void setEntityInteract(boolean entityInteract) {
        this.entityInteract = entityInteract;
    }
    
    public void setDamage(boolean damage) {
        this.damage = damage;
    }
    
    public void setChat(boolean chat) {
        this.chat = chat;
    }
    
    public void setFly(boolean fly) {
        this.fly = fly;
    }
    
    public void setCollision(boolean collision) {
        this.collision = collision;
    }
    
    public void setEntityTarget(boolean entityTarget) {
        this.entityTarget = entityTarget;
    }
    
    public void setBlockBreak(boolean blockBreak) {
        this.blockBreak = blockBreak;
    }
    
    public void setBlockPlace(boolean blockPlace) {
        this.blockPlace = blockPlace;
    }
    
    public void setSilent(boolean silent) {
        this.silent = silent;
    }
    
    public void setNightVision(boolean nightVision) {
        this.nightVision = nightVision;
    }
    
    public boolean canItemPickup() {
        return itemPickup;
    }
    
    public boolean canDropItems() {
        return itemDrop;
    }
    
    public boolean canEntityInteract() {
        return entityInteract;
    }
    
    public boolean canDamage() {
        return damage;
    }
    
    public boolean canChat() {
        return chat;
    }
    
    public boolean canCollide() {
        return collision;
    }
    
    public boolean canEntityTarget() {
        return entityTarget;
    }
    
    public boolean canBlockBreak() {
        return blockBreak;
    }
    
    public boolean canBlockPlace() {
        return blockPlace;
    }
    
    public boolean isSilent() {
        return silent;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public boolean canFly() {
        return fly;
    }
    
    public boolean usingNightVision() {
        return nightVision;
    }
    
    public VanishInfo(Map<String, Object> serialized) {
        List<Field> fields = Utils.getClassFields(this, false);
        for (Field field : fields) {
            field.setAccessible(true);
            if (serialized.containsKey(field.getName())) {
                try {
                    if (field.getType().isAssignableFrom(UUID.class)) {
                        field.set(this, UUID.fromString((String) serialized.get(field.getName())));
                    } else {
                        field.set(this, serialized.get(field.getName()));
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        List<Field> fields = Utils.getClassFields(this, false);
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                if (field.getType().isAssignableFrom(UUID.class)) {
                    UUID uuid = (UUID) field.get(this);
                    serialized.put(field.getName(), uuid.toString());
                } else {
                    serialized.put(field.getName(), field.get(this));
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return serialized;
    }
    
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}