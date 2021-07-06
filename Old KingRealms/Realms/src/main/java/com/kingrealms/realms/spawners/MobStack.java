package com.kingrealms.realms.spawners;

import com.starmediadev.lib.util.EntityNames;
import com.starmediadev.lib.util.Utils;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.LivingEntity;

import java.util.*;

@SerializableAs("MobStack")
public class MobStack implements ConfigurationSerializable {

    private final int MAX = 100;
    
    private UUID entityId; //mysql
    private int count = 1; //mysql
    
    public MobStack(UUID entityId) {
        this.entityId = entityId;
    }
    
    public MobStack(UUID entityId, int count) {
        this.entityId = entityId;
        this.count = count;
    }
    
    public void increment() {
        if (count < MAX) {
            this.count++;
        }
    }
    
    public void decrement() {
        if (count != 0) {
            this.count--;
        }
    }
    
    public void increment(int amount) {
        if ((count + amount) <= MAX) {
            this.count += amount;
        } else {
            this.count = MAX;
        }
    }
    
    public void decrement(int amount) {
        if (count < amount) {
            count = 0;
        } else {
            count -= amount;
        }
    }
    
    public static MobStack deserialize(Map<String, Object> serialized) {
        UUID entityId = UUID.fromString((String) serialized.get("entityId"));
        int count = Integer.parseInt((String) serialized.get("count"));
        return new MobStack(entityId, count);
    }
    
    @Override
    public Map<String, Object> serialize() {
        return new HashMap<>() {{
            put("entityId", entityId.toString());
            put("count", count + "");
        }};
    }
    
    public void setEntityId(UUID entityId) {
        this.entityId = entityId;
    }
    
    public UUID getEntityId() {
        return entityId;
    }
    
    public int getCount() {
        return count;
    }
    
    public void updateName(LivingEntity entity) {
        entity.setCustomName(Utils.color("&c&l" + EntityNames.getName(entity.getType()) + " &f&lx" + getCount()));
    }
    
    public void setCount(int amount) {
        this.count = amount;
    }
}