package com.kingrealms.realms.settings;

import com.kingrealms.realms.storage.StorageManager;
import com.starmediadev.lib.config.ConfigManager;
import com.starmediadev.lib.region.Cuboid;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.UUID;

public class SettingsManager {
    
    private UUID netherPortalKeeper;
    private ConfigManager configManager = StorageManager.settingsConfig;
    private Cuboid netherStartCuboid;
    
    public SettingsManager() {
        configManager.setup();
    }
    
    public void saveData() {
        if (this.netherStartCuboid != null) {
            this.configManager.getConfig().set("netherStartCuboid", netherStartCuboid);
        }
        if (this.netherPortalKeeper != null) {
            this.configManager.getConfig().set("netherPortalKeeper", netherPortalKeeper.toString());
        }
        configManager.saveConfig();
    }
    
    public void loadData() {
        FileConfiguration config = this.configManager.getConfig();
        if (config.contains("netherStartCuboid")) {
            this.netherStartCuboid = (Cuboid) config.get("netherStartCuboid");
        }
        if (config.contains("netherPortalKeeper")) {
            this.netherPortalKeeper = UUID.fromString(config.getString("netherPortalKeeper"));
        }
    }
    
    public Cuboid getNetherStartCuboid() {
        return netherStartCuboid;
    }
    
    public void setNetherStartCuboid(Cuboid netherStartCuboid) {
        this.netherStartCuboid = netherStartCuboid;
    }
    
    public UUID getNetherPortalKeeper() {
        return netherPortalKeeper;
    }
    
    public void setNetherPortalKeeper(UUID netherPortalKeeper) {
        this.netherPortalKeeper = netherPortalKeeper;
    }
}