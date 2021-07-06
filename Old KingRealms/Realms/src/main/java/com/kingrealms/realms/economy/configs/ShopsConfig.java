package com.kingrealms.realms.economy.configs;

import com.starmediadev.lib.config.ConfigManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class ShopsConfig extends ConfigManager {
    public ShopsConfig(JavaPlugin plugin, File folder) {
        super(plugin);
        this.file = new File(folder + File.separator + "shops.yml");
    }
}