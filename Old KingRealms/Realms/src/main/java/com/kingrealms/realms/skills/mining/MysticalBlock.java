package com.kingrealms.realms.skills.mining;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.items.CustomItemRegistry;
import com.kingrealms.realms.loot.LootTable;
import com.kingrealms.realms.loot.Rarity;
import com.kingrealms.realms.util.RealmsLoot;
import com.starmediadev.lib.pagination.IElement;
import com.starmediadev.lib.util.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.*;

public class MysticalBlock implements ConfigurationSerializable, IElement, CommandViewable {
    private long date, lastMined;
    private ResourceType resourceType;
    private Location location; //mysql
    private Material material; //mysql
    private UUID owner;
    
    public MysticalBlock(ResourceType resourceType, Location location, UUID owner, long date) {
        this.resourceType = resourceType;
        this.location = location;
        this.owner = owner;
        this.date = date;
    }
    
    public MysticalBlock(Map<String, Object> serialized) {
        this.material = Material.valueOf((String) serialized.get("material"));
        this.location = (Location) serialized.get("location");
        if (serialized.containsKey("lastMined")) {
            this.lastMined = Long.parseLong((String) serialized.get("lastMined"));
        }
        
        if (serialized.containsKey("resourceType")) {
            try {
                this.resourceType = ResourceType.valueOf((String) serialized.get("resourceType"));
            } catch (Exception e) {
                this.resourceType = ResourceType.getType(null, material);
            }
        }
        
        if (serialized.containsKey("owner")) {
            this.owner = UUID.fromString((String) serialized.get("owner"));
        }
        
        if (serialized.containsKey("date")) {
            this.date = Long.parseLong((String) serialized.get("date"));
        }
    }
    
    @Override
    public Map<String, String> getDisplayMap() {
        return new LinkedHashMap<>() {{
            if (owner != null) { put("Owner", Realms.getInstance().getProfileManager().getProfile(owner).getName()); }
            put("Date", Constants.DATE_FORMAT.format(new Date(date)));
            put("Last Mined", Utils.formatTime(System.currentTimeMillis() - lastMined));
            put("Location", Utils.locationToString(location));
            put("Material", MaterialNames.getName(material));
            put("Type", resourceType.name());
        }};
    }
    
    public Map<String, Object> serialize() {
        return new HashMap<>() {{
            put("material", material.name());
            put("location", location);
            put("resourceType", resourceType);
            put("lastMined", lastMined + "");
            if (owner != null) {
                put("owner", owner.toString());
            }
            put("date", date + "");
        }};
    }
    
    public long getDate() {
        return date;
    }
    
    public void setDate(long date) {
        this.date = date;
    }
    
    public Location getLocation() {
        return location;
    }
    
    public LootTable getLootTable() {
        LootTable lootTable = new LootTable(this.resourceType.name().toLowerCase() + "_table", 1, 2);
        lootTable.addPossibleLoot(new RealmsLoot(CustomItemRegistry.RESOURCES.getItem(resourceType.getDrop()), Rarity.COMMON));
        lootTable.addPossibleLoot(new RealmsLoot(CustomItemRegistry.MYSTICAL_SLIVERS.getItem(resourceType), Rarity.LEGENDARY));
        return lootTable;
    }
    
    public Material getMaterial() {
        return material;
    }
    
    @Override
    public String formatLine(String... args) {
        int x = location.getBlockX(), y = location.getBlockY(), z = location.getBlockZ();
        return " &8- &a" + MaterialNames.getName(this.material).replace(" Ore", "") + " &6Mystial Resource at &a(" + x + ", " + y + ", " + z + ") &6regenerates in &a" + Utils.formatTime((lastMined + resourceType.getCooldown().toMilliseconds()) - System.currentTimeMillis());
    }
    
    public UUID getOwner() {
        return owner;
    }
    
    public void setOwner(UUID owner) {
        this.owner = owner;
    }
    
    public ResourceType getResourceType() {
        return resourceType;
    }
    
    public boolean isCooldownExpired() {
        return resourceType.getCooldown().isExpired(lastMined + resourceType.getCooldown().toMilliseconds());
    }
    
    public long getLastMined() {
        return lastMined;
    }
    
    public void setLastMined(long lastMined) {
        this.lastMined = lastMined;
    }
}