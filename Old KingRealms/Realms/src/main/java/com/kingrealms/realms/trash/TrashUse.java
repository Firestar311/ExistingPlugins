package com.kingrealms.realms.trash;

import com.starmediadev.lib.items.InventoryStore;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@SerializableAs("TrashUse")
public class TrashUse implements ConfigurationSerializable {
    
    private long date, restoreDate;
    private String items;
    private UUID player, restoreActor;
    
    public TrashUse(UUID player, long date, String items) {
        this.player = player;
        this.date = date;
        this.items = items;
    }
    
    public TrashUse(UUID player, long date, ItemStack[] itemStacks) {
        this.player = player;
        this.date = date;
        this.items = InventoryStore.itemsToString(itemStacks);
    }
    
    public TrashUse(UUID player, UUID restoreActor, long date, long restoreDate, String items) {
        this.player = player;
        this.restoreActor = restoreActor;
        this.date = date;
        this.restoreDate = restoreDate;
        this.items = items;
    }
    
    public TrashUse(Map<String, Object> serialized) {
        this.player = UUID.fromString((String) serialized.get("player"));
        this.date = Long.parseLong((String) serialized.get("date"));
        this.items = (String) serialized.get("items");
        if (serialized.containsKey("restoreActor")) {
            this.restoreActor = UUID.fromString((String) serialized.get("restoreActor"));
            this.restoreDate = Long.parseLong((String) serialized.get("restoreDate"));
        }
    }
    
    public Map<String, Object> serialize() {
        return new HashMap<>() {{ 
            put("player", player.toString());
            put("date", date +"");
            put("items", items);
            if (restoreActor != null) {
                put("restoreActor", restoreActor.toString());
                put("restoreDate", restoreDate + "");
            }
        }};
    }
    
    public UUID getPlayer() {
        return player;
    }
    
    public UUID getRestoreActor() {
        return restoreActor;
    }
    
    public void setRestoreActor(UUID restoreActor) {
        this.restoreActor = restoreActor;
    }
    
    public long getDate() {
        return date;
    }
    
    public long getRestoreDate() {
        return restoreDate;
    }
    
    public void setRestoreDate(long restoreDate) {
        this.restoreDate = restoreDate;
    }
    
    public String getItems() {
        return items;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        TrashUse trashUse = (TrashUse) o;
        return date == trashUse.date && Objects.equals(items, trashUse.items) && Objects.equals(player, trashUse.player);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(date, items, player);
    }
}