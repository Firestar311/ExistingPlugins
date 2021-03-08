package net.firecraftmc.maniacore;

import net.firecraftmc.maniacore.api.ManiaCore;
import net.firecraftmc.maniacore.api.ranks.RankRedisListener;
import net.firecraftmc.maniacore.api.records.StatRecord;
import net.firecraftmc.maniacore.api.records.UserRecord;
import net.firecraftmc.maniacore.api.redis.Redis;
import net.firecraftmc.maniacore.bungee.cmd.DiscordCommand;
import net.firecraftmc.maniacore.bungee.cmd.GotoCmd;
import net.firecraftmc.maniacore.bungee.cmd.HubCommand;
import net.firecraftmc.maniacore.bungee.cmd.RulesCommand;
import net.firecraftmc.maniacore.bungee.communication.BungeeMessageHandler;
import net.firecraftmc.maniacore.bungee.listeners.BungeeListener;
import net.firecraftmc.maniacore.bungee.plugin.BungeeManiaTask;
import net.firecraftmc.maniacore.bungee.server.BungeeCordServerManager;
import net.firecraftmc.maniacore.bungee.user.BungeeUser;
import net.firecraftmc.maniacore.bungee.user.BungeeUserManager;
import net.firecraftmc.maniacore.bungee.user.UserRedisListener;
import net.firecraftmc.maniacore.plugin.ManiaPlugin;
import net.firecraftmc.maniacore.plugin.ManiaTask;
import net.firecraftmc.manialib.sql.Database;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import redis.clients.jedis.Jedis;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

public class ManiaCoreProxy extends Plugin implements ManiaPlugin {
    
    private net.firecraftmc.maniacore.api.ManiaCore maniaCore;
    Configuration config;
    
    public void onEnable() {
        net.firecraftmc.maniacore.api.ManiaCore.setInstance(this.maniaCore = new net.firecraftmc.maniacore.api.ManiaCore());
        maniaCore.setLogger(getLogger());
        this.saveDefaultConfig();
        maniaCore.init(getLogger(), this);
        maniaCore.setServerManager(new net.firecraftmc.maniacore.bungee.server.BungeeCordServerManager(maniaCore));
        maniaCore.getServerManager().init();
        getProxy().getPluginManager().registerListener(this, new BungeeListener(this));
        
        getProxy().getPluginManager().registerCommand(this, new HubCommand());
        getProxy().getPluginManager().registerCommand(this, new DiscordCommand());
        getProxy().getPluginManager().registerCommand(this, new GotoCmd());
        getProxy().getPluginManager().registerCommand(this, new RulesCommand());
        maniaCore.setMessageHandler(new BungeeMessageHandler());
        maniaCore.getMemoryManager().addManiaPlugin(this);
    }

    public void setupRedisListeners() {
        net.firecraftmc.maniacore.api.redis.Redis.registerListener(new RankRedisListener());
        net.firecraftmc.maniacore.api.redis.Redis.registerListener(new UserRedisListener());
        try (Jedis jedis = net.firecraftmc.maniacore.api.redis.Redis.getConnection()) {
            jedis.del("uuidtoidmap");
            jedis.del("uuidtonamemap");
        }
    }

    public void setupServerManager() {
        maniaCore.setServerManager(new BungeeCordServerManager(maniaCore));
        maniaCore.getServerManager().init();
    }

    public void setupUserManager() {
        maniaCore.setUserManager(new BungeeUserManager(this));
    }

    public void onDisable() {
        maniaCore.getDatabase().pushQueue();
        net.firecraftmc.maniacore.api.redis.Redis.getConnection().flushAll();
    }
    
    public Database getDatabase() {
        return maniaCore.getDatabase();
    }
    
    public void reloadConfig() {
        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
        } catch (IOException e) {
            getLogger().severe("Could not reload the config.yml");
        }
    }
    
    public Configuration getConfig() {
        if (config == null) {
            saveDefaultConfig();
        }
        
        return config;
    }
    
    public void saveConfig() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, new File(getDataFolder(), "config.yml"));
        } catch (Exception e) {
            getLogger().severe("Could not save config.yml");
        }
    }
    
    public void saveDefaultConfig() {
        if (!getDataFolder().exists()) { getDataFolder().mkdir(); }
        
        File file = new File(getDataFolder(), "config.yml");
        
        if (!file.exists()) {
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        reloadConfig();
    }
    
    public net.firecraftmc.maniacore.api.ManiaCore getManiaCore() {
        return maniaCore;
    }
    
    @Override
    public String getVersion() {
        return getDescription().getVersion();
    }
    
    @Override
    public String getName() {
        return getDescription().getName();
    }
    
    public net.firecraftmc.maniacore.plugin.ManiaTask runTask(Runnable runnable) {
        return new net.firecraftmc.maniacore.bungee.plugin.BungeeManiaTask(getProxy().getScheduler().schedule(this, runnable, 0, TimeUnit.MILLISECONDS));
    }
    
    public net.firecraftmc.maniacore.plugin.ManiaTask runTaskAsynchronously(Runnable runnable) {
        return new net.firecraftmc.maniacore.bungee.plugin.BungeeManiaTask(getProxy().getScheduler().runAsync(this, runnable));
    }
    
    public net.firecraftmc.maniacore.plugin.ManiaTask runTaskLater(Runnable runnable, long delay) {
        return new net.firecraftmc.maniacore.bungee.plugin.BungeeManiaTask(getProxy().getScheduler().schedule(this, runnable, delay * 50, TimeUnit.MILLISECONDS));
    }
    
    public net.firecraftmc.maniacore.plugin.ManiaTask runTaskLaterAsynchronously(Runnable runnable, long delay) {
        return new net.firecraftmc.maniacore.bungee.plugin.BungeeManiaTask(getProxy().getScheduler().schedule(this, () -> getProxy().getScheduler().runAsync(ManiaCoreProxy.this, runnable), delay * 50, TimeUnit.MILLISECONDS));
    }
    
    public net.firecraftmc.maniacore.plugin.ManiaTask runTaskTimer(Runnable runnable, long delay, long period) {
        return new net.firecraftmc.maniacore.bungee.plugin.BungeeManiaTask(getProxy().getScheduler().schedule(this, runnable, delay * 50, period * 50, TimeUnit.MILLISECONDS));
    }
    
    public ManiaTask runTaskTimerAsynchronously(Runnable runnable, long delay, long period) {
        return new BungeeManiaTask(getProxy().getScheduler().schedule(this, () -> getProxy().getScheduler().runAsync(ManiaCoreProxy.this, runnable), delay * 50, period * 50, TimeUnit.MILLISECONDS));
    }
    
    public static void saveUserData(BungeeUser bungeeUser) {
        bungeeUser.setStats(net.firecraftmc.maniacore.api.redis.Redis.getUserStats(bungeeUser.getUniqueId()));
        net.firecraftmc.maniacore.api.ManiaCore.getInstance().getDatabase().pushRecord(new UserRecord(bungeeUser));
        bungeeUser.getStats().forEach((type, stat) -> ManiaCore.getInstance().getDatabase().pushRecord(new StatRecord(stat)));
        if (!bungeeUser.isOnline()) {
            net.firecraftmc.maniacore.api.redis.Redis.deleteUserData(bungeeUser.getUniqueId());
            Redis.deleteUserStats(bungeeUser.getUniqueId());
        }
    }
}