package com.starmediadev.lib.user;

import com.starmediadev.lib.items.InventoryStore;
import com.starmediadev.lib.pagination.IElement;
import com.starmediadev.lib.util.Constants;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.Map.Entry;

@SerializableAs("PlaySession")
public class PlaySession implements ConfigurationSerializable, IElement, Comparable<PlaySession> {
    @Deprecated
    protected SortedMap<Long, DeathSnapshot> deaths = new TreeMap<>();
    protected Location loginLocation, logoutLocation;
    protected long loginTime, logoutTime = -1;
    protected int logoutFoodLevel;
    protected GameMode logoutGamemode;
    protected double logoutHealth;
    protected String logoutInventory, logoutEnderchest;
    protected float logoutSaturation;
    protected UUID player;
    private ItemStack[] inventoryItems, enderchestItems;
    protected int id = -1;
    
    public PlaySession(UUID player, long loginTime, Location location) {
        this.player = player;
        this.loginTime = loginTime;
        this.loginLocation = location;
    }
    
    @Override
    public String formatLine(String... args) {
        boolean current = logoutTime == -1;
        String time = Constants.DATE_FORMAT.format(new Date(this.loginTime)) + " &b- " + ((current) ? "&dCurrent" : "&c" + Constants.DATE_FORMAT.format(new Date(this.logoutTime)));
        return " &8- &e" + id + ": &a" + time;
    }
    
    @Override
    public int compareTo(PlaySession o) {
        return Integer.compare(o.getId(), this.getId());
    }
    
    public Map<String, String> getDisplayMap() {
        return new LinkedHashMap<>() {{
            put("Login", Constants.DATE_FORMAT.format(new Date(loginTime)));
            put("Logout", (logoutTime == -1) ? "Online" : Constants.DATE_FORMAT.format(new Date(logoutTime)));
            if (logoutTime != -1) {
                put("Logout Food", logoutFoodLevel + "");
                put("Logout Health", logoutHealth + "");
                put("Logout Gamemode", logoutGamemode.name());
                put("Logout Saturation", logoutSaturation + "");
                put("Logout Inventory", (StringUtils.isEmpty(logoutInventory) ? "Yes" : "Empty"));
                put("Logout Enderchest", (StringUtils.isEmpty(logoutEnderchest) ? "Yes" : "Empty"));
            }
        }};
    }
    
    public static PlaySession deserialize(Map<String, Object> serialized) {
        int id;
        UUID player = UUID.fromString((String) serialized.get("player"));
        long loginTime = Long.parseLong((String) serialized.get("loginTime"));
        long logoutTime = Long.parseLong((String) serialized.get("logoutTime"));
        Location loginLocation = (Location) serialized.get("loginLocation");
        Location logoutLocation = (Location) serialized.get("logoutLocation");
        int logoutFoodLevel = Integer.parseInt((String) serialized.get("logoutFoodLevel"));
        GameMode logoutGamemode = GameMode.valueOf((String) serialized.get("logoutGamemode"));
        double logoutHealth = Double.parseDouble((String) serialized.get("logouthealth"));
        float logoutSaturation = Float.parseFloat((String) serialized.get("logoutSaturation"));
        String logoutInventory = (String) serialized.get("logoutInventory");
        String logoutEnderchest = (String) serialized.get("logoutEnderchest");
        SortedMap<Long, DeathSnapshot> deaths = new TreeMap<>();
        for (Entry<String, Object> entry : serialized.entrySet()) {
            if (entry.getKey().contains("death-")) {
                long time = Long.parseLong(entry.getKey().split("-")[1]);
                System.out.println("Loaded death for " + player.toString() + ": " + time);
                deaths.put(time, (DeathSnapshot) entry.getValue());
            }
        }
        
        if (serialized.containsKey("id")) {
            id = Integer.parseInt((String) serialized.get("id"));
        } else {
            id = -1;
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
        playSession.deaths = deaths;
        playSession.id = id;
        return playSession;
    }
    
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
    
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("id", this.id + "");
        serialized.put("loginTime", this.loginTime + "");
        serialized.put("logoutTime", this.logoutTime + "");
        serialized.put("loginLocation", this.loginLocation);
        serialized.put("logoutLocation", this.logoutLocation);
        serialized.put("player", this.player.toString());
        serialized.put("logoutGamemode", this.logoutGamemode.name());
        serialized.put("logoutFoodLevel", this.logoutFoodLevel + "");
        serialized.put("logouthealth", this.logoutHealth + "");
        serialized.put("logoutSaturation", this.logoutSaturation + "");
        serialized.put("logoutInventory", this.logoutInventory);
        serialized.put("logoutEnderchest", this.logoutEnderchest);
        return serialized;
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
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        PlaySession session = (PlaySession) o;
        return id == session.id && Objects.equals(player, session.player);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(player, id);
    }
}