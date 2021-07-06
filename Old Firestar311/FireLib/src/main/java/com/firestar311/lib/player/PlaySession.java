package com.firestar311.lib.player;

import com.firestar311.lib.items.InventoryStore;
import org.bukkit.*;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.Map.Entry;

public class PlaySession implements ConfigurationSerializable {
    protected SortedMap<Long, String> chatMessages = new TreeMap<>(), commands = new TreeMap<>();
    protected SortedMap<Long, DeathSnapshot> deaths = new TreeMap<>();
    protected long loginTime, logoutTime = -1;
    protected Location loginLocation, logoutLocation;
    protected UUID player;
    protected int logoutFoodLevel;
    protected GameMode logoutGamemode;
    protected double logoutHealth;
    protected float logoutSaturation;
    protected String logoutInventory, logoutEnderchest;
    
    private ItemStack[] inventoryItems, enderchestItems;
    
    public boolean restoreLogoutInfo() {
        Player player = Bukkit.getPlayer(this.player);
        if (player == null) {
            return false;
        }
        
        player.getInventory().setContents(getLogoutInventory());
        player.getEnderChest().setContents(getLogoutEnderchest());
        player.setFoodLevel(this.getLogoutFoodLevel());
        player.setGameMode(this.getLogoutGamemode());
        player.setHealth(this.getLogoutHealth());
        player.setSaturation(this.getLogoutSaturation());
        return true;
    }
    
    public PlaySession(UUID player, long loginTime, Location location) {
        this.player = player;
        this.loginTime = loginTime;
        this.loginLocation = location;
    }
    
    public void addChatMessage(long time, String message) {
        this.chatMessages.put(time, message);
    }
    
    public void addCommand(long time, String command) {
        this.commands.put(time, command);
    }
    
    public void addDeath(DeathSnapshot snapshot) {
        this.deaths.put(snapshot.getTime(), snapshot);
    }
    
    /*
    protected SortedMap<Long, String> chatMessages = new TreeMap<>(), commands = new TreeMap<>();
    protected SortedMap<Long, DeathSnapshot> deaths = new TreeMap<>();
    protected Location loginLocation, logoutLocation;
    protected UUID player;
    protected int logoutFoodLevel;
    protected GameMode logoutGamemode;
    protected double logoutHealth;
    protected float logoutSaturation;
    protected String logoutInventory, logoutEnderchest;
     */
    public static PlaySession deserialize(Map<String, Object> serialized) {
        UUID player = UUID.fromString((String) serialized.get("player"));
        long loginTime = Long.parseLong((String) serialized.get("loginTime"));
        long logoutTime = Long.parseLong((String) serialized.get("logoutTime"));
        Location loginLocation = (Location) serialized.get("loginLocation");
        Location logoutLocation = (Location) serialized.get("logoutLocation");
        int logoutFoodLevel = Integer.parseInt((String) serialized.get("logoutFoodLevel"));
        GameMode logoutGamemode = GameMode.valueOf((String) serialized.get("logoutGamemode"));
        double logoutHealth = Double.parseDouble((String) serialized.get("logoutHealth"));
        float logoutSaturation = Float.parseFloat((String) serialized.get("logoutSaturation"));
        String logoutInventory = (String) serialized.get("logoutInventory");
        String logoutEnderchest = (String) serialized.get("logoutEnderchest");
        SortedMap<Long, String> chatMessages = new TreeMap<>(), commands = new TreeMap<>();
        SortedMap<Long, DeathSnapshot> deaths = new TreeMap<>();
        for (Entry<String, Object> entry : serialized.entrySet()) {
            if (entry.getKey().contains("chat-")) {
                long time = Long.parseLong(entry.getKey().split("-")[1]);
                chatMessages.put(time, (String) entry.getValue());
            } else if (entry.getKey().contains("cmd-")) {
                long time = Long.parseLong(entry.getKey().split("-")[1]);
                commands.put(time, (String) entry.getValue());
            } else if (entry.getKey().contains("deaths-")) {
                long time = Long.parseLong(entry.getKey().split("-")[1]);
                deaths.put(time, (DeathSnapshot) entry.getValue());
            }
        }
        
        PlaySession playSession = new PlaySession(player, loginTime, loginLocation);
        playSession.logoutTime = logoutTime;
        playSession.logoutLocation = logoutLocation;
        playSession.logoutFoodLevel = logoutFoodLevel;
        playSession.logoutGamemode = logoutGamemode;
        playSession.logoutHealth = logoutHealth;
        playSession.logoutSaturation = logoutSaturation;
        playSession.logoutInventory = logoutInventory;
        playSession.logoutEnderchest = logoutEnderchest;
        playSession.chatMessages = chatMessages;
        playSession.commands = commands;
        playSession.deaths = deaths;
        return playSession;
    }
    
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("loginTime", this.loginTime + "");
        serialized.put("logoutTime", this.logoutTime + "");
        serialized.put("loginLocation", this.loginLocation);
        serialized.put("logoutLocation", this.logoutLocation);
        serialized.put("player", this.player.toString());
        serialized.put("logoutGamemode", this.logoutGamemode.name());
        serialized.put("logoutFoodLevel", this.logoutFoodLevel + "");
        serialized.put("logouthealth", this.logoutHealth + "");
        serialized.put("logoutSaturation", this.logoutSaturation);
        serialized.put("logoutInventory", this.logoutInventory);
        serialized.put("logoutEnderchest", this.logoutEnderchest);
        for (Entry<Long, String> entry : chatMessages.entrySet()) {
            serialized.put("chat-" + entry.getKey(), entry.getValue());
        }
        for (Entry<Long, String> entry : commands.entrySet()) {
            serialized.put("cmd-" + entry.getKey(), entry.getValue());
        }
        for (Entry<Long, DeathSnapshot> entry : deaths.entrySet()) {
            serialized.put("death-" + entry.getKey(), entry.getValue());
        }
        return serialized;
    }
    
    public void setLogoutInfo(long logout, Player player) {
        this.logoutTime = logout;
        this.logoutLocation = player.getLocation();
        this.logoutGamemode = player.getGameMode();
        this.logoutHealth = player.getHealth();
        this.logoutSaturation = player.getSaturation();
        this.logoutFoodLevel = player.getFoodLevel();
        this.logoutInventory = InventoryStore.itemsToString(player.getInventory().getContents());
        this.logoutEnderchest = InventoryStore.itemsToString(player.getEnderChest().getContents());
    }
    
    public long getLoginTime() {
        return loginTime;
    }
    
    public long getLogoutTime() {
        return logoutTime;
    }
    
    public Location getLoginLocation() {
        return loginLocation;
    }
    
    public Location getLogoutLocation() {
        return logoutLocation;
    }
    
    public UUID getPlayer() {
        return player;
    }
    
    public int getLogoutFoodLevel() {
        return logoutFoodLevel;
    }
    
    public GameMode getLogoutGamemode() {
        return logoutGamemode;
    }
    
    public double getLogoutHealth() {
        return logoutHealth;
    }
    
    public float getLogoutSaturation() {
        return logoutSaturation;
    }
    
    public ItemStack[] getLogoutInventory() {
        if (this.inventoryItems == null) {
            this.inventoryItems = InventoryStore.stringToItems(this.logoutInventory);
        }
        return this.inventoryItems;
    }
    
    public ItemStack[] getLogoutEnderchest() {
        if (this.enderchestItems == null) {
            this.enderchestItems = InventoryStore.stringToItems(this.logoutEnderchest);
        }
        return this.enderchestItems;
    }
}