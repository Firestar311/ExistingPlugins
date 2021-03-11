package net.firecraftmc.hungergames;

import lombok.Getter;
import lombok.Setter;
import net.firecraftmc.hungergames.chat.HGChatFormatter;
import net.firecraftmc.hungergames.chat.HGChatHandler;
import net.firecraftmc.hungergames.game.Game;
import net.firecraftmc.hungergames.game.GameManager;
import net.firecraftmc.hungergames.game.cmd.HGCommand;
import net.firecraftmc.hungergames.game.cmd.ProbablityCmd;
import net.firecraftmc.hungergames.game.gui.SpectatorInventoryGui;
import net.firecraftmc.hungergames.game.timer.Timer;
import net.firecraftmc.hungergames.listeners.BlockListeners;
import net.firecraftmc.hungergames.listeners.EntityListeners;
import net.firecraftmc.hungergames.listeners.InventoryListeners;
import net.firecraftmc.hungergames.listeners.PlayerListeners;
import net.firecraftmc.hungergames.lobby.Lobby;
import net.firecraftmc.hungergames.loot.Loot;
import net.firecraftmc.hungergames.loot.LootManager;
import net.firecraftmc.hungergames.map.HGMap;
import net.firecraftmc.hungergames.map.MapManager;
import net.firecraftmc.hungergames.records.GameRecord;
import net.firecraftmc.hungergames.records.GameSettingRecord;
import net.firecraftmc.hungergames.records.GameSettingsRecord;
import net.firecraftmc.hungergames.records.LootRecord;
import net.firecraftmc.hungergames.settings.GameSettings;
import net.firecraftmc.hungergames.settings.SettingsManager;
import net.firecraftmc.maniacore.ManiaCorePlugin;
import net.firecraftmc.maniacore.api.ManiaCore;
import net.firecraftmc.maniacore.api.channel.Channel;
import net.firecraftmc.maniacore.api.chat.ChatManager;
import net.firecraftmc.maniacore.api.server.ServerType;
import net.firecraftmc.maniacore.memory.MemoryHook;
import net.firecraftmc.maniacore.plugin.ManiaPlugin;
import net.firecraftmc.maniacore.plugin.ManiaTask;
import net.firecraftmc.maniacore.spigot.gui.Gui;
import net.firecraftmc.maniacore.spigot.perks.Perks;
import net.firecraftmc.maniacore.spigot.plugin.SpigotManiaTask;
import net.firecraftmc.maniacore.spigot.user.PlayerBoard;
import net.firecraftmc.maniacore.spigot.user.SpigotUser;
import net.firecraftmc.maniacore.spigot.util.Spawnpoint;
import net.firecraftmc.manialib.ManiaLib;
import net.firecraftmc.manialib.data.DatabaseManager;
import net.firecraftmc.manialib.data.MysqlDatabase;
import net.firecraftmc.manialib.util.Priority;
import net.firecraftmc.manialib.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;

@Getter
public final class HungerGames extends JavaPlugin implements ManiaPlugin {
    
    private GameManager gameManager;
    private ManiaCore maniaCore;
    @Setter
    private MapManager mapManager;
    private Lobby lobby;
    private LootManager lootManager;
    
    private static HungerGames instance;
    
    private MemoryHook gameTaskHook = new MemoryHook("Game Task");
    private SettingsManager settingsManager;

    public Spawnpoint getSpawnpoint() {
        return ((ManiaCorePlugin) Bukkit.getPluginManager().getPlugin("ManiaCore")).getSpawnpoint();
    }

    @Override
    public void onEnable() {
        instance = this;
        maniaCore = ManiaCore.getInstance();
        this.saveDefaultConfig();
        Gui.prepare(this);
        
        maniaCore.getDatabase().registerRecordType(GameSettingsRecord.class);
        maniaCore.getDatabase().registerRecordType(GameRecord.class);
        maniaCore.getDatabase().registerRecordType(LootRecord.class);
        maniaCore.getDatabase().registerRecordType(GameSettingRecord.class);
        maniaCore.getDatabase().generateTables();
        
        maniaCore.getServerManager().getCurrentServer().setType(ServerType.HUNGER_GAMES);
        
        this.settingsManager = new SettingsManager(this);
        this.settingsManager.load();
        
        this.mapManager = new MapManager(this);
        this.mapManager.loadMaps();
        this.getCommand("mapsadmin").setExecutor(mapManager);
        
        this.getCommand("probability").setExecutor(new ProbablityCmd());
    
        this.gameManager = new GameManager(this);

        this.lobby = new Lobby(this, getSpawnpoint());
        this.getCommand("map").setExecutor(lobby);
        this.getCommand("lobby").setExecutor(lobby);
        this.getCommand("votestart").setExecutor(lobby);
        this.getCommand("nextgame").setExecutor(lobby);
        this.getServer().getPluginManager().registerEvents(lobby, this);
        this.getServer().getPluginManager().registerEvents(new PlayerListeners(), this);
        this.getServer().getPluginManager().registerEvents(new EntityListeners(), this);
        this.getServer().getPluginManager().registerEvents(new BlockListeners(), this);
        this.getServer().getPluginManager().registerEvents(new InventoryListeners(), this);
    
        this.lobby.generateMapOptions();
        
        this.lootManager = new LootManager();
        this.lootManager.loadFromDatabase();
        this.lootManager.generateDefaultLoot();
        
        this.getCommand("hungergames").setExecutor(new HGCommand(this));
        this.getCommand("settings").setExecutor(settingsManager);
        
        Bukkit.getWorld("world").setDifficulty(Difficulty.PEACEFUL);
        
        ManiaCore.getInstance().getMemoryManager().addMemoryHook(gameTaskHook);
        ManiaCore.getInstance().getMemoryManager().addManiaPlugin(this);
        
        new BukkitRunnable() {
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    SpigotUser user = (SpigotUser) ManiaCore.getInstance().getUserManager().getUser(player.getUniqueId());
                    PlayerBoard scoreboard = user.getScoreboard();
                    if (scoreboard != null) {
                        scoreboard.update();
                    }
                }
            }
        }.runTaskTimer(this, 20L, 10L);

        getLogger().info("Loaded " + Perks.PERKS.size() + " Perks");

        Timer.startTimerUpdater(this);

        ChatManager chatManager = ManiaCore.getInstance().getChatManager();
        chatManager.setFormatter(Channel.GLOBAL, new HGChatFormatter());
        chatManager.registerHandler(this, new HGChatHandler(), Priority.HIGHEST);
        SpectatorInventoryGui.prepareTask();
    }

    public void registerRecordTypes() {
        DatabaseManager databaseManager = ManiaLib.getInstance().getDatabaseManager();
        MysqlDatabase database = ManiaLib.getInstance().getMysqlDatabase();
        databaseManager.registerRecordClasses(database, Game.class, GameSettings.class, Loot.class);
    }

    @Override
    public void onDisable() {
        File parentFile = new File(getDataFolder() + File.separator + ".." + File.separator + "..");
        for (HGMap map : mapManager.getMaps().values()) {
            if (map.getWorld() != null) {
                if (!map.getWorld().getPlayers().isEmpty()) {
                    for (Player player : map.getWorld().getPlayers()) {
                        player.teleport(getSpawnpoint().getLocation());
                    }
                }
                Bukkit.unloadWorld(map.getWorld(), false);
            }
            String worldName = map.getName().toLowerCase().replace(" ", "_");
            for (File file : parentFile.listFiles()) {
                if (file.isDirectory()) {
                    if (file.getName().equalsIgnoreCase(worldName)) {
                        Utils.purgeDirectory(file);
                    }
                }
            }
        }
        
        lobby.getLobbySigns().save();
        
        this.maniaCore.getDatabase().pushQueue();
        saveConfig();
    }
    
    public static HungerGames getInstance() {
        return instance;
    }
    
    public SettingsManager getSettingsManager() {
        return settingsManager;
    }
    
    @Override
    public String getVersion() {
        return getDescription().getVersion();
    }
    
    public ManiaTask runTask(Runnable runnable) {
        return new SpigotManiaTask(Bukkit.getScheduler().runTask(this, runnable));
    }
    
    public ManiaTask runTaskAsynchronously(Runnable runnable) {
        return new SpigotManiaTask(Bukkit.getScheduler().runTaskAsynchronously(this, runnable));
    }
    
    public ManiaTask runTaskLater(Runnable runnable, long delay) {
        return new SpigotManiaTask(Bukkit.getScheduler().runTaskLater(this, runnable, delay));
    }
    
    public ManiaTask runTaskLaterAsynchronously(Runnable runnable, long delay) {
        return new SpigotManiaTask(Bukkit.getScheduler().runTaskLaterAsynchronously(this, runnable, delay));
    }
    
    public ManiaTask runTaskTimer(Runnable runnable, long delay, long period) {
        return new SpigotManiaTask(Bukkit.getScheduler().runTaskTimer(this, runnable, delay, period));
    }
    
    public ManiaTask runTaskTimerAsynchronously(Runnable runnable, long delay, long period) {
        return new SpigotManiaTask(Bukkit.getScheduler().runTaskTimerAsynchronously(this, runnable, delay, period));
    }
}