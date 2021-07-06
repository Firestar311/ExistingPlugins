package com.kingrealms.realms.profile;

import com.kingrealms.realms.Realms;
import com.starmediadev.lib.items.InventoryStore;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@SerializableAs("Mailbox")
public class Mailbox implements ConfigurationSerializable {
    
    private List<ItemStack> items = new ArrayList<>();
    private UUID owner;
    private RealmProfile profile;
    
    public Mailbox(UUID owner) {
        this.owner = owner;
    }
    
    public RealmProfile getProfile() {
        if (this.profile == null) {
            this.profile = Realms.getInstance().getProfileManager().getProfile(owner);
        }
        return profile;
    }
    
    public Mailbox(Map<String, Object> serialized) {
        this.owner = UUID.fromString((String) serialized.get("owner"));
        this.items = new ArrayList<>(List.of(InventoryStore.stringToItems((String) serialized.get("items"))));
    }
    
    public Map<String, Object> serialize() {
        return new HashMap<>() {{
            put("owner", owner.toString());
            put("items", InventoryStore.itemsToString(items.toArray(new ItemStack[0])));
        }};
    }
    
    public void addItem(ItemStack itemStack) {
        this.items.add(itemStack);
    }
    
    public void removeItem(ItemStack itemStack) {
        this.items.remove(itemStack);
    }
    
    public ItemStack getItem(int index) {
        return this.items.get(index);
    }
    
    public void removeItem(int index) {
        this.items.remove(index);
    }
    
    public List<ItemStack> getItems() {
        return this.items;
    }
}