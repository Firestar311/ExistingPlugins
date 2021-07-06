package com.starmediadev.lib.user;

import com.starmediadev.lib.collection.IncrementalMap;
import com.starmediadev.lib.util.Utils;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.Map.Entry;

@SerializableAs("User")
public class User implements ConfigurationSerializable {
    
    protected IncrementalMap<DeathSnapshot> deaths = new IncrementalMap<>();
    protected Set<String> ipAddresses = new HashSet<>();
    protected String lastName = "";
    protected IncrementalMap<PlaySession> sessions = new IncrementalMap<>();
    protected UUID uuid;
    
    public User(UUID uuid) {
        this.uuid = uuid;
    }
    
    public static User deserialize(Map<String, Object> serialized) {
        UUID uuid = UUID.fromString((String) serialized.get("uuid"));
        String lastName = (String) serialized.get("lastname");
        Set<String> ipAddresses = (Set<String>) serialized.get("ipAddresses");
        List<PlaySession> oldSessions = new ArrayList<>();
        IncrementalMap<PlaySession> sessions = new IncrementalMap();
        IncrementalMap<DeathSnapshot> deaths = new IncrementalMap<>();
        for (Entry<String, Object> entry : serialized.entrySet()) {
            if (entry.getKey().contains("session")) {
                PlaySession session = (PlaySession) entry.getValue();
                if (session.getId() == -1) {
                    oldSessions.add(session);
                } else {
                    sessions.put(session.getId(), session);
                }
            } else if (entry.getKey().contains("death")) {
                DeathSnapshot death = (DeathSnapshot) entry.getValue();
                deaths.put(death.getId(), death);
            }
        }
        
        if (!oldSessions.isEmpty()) {
            oldSessions.forEach(session -> {
                int index = sessions.add(session);
                session.setId(index);
            });
        }
        
        User user = new User(uuid);
        user.setLastName(lastName);
        user.ipAddresses = ipAddresses;
        user.sessions = sessions;
        user.deaths = deaths;
        return user;
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
    
    public void sendMessage(BaseComponent... components) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            player.spigot().sendMessage(components);
        }
    }
    
    public void addPlaySession(PlaySession session) {
        int id = sessions.add(session);
        session.setId(id);
    }
    
    public void addDeath(DeathSnapshot death) {
        int id = deaths.add(death);
        death.setId(id);
    }
    
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("uuid", this.uuid.toString());
        serialized.put("lastname", this.lastName);
        serialized.put("ipAddresses", this.ipAddresses);
        for (PlaySession session : getPlaySessions()) {
            if (session != getCurrentSession()) {
                serialized.put("session" + session.getLoginTime() + "-" + session.getLogoutTime(), session);
            }
        }
        
        for (DeathSnapshot death : this.deaths.values()) {
            serialized.put("death" + death.getId(), death);
        }
        return serialized;
    }
    
    public void sendMessage(String... messages) {
        for (String msg : messages) {
            sendMessage(msg);
        }
    }
    
    public void sendMessage(String message) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            player.sendMessage(Utils.color(message));
        }
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public Collection<DeathSnapshot> getDeaths() {
        return new HashSet<>(this.deaths.values());
    }
    
    public DeathSnapshot getDeath(int id) {
        return this.deaths.get(id);
    }
    
    public DeathSnapshot getDeath(long time) {
        for (DeathSnapshot deathSnapshot : this.deaths.values()) {
            if (deathSnapshot.getTime() == time) {
                return deathSnapshot;
            }
        }
        
        return null;
    }
    
    public Set<DeathSnapshot> getDeaths(long start, long end) {
        Set<DeathSnapshot> deaths = new HashSet<>();
        System.out.println(start);
        System.out.println(end);
        for (DeathSnapshot snapshot : this.deaths.values()) {
            System.out.println(snapshot.getTime());
            if (start <= snapshot.getTime() && end >= snapshot.getTime()) {
                deaths.add(snapshot);
            }
        }
        
        return deaths;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public Location getLocation() {
        Player player = Bukkit.getPlayer(this.getUniqueId());
        if (player != null) {
            return player.getLocation();
        } else {
            PlaySession lastSession = getLatestSession();
            if (lastSession != null) {
                return lastSession.getLogoutLocation();
            } else {
                return null;
            }
        }
    }
    
    public UUID getUniqueId() {
        return uuid;
    }
    
    public PlaySession getLatestSession() {
        if (isOnline()) { return getCurrentSession(); }
        PlaySession latestSession = null;
        for (PlaySession session : this.sessions.values()) {
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
        for (PlaySession session : getPlaySessions()) {
            if (session.getLogoutTime() == -1) {
                return session;
            }
        }
        return null;
    }
    
    public Collection<PlaySession> getPlaySessions() {
        return new HashSet<>(this.sessions.values());
    }
    
    public List<String> getIpAddresses() {
        return new ArrayList<>(this.ipAddresses);
    }
    
    public PlaySession getPlaySession(long time) {
        for (PlaySession session : this.sessions.values()) {
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
    
    public PlaySession getPlaySession(int id) {
        return this.sessions.get(id);
    }
    
    public long getPlayTime() {
        long totalTime = 0;
        for (PlaySession session : sessions.values()) {
            long difference;
            if (session.getLogoutTime() == -1) {
                difference = System.currentTimeMillis() - session.getLoginTime();
            } else {
                difference = session.getLogoutTime() - session.getLoginTime();
            }
            totalTime += difference;
        }
        return totalTime;
    }
    
    public void clearSessions() {
        this.sessions.clear();
    }
}