package com.stardevmc.enforcer.manager;

import com.stardevmc.enforcer.Enforcer;

import java.io.File;

public class SettingsManager {
    
    private final int configVersion = 1;
    
    private boolean usingDisplayNames;
    
    private String prefix;
    private String serverName;
    
    public SettingsManager(Enforcer plugin) {
        if (plugin.getConfig().getInt("config-version") != this.configVersion) {
            File pluginConfig = new File(plugin.getDataFolder() + File.separator, "config.yml");
            pluginConfig.delete();
            plugin.saveDefaultConfig();
        }
        
        if (!plugin.getConfig().contains("usingdisplaynames")) {
            plugin.getConfig().set("usingdisplaynames", false);
        }
        this.usingDisplayNames = plugin.getConfig().getBoolean("usingdisplaynames");
        if (!plugin.getConfig().contains("prefix")) {
            plugin.getConfig().set("prefix", "Enforcer");
        }
        this.prefix = plugin.getConfig().getString("prefix");
        if (!plugin.getConfig().contains("server")) {
            plugin.getConfig().set("server", "Server");
        }
        this.serverName = plugin.getConfig().getString("server");
        plugin.saveConfig();
    }
    
    public int getConfigVersion() {
        return configVersion;
    }
    
    public boolean isUsingDisplayNames() {
        return usingDisplayNames;
    }
    
    public String getPrefix() {
        return prefix;
    }
    
    public String getServerName() {
        return serverName;
    }
    
    public void setUsingDisplayNames(boolean usingDisplayNames) {
        this.usingDisplayNames = usingDisplayNames;
    }
    
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
    
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
}