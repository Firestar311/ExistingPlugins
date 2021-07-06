package com.kingrealms.realms.kits;

import com.kingrealms.realms.profile.RealmProfile;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@SerializableAs("KitTier")
public class KitTier implements ConfigurationSerializable {
    private int position; //mysql
    private Map<Integer, ItemStack> items = new HashMap<>(); //mysql
    
    public KitTier(int position) {
        this.position = position;
    }
    
    public KitTier(Map<String, Object> serialized) {
        this.position = Integer.parseInt((String) serialized.get("position"));
        serialized.forEach((key, value) -> {
            if (key.startsWith("slot-")) {
                Integer slot = Integer.parseInt(key.split("-")[1]);
                ItemStack itemStack = (ItemStack) value;
                items.put(slot, itemStack);
            }
        });
    }
    
    public Map<String, Object> serialize() {
        return new HashMap<>() {{
            put("position", position + "");
            items.forEach((slot, item) -> put("slot-" + slot, item));
        }};
    }
    
    public void setPosition(int position) {
        this.position = position;
    }
    
    public void setItems(Map<Integer, ItemStack> items) {
        this.items = items;
    }
    
    public int getPosition() {
        return position;
    }
    
    public Map<Integer, ItemStack> getItems() {
        return items;
    }
    
    public void apply(RealmProfile profile) {
        List<ItemStack> notAdded = new ArrayList<>();
        Inventory inv = profile.getInventory();
        this.items.forEach((slot, item) -> {
            ItemStack existing = inv.getItem(slot);
            if (existing == null) {
                inv.setItem(slot, item);
            } else {
                notAdded.add(item);
            }
        });
        
        if (!notAdded.isEmpty()) {
            for (ItemStack itemStack : notAdded) {
                inv.addItem(itemStack);
            }
        }
    }
    
    public void addItem(int i, ItemStack item) {
        this.items.put(i, item);
    }
}