package com.kingrealms.realms.whitelist;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.profile.RealmProfile;
import com.kingrealms.realms.storage.StorageManager;
import com.starmediadev.lib.collection.IncrementalMap;
import com.starmediadev.lib.config.ConfigManager;
import com.starmediadev.lib.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;

public class WhitelistManager implements Listener {
    
    private IncrementalMap<Whitelist> whitelists = new IncrementalMap<>();
    private int activeWhitelist = -1;
    private ConfigManager configManager = StorageManager.whitelistConfig;
    
    public WhitelistManager() {
        configManager.setup();
        Realms.getInstance().getServer().getPluginManager().registerEvents(this, Realms.getInstance());
    }
    
    @EventHandler
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent e) {
        Whitelist whitelist = getActiveWhitelist();
        if (whitelist == null) return;
        RealmProfile profile = Realms.getInstance().getProfileManager().getProfile(e.getUniqueId());
        if (!whitelist.isAllowed(profile)) {
            e.setLoginResult(Result.KICK_WHITELIST);
            e.setKickMessage(Utils.color("&fYou are not whitelisted on this server."));
        }
    }
    
    public Whitelist getActiveWhitelist() {
        if (activeWhitelist > -1) {
            return whitelists.get(activeWhitelist);
        }
        return null;
    }
    
    public void saveData() {
        configManager.getConfig().set("whitelists", null);
        configManager.saveConfig();
        if (!whitelists.isEmpty()) {
            for (Entry<Integer, Whitelist> entry : whitelists.entrySet()) {
                configManager.getConfig().set("whitelists." + entry.getKey(), entry.getValue());
            }
        }
        if (activeWhitelist > -1) {
            configManager.getConfig().set("activeWhitelist", activeWhitelist);
        }
        configManager.saveConfig();
    }
    
    public void loadData() {
        FileConfiguration config = configManager.getConfig();
        if (config.contains("whitelists")) {
            ConfigurationSection whitelistsSection = config.getConfigurationSection("whitelists");
            for (String i : whitelistsSection.getKeys(false)) {
                Integer id = Integer.parseInt(i);
                Whitelist whitelist = (Whitelist) whitelistsSection.get(i);
                this.whitelists.put(id, whitelist);
            }
        }
        
        if (config.contains("activeWhitelist")) {
            this.activeWhitelist = config.getInt("activeWhitelist");
        }
    }
    
    public Whitelist getWhitelist(String name) {
        try {
            int id = Integer.parseInt(name);
            return whitelists.get(id);
        } catch (NumberFormatException e) {
            name = name.toLowerCase().replace(" ", "_");
            for (Whitelist whitelist : this.whitelists.values()) {
                if (whitelist.getName().toLowerCase().replace(" ", "_").equals(name)) {
                    return whitelist;
                }
            } 
        }
        
        return null;
    }
    
    public void addWhitelist(Whitelist whitelist) {
        int index = whitelists.add(whitelist);
        whitelist.setId(index);
    }
    
    public void setActiveWhitelist(int id) {
        this.activeWhitelist = id;
        
        Whitelist whitelist = getActiveWhitelist();
        if (whitelist != null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!whitelist.isAllowed(player.getUniqueId())) {
                    player.kickPlayer(Utils.color("&cYou are not whitelisted on the server."));
                }
            }
        }
    }
    
    public void removeWhitelist(Whitelist whitelist) {
        this.whitelists.remove(whitelist.getId());
    }
    
    public Collection<Whitelist> getWhitelists() {
        return new ArrayList<>(this.whitelists.values());
    }
}