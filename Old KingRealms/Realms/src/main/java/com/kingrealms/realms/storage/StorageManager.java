package com.kingrealms.realms.storage;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.economy.configs.*;
import com.starmediadev.lib.config.ConfigManager;
import com.starmediadev.lib.sql.DBType;
import com.starmediadev.lib.sql.Database;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;

@SuppressWarnings("unused")
public class StorageManager {
    
    private static Realms plugin = Realms.getInstance();
    private Database database; //SQL Storage
    
    //Flatfile Storage
    public static final ConfigManager channelConfig = new ConfigManager(plugin, "channels");
    public static final ConfigManager craftingConfig = new ConfigManager(plugin, "crafting");
    public static final ConfigManager gravesConfig = new ConfigManager(plugin, "graves");
    public static final ConfigManager kitsConfig = new ConfigManager(plugin, "kits");
    public static final ConfigManager limitsConfig = new ConfigManager(plugin, "limits");
    public static final ConfigManager plotsConfig = new ConfigManager(plugin, "plots");
    public static final ConfigManager profilesConfig = new ConfigManager(plugin, "profiles");
    public static final ConfigManager warpsConfig = new ConfigManager(plugin, "warps");
    public static final ConfigManager territoriesConfig = new ConfigManager(plugin, "territories");
    public static final ConfigManager spawnersConfig = new ConfigManager(plugin, "spawners");
    public static final ConfigManager miningConfig = new ConfigManager(plugin, "mining");
    public static final ConfigManager farmingConfig = new ConfigManager(plugin, "farming");
    public static final ConfigManager settingsConfig = new ConfigManager(plugin, "settings");
    public static final ConfigManager trashConfig = new ConfigManager(plugin, "trash");
    public static final ConfigManager whitelistConfig = new ConfigManager(plugin, "whitelists");
    public static final ConfigManager supplyDropConfig = new ConfigManager(plugin, "supplydrops");
    public static final ConfigManager woodcuttingConfig = new ConfigManager(plugin, "woodcutting");
    
    private static final File economyFolder = new File(Realms.getInstance().getDataFolder() + File.separator + "economy");
    
    public static final ConfigManager accountsConfig = new AccountsConfig(Realms.getInstance(), economyFolder);
    public static final ConfigManager transactionsConfig = new TransactionsConfig(Realms.getInstance(), economyFolder);
    public static final ConfigManager shopsConfig = new ShopsConfig(Realms.getInstance(), economyFolder);
    public static final ConfigManager ticketsConfig = new TicketsConfig(Realms.getInstance(), economyFolder);
    
    public StorageManager() {
        FileConfiguration config = plugin.getConfig();
        database = new Database(plugin, DBType.MYSQL, config.getString("storage.mysql.hostname"), config.getString("storage.mysql.username"), config.getString("storage.mysql.password"), config.getInt("storage.mysql.port"), config.getString("storage.mysql.database"));
        database.loadRecords();
    }
    
    public Database getDatabase() {
        return database;
    }
}