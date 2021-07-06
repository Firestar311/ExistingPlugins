package com.kingrealms.realms.skills.farming;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.items.CustomItemRegistry;
import com.kingrealms.realms.items.type.CropItem;
import com.kingrealms.realms.items.type.CropScrap;
import com.kingrealms.realms.loot.LootTable;
import com.kingrealms.realms.loot.Rarity;
import com.kingrealms.realms.profile.RealmProfile;
import com.kingrealms.realms.skills.SkillType;
import com.kingrealms.realms.territory.base.Territory;
import com.kingrealms.realms.territory.enums.Relation;
import com.kingrealms.realms.util.RealmsLoot;
import com.starmediadev.lib.pagination.IElement;
import com.starmediadev.lib.util.*;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.*;
import java.util.concurrent.TimeUnit;

@SerializableAs("CropBlock")
public abstract class CropBlock implements ConfigurationSerializable, IElement, CommandViewable {
    
    protected long lastGrowth = 0; //mysql
    protected long nextGrowth; //mysql
    protected Location soilLocation; //mysql
    protected CropType type; //mysql
    protected UUID owner;
    protected long date;
    
    public CropBlock(CropType type) {
        this.type = type;
    }
    
    public CropBlock(CropType type, Location soilLocation) {
        this.soilLocation = soilLocation;
        this.type = type;
    }
    
    public FarmingLevel getLevel() {
        return Realms.getInstance().getSkillManager().getFarmingSkill().getLevel(this.type);
    }
    
    @Override
    public Map<String, String> getDisplayMap() {
        return new LinkedHashMap<>() {{
            put("Owner", Realms.getInstance().getProfileManager().getProfile(owner).getName());
            put("Placed", Constants.DATE_FORMAT.format(new Date(date)));
            put("Type", Utils.capitalizeEveryWord(type.name()));
            put("Last Growth", Utils.formatTime(System.currentTimeMillis() - lastGrowth) + " ago");
            put("Next Growth", Utils.formatTime(nextGrowth - System.currentTimeMillis()));
            put("Soil Location", Utils.locationToString(soilLocation));
        }};
    }
    
    public CropBlock(Map<String, Object> serialized) {
        this.soilLocation = (Location) serialized.get("soilLocation");
        this.type = CropType.valueOf((String) serialized.get("type"));
        if (serialized.containsKey("owner")) {
            this.owner = UUID.fromString((String) serialized.get("owner"));
        }
        if (serialized.containsKey("date")) {
            this.date = Long.parseLong((String) serialized.get("date"));
        }
    }
    
    public boolean contains(Location location) {
        return this.soilLocation.equals(location);
    }
    
    public abstract void grow();
    
    public abstract void onPlace();
    
    public abstract void onBreak();
    
    public abstract void onHarvest(Location location, RealmProfile profile);
    
    public LootTable getLootTable() {
        LootTable lootTable = new LootTable(type.name().toLowerCase() + "_table", 1, 2);
        lootTable.addPossibleLoot(new RealmsLoot(getCropItem().getDropItem(), Rarity.COMMON));
        lootTable.addPossibleLoot(new RealmsLoot(getScrap(), Rarity.LEGENDARY));
        return lootTable;
    }
    
    public boolean canHarvest(Location location, RealmProfile profile) {
        return checkLevel(profile) && !checkClaim(location, profile, true);
    }
    
    public boolean checkLevel(RealmProfile profile) {
        FarmingLevel level = Realms.getInstance().getSkillManager().getFarmingSkill().getLevel(this.type);
        Double playerXp = profile.getSkillExperience().get(SkillType.FARMING);
        if (playerXp == null) return false;
        return playerXp >= level.getTotalXpNeeded();
    }
    
    public boolean checkClaim(Location location, RealmProfile profile, boolean ignoreServerClaim) {
        Realms plugin = Realms.getInstance();
        if (!ignoreServerClaim) {
            if (plugin.getSpawn().contains(location) || plugin.getWarzone().contains(location)) {
                return true;
            }
        }
        
        Territory territory = plugin.getTerritoryManager().getTerritory(location);
        if (territory != null) {
            if (territory.isMember(profile)) {
                return false;
            } else {
                Territory playerTerritory = plugin.getTerritoryManager().getTerritory(profile);
                Relation relationship = territory.getRelationship(playerTerritory);
                return relationship != Relation.ALLY;
            }
        }
        
        return false;
    }
    
    public void dropScrap(RealmProfile profile) {
        if (!Realms.getInstance().getSpawn().contains(this.getSoilLocation()) || !Realms.getInstance().getWarzone().contains(getSoilLocation())) {
            int v = new Random().nextInt(100);
            if (v < 50) {
                profile.getInventory().addItem(getScrap().getItemStack());
            }
        }
    }
    
    public CropScrap getScrap() {
        return CustomItemRegistry.CROP_SCRAPS.getItem(this.type);
    }
    
    public CropItem getCropItem() {
        return CustomItemRegistry.CROP_ITEMS.getItem(this.type);
    }
    
    public long getNextGrowth() {
        return nextGrowth;
    }
    
    public CropType getType() {
        return type;
    }
    
    public void generateNextGrowth() {
        Random random = new Random();
        int nextGrowth = random.nextInt(Realms.getInstance().getServerMode().getMaxCropGrow()) + Realms.getInstance().getServerMode().getMinCropGrow();
        this.nextGrowth = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(nextGrowth);
        //this.nextGrowth = TimeUnit.SECONDS.toMillis(1);
    }
    
    public Map<String, Object> serialize() {
        return new HashMap<>() {{
            put("soilLocation", soilLocation);
            put("type", type.name());
            if (owner != null) {
                put("owner", owner.toString());
            }
            put("date", date + "");
        }};
    }
    
    public Location getSoilLocation() {
        return soilLocation;
    }
    
    public void setSoilLocation(Location soilLocation) {
        this.soilLocation = soilLocation;
    }
    
    public long getLastGrowth() {
        return lastGrowth;
    }
    
    public long getDate() {
        return date;
    }
    
    public void setDate(long date) {
        this.date = date;
    }
    
    public void setLastGrowth(long lastGrowth) {
        this.lastGrowth = lastGrowth;
    }
    
    @Override
    public String formatLine(String... args) {
        int x = soilLocation.getBlockX(), y = soilLocation.getBlockY(), z = soilLocation.getBlockZ();
        return " &8- &a" + Utils.capitalizeEveryWord(this.type.name()) + " &6Crop Block at &a(" + x + ", " + y + ", " + z + ") &6grows in &a" + Utils.formatTime(nextGrowth - System.currentTimeMillis());
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(soilLocation);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        CropBlock cropBlock = (CropBlock) o;
        return Objects.equals(soilLocation, cropBlock.soilLocation);
    }
    
    public UUID getOwner() {
        return owner;
    }
    
    public void setOwner(UUID owner) {
        this.owner = owner;
    }
}   