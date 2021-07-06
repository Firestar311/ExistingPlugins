package com.stardevmc.enforcer.objects.prison;

import com.starmediadev.lib.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.*;

public class Inmate implements ConfigurationSerializable {
    
    private UUID uuid;
    private int prison;
    private String inventory;
    private boolean offline, notifiedOffline;
    
    public Inmate(UUID uuid) {
        this.uuid = uuid;
    }
    
    public Inmate(UUID uuid, int prison, String inventory, boolean offline, boolean notifiedOffline) {
        this.uuid = uuid;
        this.prison = prison;
        this.inventory = inventory;
        this.offline = offline;
        this.notifiedOffline = notifiedOffline;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Inmate inmate = (Inmate) o;
        return Objects.equals(uuid, inmate.uuid);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
    
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("uuid", this.uuid.toString());
        serialized.put("prison", this.prison);
        serialized.put("inventory", this.inventory);
        serialized.put("offline", this.offline);
        serialized.put("notifiedOffline", this.notifiedOffline);
        return serialized;
    }
    
    public Inmate deserialize(Map<String, Object> serialized) {
        UUID uuid = UUID.fromString((String) serialized.get("uuid"));
        int prison = (int) serialized.get("prison");
        String inventory = (String) serialized.get("inventory");
        boolean offline = (boolean) serialized.get("offline");
        boolean notifiedOffline = (boolean) serialized.get("notifiedOffline");
        return new Inmate(uuid, prison, inventory, offline, notifiedOffline);
    }
    public UUID getUuid() {
        return uuid;
    }
    
    public int getPrison() {
        return prison;
    }
    
    public String getInventory() {
        return inventory;
    }
    
    public boolean isOffline() {
        return offline;
    }
    
    public boolean isNotifiedOffline() {
        return notifiedOffline;
    }
    
    public void setPrison(int prison) {
        this.prison = prison;
    }
    
    public void setInventory(String inventory) {
        this.inventory = inventory;
    }
    
    public void setOffline(boolean offline) {
        this.offline = offline;
    }
    
    public void setNotifiedOffline(boolean notifiedOffline) {
        this.notifiedOffline = notifiedOffline;
    }
    
    public void sendMessage(String message) {
        Player player = Bukkit.getPlayer(this.uuid);
        if (player != null) {
            player.sendMessage(Utils.color(message));
        }
    }
    
    public void movePrison(Prison prison) {
    
    }
}