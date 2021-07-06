package com.kingrealms.realms.profile;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.storage.StorageManager;
import com.starmediadev.lib.config.ConfigManager;
import com.starmediadev.lib.user.User;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

public class ProfileManager {
    
    private final Realms plugin = Realms.getInstance();
    private final Set<RealmProfile> profiles = new HashSet<>();
    private final ConfigManager configManager = StorageManager.profilesConfig;
    
    private static ServerProfile SERVER_PROFILE = new ServerProfile();
    
    public ProfileManager() {
        configManager.setup();
    }
    
    public void saveData() {
        FileConfiguration config = configManager.getConfig();
        config.set("profiles", null);
        for (RealmProfile profile : this.profiles) {
            config.set("profiles." + profile.getUser().getUniqueId().toString(), profile);
        }
        
        config.set("serverProfile", SERVER_PROFILE);
        
        this.configManager.saveConfig();
    }
    
    public void loadData() {
        FileConfiguration config = configManager.getConfig();
        ConfigurationSection profilesSection = config.getConfigurationSection("profiles");
        if (profilesSection != null) {
            for (String p : profilesSection.getKeys(false)) {
                RealmProfile profile = (RealmProfile) profilesSection.get(p);
                profiles.add(profile);
            }
        }
        
        SERVER_PROFILE = (ServerProfile) config.get("serverProfile");
    }
    
    public RealmProfile getProfile(UUID uuid) {
        if (!profiles.isEmpty()) {
            for (RealmProfile profile : this.profiles) {
                if (profile.getUniqueId().equals(uuid)) {
                    return profile;
                }
            }
        }
        
        User user = plugin.getUserManager().getUser(uuid);
        RealmProfile profile = new RealmProfile(user);
        this.profiles.add(profile);
        return profile;
    }
    
    public RealmProfile getProfile(String name) {
        UUID uuid = null;
        try {
            uuid = UUID.fromString(name);
            return getProfile(uuid);
        } catch (Exception e) {}
        
        for (RealmProfile profile : this.profiles) {
            if (profile.getUser().getLastName().equalsIgnoreCase(name)) {
                return profile;
            } else {
                if (uuid != null) {
                    if (profile.getUniqueId().equals(uuid)) {
                        return profile;
                    }
                }
            }
        }
        
        if (name.equalsIgnoreCase("console") || name.equalsIgnoreCase("server")) {
            return getServerProfile();
        }
        
        User user = plugin.getUserManager().getUser(name);
        RealmProfile profile = new RealmProfile(user);
        this.profiles.add(profile);
        return profile;
    }
    
    public void addProfile(RealmProfile profile) {
        this.profiles.add(profile);
    }
    
    public List<RealmProfile> getProfiles() {
        return new ArrayList<>(this.profiles);
    }
    
    public RealmProfile getProfile(CommandSender sender) {
        if (sender instanceof ConsoleCommandSender) {
            return getServerProfile();
        }
        return getProfile(((Player) sender).getUniqueId());
    }
    
    public static ServerProfile getServerProfile() {
        if (SERVER_PROFILE == null) {
            SERVER_PROFILE = new ServerProfile();
        }
        
        return SERVER_PROFILE;
    }
}