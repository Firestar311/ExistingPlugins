package com.kingrealms.realms.skills.woodcutting;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.items.CustomItemRegistry;
import com.kingrealms.realms.loot.LootTable;
import com.kingrealms.realms.loot.Rarity;
import com.kingrealms.realms.util.RealmsLoot;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Sapling;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.Map.Entry;

@SerializableAs("ArcaneTree")
public class ArcaneTree implements ConfigurationSerializable {
    private Location baseLocation;
    private int id;
    private TreeType treeType;
    private Set<Location> woodLocations = new HashSet<>(), leafLocations = new HashSet<>();
    private UUID owner;
    private long date;
    
    public ArcaneTree(Location baseLocation, TreeType treeType) {
        this.baseLocation = baseLocation;
        this.treeType = treeType;
    }
    
    public ArcaneTree(Map<String, Object> serialized) {
        this.id = Integer.parseInt((String) serialized.get("id"));
        this.baseLocation = (Location) serialized.get("baseLocation");
        this.treeType = TreeType.valueOf((String) serialized.get("treeType"));
        for (Entry<String, Object> entry : serialized.entrySet()) {
            if (entry.getKey().startsWith("logLocation")) {
                Location location = (Location) entry.getValue();
                this.woodLocations.add(location);
            }
            
            if (entry.getKey().startsWith("leafLocation")) {
                Location location = (Location) entry.getValue();
                this.leafLocations.add(location);
            }
        }
        this.owner = UUID.fromString((String) serialized.get("owner"));
        this.date = Long.parseLong((String) serialized.get("date"));
    }
    
    public LootTable getLootTable() {
        LootTable lootTable = new LootTable(this.treeType.name().toLowerCase() + "_table", this.woodLocations.size(), this.woodLocations.size() + 1);
        lootTable.addPossibleLoot(new RealmsLoot(CustomItemRegistry.TREE_DROPS.getItem(treeType), Rarity.COMMON));
        lootTable.addPossibleLoot(new RealmsLoot(CustomItemRegistry.ARCANE_SAPLINGS.getItem(treeType), Rarity.UNCOMMON));
        lootTable.addPossibleLoot(new RealmsLoot(CustomItemRegistry.WOOD_CHIPS.getItem(treeType), Rarity.LEGENDARY));
        return lootTable;
    }
    
    public void removeTree() {
        for (Location location : getWoodLocations()) {
            location.getBlock().setType(Material.AIR);
        }
    
        for (Location location : getLeafLocations()) {
            location.getBlock().setType(Material.AIR);
        }
        
        woodLocations.clear();
        
        new BukkitRunnable() {
            public void run() {
                getBaseLocation().getBlock().setType(treeType.getSapling());
            }
        }.runTaskLater(Realms.getInstance(), 1L);
    }
    
    public Set<Location> getWoodLocations() {
        return woodLocations;
    }
    
    public Location getBaseLocation() {
        return baseLocation;
    }
    
    public boolean isSapling() {
        Block block = baseLocation.getBlock();
        return block.getBlockData() instanceof Sapling;
    }
    
    public Map<String, Object> serialize() {
        return new HashMap<>() {{
            put("id", id + "");
            put("baseLocation", baseLocation);
            put("treeType", treeType.name());
            int counter = 0;
            for (Location location : woodLocations) {
                put("logLocation" + counter, location);
                counter++;
            }
            
            counter = 0;
            for (Location location : leafLocations) {
                put("leafLocation" + counter, location);
                counter++;
            }
            put("owner", owner.toString());
            put("date", date + "");
        }};
    }
    
    public long getDate() {
        return date;
    }
    
    public void setDate(long date) {
        this.date = date;
    }
    
    public boolean contains(Location location) {
        if (baseLocation.equals(location)) { return true; }
        for (Location loc : woodLocations) {
            if (loc.equals(location)) { return true; }
        }
        
        for (Location loc : leafLocations) {
            if (loc.equals(location)) {return true;}
        }
        return false;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public TreeType getTreeType() {
        return treeType;
    }
    
    public void addWoodLocation(Location location) {
        this.woodLocations.add(location);
    }
    
    public void addLeafLocation(Location location) {
        this.leafLocations.add(location);
    }
    
    public Set<Location> getLeafLocations() {
        return leafLocations;
    }
    
    public UUID getOwner() {
        return owner;
    }
    
    public void setOwner(UUID owner) {
        this.owner = owner;
    }
}