package com.firestar311.lib.player;

import com.firestar311.lib.items.InventoryStore;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import java.util.*;

public class DeathSnapshot implements ConfigurationSerializable {
    private UUID player;
    private long time;
    private DamageCause deathCause;
    private String inventory;
    private int exp;
    
    public DeathSnapshot(Player player, long time) {
        this.player = player.getUniqueId();
        this.time = time;
        this.deathCause = player.getLastDamageCause().getCause();
        this.inventory = InventoryStore.itemsToString(player.getInventory().getContents());
        this.exp = player.getTotalExperience();
    }
    
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("player", this.player.toString());
        serialized.put("time", this.time + "");
        serialized.put("deathCause", this.deathCause.name());
        serialized.put("inventory", this.inventory);
        serialized.put("exp", this.exp + "");
        return serialized;
    }
    
    public static DeathSnapshot deserialize(Map<String, Object> serialized) {
        UUID player = UUID.fromString((String) serialized.get("player"));
        long time = Long.parseLong((String) serialized.get("time"));
        DamageCause deathCause = DamageCause.valueOf((String) serialized.get("deathCause"));
        String inventory = (String) serialized.get("inventory");
        int exp = Integer.parseInt((String) serialized.get("exp"));
        return null;
    }
    
    public UUID getPlayer() {
        return player;
    }
    
    public long getTime() {
        return time;
    }
    
    public DamageCause getDeathCause() {
        return deathCause;
    }
    
    public String getInventory() {
        return inventory;
    }
    
    public int getExp() {
        return exp;
    }
}