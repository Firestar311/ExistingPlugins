package com.starmediadev.lib.user;

import com.starmediadev.lib.items.InventoryStore;
import com.starmediadev.lib.pagination.IElement;
import com.starmediadev.lib.user.damage.*;
import com.starmediadev.lib.util.Constants;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@SerializableAs("DeathSnapshot")
public class DeathSnapshot implements ConfigurationSerializable, IElement, Comparable<DeathSnapshot> {
    private int id;
    private final UUID player;
    private final long time;
    private final Location location;
    private DamageInfo damageInfo;
    private final String inventory;
    private final int level;
    private final float exp;
    
    public DeathSnapshot(Player player, long time) {
        this(-1, player.getUniqueId(), time, player.getLocation(), player.getLastDamageCause(), InventoryStore.itemsToString(player.getInventory().getContents()), player.getLevel(), player.getExp());
    }
    
    public DeathSnapshot(int id, UUID player, long time, Location location, DamageInfo damageInfo, String inventory, int level, float exp) {
        this(id, player, time, location, inventory, level, exp);
        this.damageInfo = damageInfo;
    }
    
    public DeathSnapshot(int id, UUID player, long time, Location location, String inventory, int level, float exp) {
        this.id = id;
        this.player = player;
        this.time = time;
        this.location = location;
        this.inventory = inventory;
        this.level = level;
        this.exp = exp;
    }
    
    public DeathSnapshot(int id, UUID uniqueId, long time, Location location, EntityDamageEvent lastDamageCause, String inventory, int level, float exp) {
        this(id, uniqueId, time, location, inventory, level, exp);
        
        if (lastDamageCause instanceof EntityDamageByBlockEvent && (lastDamageCause.getCause() != DamageCause.LAVA)) {
            this.damageInfo = new BlockDamageInfo(lastDamageCause);
        } else if (lastDamageCause instanceof EntityDamageByEntityEvent) {
            if (((EntityDamageByEntityEvent) lastDamageCause).getDamager() instanceof Player) {
                this.damageInfo = new EntityDamagePlayerInfo(lastDamageCause);
            } else {
                this.damageInfo = new EntityDamageInfo(lastDamageCause);
            }
        } else {
            this.damageInfo = new DamageInfo(lastDamageCause.getCause(), lastDamageCause.getDamage());
        }
    }
    
    @Override
    public String formatLine(String... args) {
        String time = Constants.DATE_FORMAT.format(new Date(this.time));
        return " &8- &e" + id + ": &c" + this.damageInfo.getCause().name() + " &bat &d" + time;
    }
    
    public static DeathSnapshot deserialize(Map<String, Object> serialized) {
        int id = -1;
        if (serialized.containsKey("id")) {
            id = Integer.parseInt((String) serialized.get("id"));
        }
        UUID player = UUID.fromString((String) serialized.get("player"));
        long time = Long.parseLong((String) serialized.get("time"));
        Location location = null;
        if (serialized.containsKey("location")) {
            location = (Location) serialized.get("location");
        }
        
        DamageInfo info;
        if (serialized.containsKey("damageInfo")) {
            info = (DamageInfo) serialized.get("damageInfo");
        } else {
            DamageCause cause = DamageCause.valueOf((String) serialized.get("damageCause"));
            info = new DamageInfo(cause, -1);
        }
        
        String inventory = (String) serialized.get("inventory");
        float exp = 0;
        try {
            exp = Float.parseFloat((String) serialized.get("exp"));
        } catch (Exception e) {}
        int level = 0;
        try {
            level = Integer.parseInt((String) serialized.get("level"));
        } catch (Exception e) {}
        
        return new DeathSnapshot(id, player, time, location, info, inventory, level, exp);
    }
    
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("id", this.id + "");
        serialized.put("player", this.player.toString());
        serialized.put("time", this.time + "");
        serialized.put("damageInfo", this.damageInfo);
        serialized.put("location", this.location);
        serialized.put("inventory", this.inventory);
        serialized.put("exp", this.exp + "");
        serialized.put("level", this.level + "");
        return serialized;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, player);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        DeathSnapshot snapshot = (DeathSnapshot) o;
        return id == snapshot.id && Objects.equals(player, snapshot.player);
    }
    
    @Override
    public int compareTo(DeathSnapshot o) {
        return Integer.compare(o.getId(), this.getId());
    }
    
    public Map<String, String> getDisplayMap() {
        return new LinkedHashMap<>() {{
            put("ID", id + "");
            put("Date", Constants.DATE_FORMAT.format(new Date(time)));
            String world = location.getWorld().getName();
            int x = location.getBlockX(), y = location.getBlockY(), z = location.getBlockZ();
            put("Cause", damageInfo.toString());
            put("Location", world + " (" + x + ", " + y + ", " + z + ")");
            put("Inventory", (!StringUtils.isEmpty(inventory) ? "Yes" : "Empty"));
            put("Level", level + "");
            put("Exp", exp + "");
        }};
    }
    
    public UUID getPlayer() {
        return player;
    }
    
    public long getTime() {
        return time;
    }
    
    public DamageInfo getDamageInfo() {
        return damageInfo;
    }
    
    public String getInventory() {
        return inventory;
    }
    
    public ItemStack[] getItems() {
        return InventoryStore.stringToItems(this.inventory);
    }
    
    public float getExp() {
        return exp;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public Location getLocation() {
        return location;
    }
    
    public int getLevel() {
        return level;
    }
}