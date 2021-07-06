package com.firestar311.lib.player;

import com.firestar311.lib.util.Utils;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.Map.Entry;

public class User implements ConfigurationSerializable {
    
    protected Set<String> ipAddresses = new HashSet<>();
    protected String lastName;
    protected Set<PlaySession> playSessions = new HashSet<>();
    protected UUID uuid;
    
    public User(UUID uuid) {
        this.uuid = uuid;
    }
    
    public void addIpAddress(String ipAddress) {
        this.ipAddresses.add(ipAddress.split(":")[0].replace("/", ""));
    }
    
    public void addIpAddresses(List<String> ipAddresses) {
        this.ipAddresses.addAll(ipAddresses);
    }
    
    public int hashCode() {
        return Objects.hash(uuid);
    }
    
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        User that = (User) o;
        return Objects.equals(uuid, that.uuid);
    }
    
    public void sendMessage(String message) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            player.sendMessage(Utils.color(message));
        }
    }
    
    public void sendMessage(BaseComponent... components) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            player.spigot().sendMessage(components);
        }
    }
    
    public void addPlaySession(PlaySession session) {
        this.playSessions.add(session);
    }
    
    
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("uuid", this.uuid.toString());
        serialized.put("lastname", this.lastName);
        serialized.put("ipAddresses", this.ipAddresses);
        for (PlaySession session : this.playSessions) {
            serialized.put("session" + session.getLoginTime() + "-" + session.getLogoutTime(), session);
        }
        return serialized;
    }
    
    public static User deserialize(Map<String, Object> serialized) {
        UUID uuid = UUID.fromString((String) serialized.get("uuid"));
        String lastName = (String) serialized.get("lastName");
        Set<String> ipAddresses = (Set<String>) serialized.get("ipAddresses");
        Set<PlaySession> sessions = new HashSet<>();
        for (Entry<String, Object> entry : serialized.entrySet()) {
            if (entry.getKey().contains("session")) {
                sessions.add((PlaySession) entry.getValue());
            }
        }
        
        User user = new User(uuid);
        user.setLastName(lastName);
        user.ipAddresses = ipAddresses;
        user.playSessions = sessions;
        return null;
    }
    
    public UUID getUniqueId() {
        return uuid;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public List<String> getIpAddresses() {
        return new ArrayList<>(this.ipAddresses);
    }
    
    public PlaySession getPlaySession(long time) {
        for (PlaySession session : this.playSessions) {
            if (session.getLogoutTime() == -1) {
                if (time > session.getLoginTime()) {
                    return session;
                }
            } else {
                if (session.getLoginTime() <= time && session.getLogoutTime() >= time) {
                    return session;
                }
            }
        }
        
        return null;
    }
    
    public PlaySession getLatestSession() {
        if (isOnline()) { return getCurrentSession(); }
        PlaySession latestSession = null;
        for (PlaySession session : this.playSessions) {
            if (latestSession == null) {
                latestSession = session;
            } else {
                if (latestSession.getLogoutTime() < session.getLogoutTime()) {
                    latestSession = session;
                }
            }
        }
        return latestSession;
    }
    
    public boolean isOnline() {
        return Bukkit.getPlayer(uuid) != null;
    }
    
    public PlaySession getCurrentSession() {
        for (PlaySession session : this.playSessions) {
            if (session.getLogoutTime() == -1) {
                return session;
            }
        }
        return null;
    }
}