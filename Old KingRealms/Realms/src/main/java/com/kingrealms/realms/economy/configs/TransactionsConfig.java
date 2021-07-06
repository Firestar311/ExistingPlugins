package com.kingrealms.realms.economy.configs;

import com.starmediadev.lib.config.ConfigManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class TransactionsConfig extends ConfigManager {
    public TransactionsConfig(JavaPlugin plugin, File folder) {
        super(plugin);
        this.file = new File(folder + File.separator + "transactions.yml");
    }
}