package com.kingrealms.realms.supplydrops;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.loot.Loot;
import com.kingrealms.realms.loot.LootTable;
import com.kingrealms.realms.profile.RealmProfile;
import com.starmediadev.lib.pagination.IElement;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@SerializableAs("SupplyCrate")
public class SupplyCrate implements IElement, ConfigurationSerializable {
    private int id;
    private Location location;
    private LootTable lootTable;
    
    public SupplyCrate(Location location, LootTable lootTable) {
        this.location = location.clone();
        this.lootTable = lootTable;
    }
    
    public SupplyCrate(Map<String, Object> serialized) {
        this.id = Integer.parseInt((String) serialized.get("id"));
        this.location = (Location) serialized.get("location");
        this.lootTable = (LootTable) serialized.get("loottable");
    }
    
    public Map<String, Object> serialize() {
        return new HashMap<>() {{ 
            put("id", id + "");
            put("location", location);
            put("loottable", lootTable);
        }};
    }
    
    @SuppressWarnings("unused")
    public void openCrate(RealmProfile profile) {
        Block block = location.getBlock();
        if (block == null) {
            Realms.getInstance().getSupplyDropManager().removeCrate(id);   
            return;
        }
        
        if (block.getType() != Material.CHEST) {
            block.setType(Material.CHEST);
        }
    
        Chest chest = (Chest) block.getState();
        Inventory inventory = chest.getBlockInventory();
        boolean empty = true;
        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.getType() != Material.AIR) {
                empty = false;
                break;
            }
        }
        
        if (empty) {
            List<Loot> loot = this.lootTable.generateLoot();
            Random random = new Random();
            for (Loot l : loot) {
                int slot;
                do {
                    slot = random.nextInt(inventory.getSize());
                } while (inventory.getItem(slot) != null);
                
                inventory.setItem(slot, l.getItemStack());
            }
        }
    }
    
    public Location getLocation() {
        return location;
    }
    
    public LootTable getLootTable() {
        return lootTable;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    @Override
    public String formatLine(String... args) {
        int x = location.getBlockX(), y = location.getBlockY(), z = location.getBlockZ();
        return " &8- &aSupply Crate at &a(" + x + ", " + y + ", " + z + ")";
    }
}