package com.firestar311.lib.player;

import com.firestar311.lib.util.Utils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

public class PlayerManager implements Listener {
    
    private Map<UUID, User> users = new HashMap<>();
    
    private ServerUser serverUser = null;
    
    private File file;
    private FileConfiguration config;
    
    private JavaPlugin plugin;
    
    public PlayerManager(JavaPlugin plugin) {
        this.plugin = plugin;
        file = new File(plugin.getDataFolder(), "players.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create the players.yml file!");
                return;
            }
        }
        
        config = YamlConfiguration.loadConfiguration(file);
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if (!users.containsKey(player.getUniqueId())) {
            this.users.put(player.getUniqueId(), new User(player.getUniqueId()));
        }
    
        User user = this.users.get(player.getUniqueId());
        user.setLastName(player.getName());
        user.addIpAddress(player.getAddress().toString());
        user.addPlaySession(new PlaySession(user.getUniqueId(), System.currentTimeMillis(), player.getLocation()));
        
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        User info = this.users.get(e.getPlayer().getUniqueId());
        info.getCurrentSession().setLogoutInfo(System.currentTimeMillis(), e.getPlayer());
    }
    
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        User user = getUser(e.getPlayer().getUniqueId());
        user.getCurrentSession().addChatMessage(System.currentTimeMillis(), e.getMessage());
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onCmdPreprocess(PlayerCommandPreprocessEvent e) {
        User user = getUser(e.getPlayer().getUniqueId());
        user.getCurrentSession().addCommand(System.currentTimeMillis(), e.getMessage());
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        User user = this.users.get(e.getEntity().getUniqueId());
        user.getCurrentSession().addDeath(new DeathSnapshot(e.getEntity(), System.currentTimeMillis()));
    }
    
    public User getUser(UUID uuid) {
        if (uuid == null) return null;
        if (serverUser != null) {
            if (serverUser.getUniqueId() != null) {
                if (serverUser.getUniqueId().equals(uuid)) {
                    return serverUser;
                }
            }
        }
        
        if (uuid == null) return null;
        
        if (!this.users.containsKey(uuid)) {
            String name = Utils.getNameFromUUID(uuid);
            if (name == null) return null;
            User user = new User(uuid);
            user.setLastName(name);
            this.users.put(uuid, user);
        }
        return this.users.get(uuid);
    }
    
    public User getUser(String name) {
        if (StringUtils.isEmpty(name)) return null;
        
        try {
            if(getServerUser(name) != null) {
                return serverUser;
            }
        } catch (Exception e) {}
        
        for (User info : this.users.values()) {
            if (info.getLastName().equalsIgnoreCase(name)) {
                return info;
            }
        }
        
        UUID uuid = Utils.getUUIDFromName(name);
        if (uuid == null) {
            return null;
        }
        User info = new User(uuid);
        info.setLastName(name);
        this.users.put(uuid, info);
        return info;
    }
    
    public void getUserInfoAsync(UUID uuid, Consumer<User> success, Consumer<Exception> fail) {
        if (!this.users.containsKey(uuid)) {
            Utils.Async.getNameFromUUID(uuid, name -> {
                User user = new User(uuid);
                user.setLastName(name);
                this.users.put(uuid, user);
                success.accept(user);
            }, fail);
        }
    }
    
    public void getUserInfoAsync(String name, Consumer<User> success, Consumer<Exception> fail) {
        for (User info : this.users.values()) {
            if (info.getLastName().equalsIgnoreCase(name)) {
                success.accept(info);
                return;
            }
        }
        
        Utils.Async.getUUIDFromName(name, uuid -> {
            User info = new User(uuid);
            info.setLastName(name);
            this.users.put(uuid, info);
            success.accept(info);
        }, fail);
    }
    
    public void savePlayerData() {
        for (User user : this.users.values()) {
            this.config.set("users." + user.getUniqueId().toString(), user);
        }
        
        this.config.set("server.uuid", this.serverUser.getUniqueId());
        this.config.set("server.history", this.serverUser.getHistory());
    }
    
    public void loadPlayerData() {
        this.users.clear();
    
        ConfigurationSection userSection = this.config.getConfigurationSection("users");
        if (userSection != null) {
            for (String u : userSection.getKeys(false)) {
                User user = (User) config.get("users." + u);
                this.users.put(user.getUniqueId(), user);
            }
        }
        
        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            if (!users.containsKey(offlinePlayer.getUniqueId())) {
                User info = new User(offlinePlayer.getUniqueId());
                this.users.put(info.getUniqueId(), info);
            }
        }
        
        if (config.contains("server.uuid")) {
            UUID serverUUID = UUID.fromString(config.getString("server.uuid"));
            ServerUUIDHistory history = (ServerUUIDHistory) config.get("server.history");
            this.serverUser = new ServerUser(serverUUID, history);
        }
    }
    
    public ServerUser getServerUser() {
        return serverUser;
    }
    
    public User getServerUser(String infoString) {
        String[] infoSplit = infoString.split(":");
        long date = Long.parseLong(infoSplit[1]);
        if (serverUser.getHistory().getPreviousUUID(date) != null) {
            return serverUser;
        }
        return null;
    }
    
    public void setServerUser(ServerUser serverUser) {
        this.serverUser = serverUser;
    }
    
    public Map<UUID, User> getUsers() {
        return users;
    }
    
    //TODO get from command sender
}