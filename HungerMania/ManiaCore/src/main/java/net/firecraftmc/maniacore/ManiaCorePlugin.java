package net.firecraftmc.maniacore;

import lombok.Getter;
import lombok.Setter;
import net.firecraftmc.maniacore.api.ManiaCore;
import net.firecraftmc.maniacore.api.chat.ChatHandler;
import net.firecraftmc.maniacore.api.ranks.Rank;
import net.firecraftmc.maniacore.api.ranks.RankRedisListener;
import net.firecraftmc.maniacore.api.records.SkinRecord;
import net.firecraftmc.maniacore.api.redis.Redis;
import net.firecraftmc.maniacore.api.skin.Skin;
import net.firecraftmc.maniacore.api.user.User;
import net.firecraftmc.maniacore.plugin.ManiaPlugin;
import net.firecraftmc.maniacore.plugin.ManiaTask;
import net.firecraftmc.maniacore.spigot.anticheat.SpartanManager;
import net.firecraftmc.maniacore.spigot.cmd.NicknameCmd;
import net.firecraftmc.maniacore.spigot.cmd.SpawnCmd;
import net.firecraftmc.maniacore.spigot.cmd.UserCmd;
import net.firecraftmc.maniacore.spigot.communication.SpigotMessageHandler;
import net.firecraftmc.maniacore.spigot.map.GameMap;
import net.firecraftmc.maniacore.spigot.map.Spawn;
import net.firecraftmc.maniacore.spigot.perks.PerkInfo;
import net.firecraftmc.maniacore.spigot.perks.PerkInfoRecord;
import net.firecraftmc.maniacore.spigot.perks.Perks;
import net.firecraftmc.maniacore.spigot.plugin.SpigotManiaTask;
import net.firecraftmc.maniacore.spigot.server.SpigotServerManager;
import net.firecraftmc.maniacore.spigot.updater.Updater;
import net.firecraftmc.maniacore.spigot.user.FriendsRedisListener;
import net.firecraftmc.maniacore.spigot.user.SpigotUserManager;
import net.firecraftmc.maniacore.spigot.util.Spawnpoint;
import net.firecraftmc.manialib.ManiaLib;
import net.firecraftmc.manialib.sql.Database;
import net.firecraftmc.manialib.util.Priority;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class ManiaCorePlugin extends JavaPlugin implements Listener, ManiaPlugin {
    
    private net.firecraftmc.maniacore.api.ManiaCore maniaCore;
    
    static {
        ConfigurationSerialization.registerClass(Spawnpoint.class);
    }
    
    private static final char[] CODE_CHARS = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    
    @Getter @Setter private Spawnpoint spawnpoint;
    
    @Override
    public void onEnable() {
        net.firecraftmc.maniacore.api.ManiaCore.setInstance(this.maniaCore = new net.firecraftmc.maniacore.api.ManiaCore());
        maniaCore.init(getLogger(), this);
        maniaCore.setLogger(getLogger());
        this.saveDefaultConfig();
        runTask(() -> {
            //This makes sure that there is a user manager registered after the server has finished loading
            if (maniaCore.getUserManager() == null) {
                net.firecraftmc.maniacore.spigot.user.SpigotUserManager userManager = new net.firecraftmc.maniacore.spigot.user.SpigotUserManager(this);
                maniaCore.setUserManager(userManager);
            }
            getServer().getPluginManager().registerEvents((net.firecraftmc.maniacore.spigot.user.SpigotUserManager) maniaCore.getUserManager(), this);
        });
        
        getServer().getPluginManager().registerEvents(new SpartanManager(), this);
        
        getCommand("incognito").setExecutor(new net.firecraftmc.maniacore.spigot.cmd.IncognitoCmd(this));
        getCommand("broadcast").setExecutor(new net.firecraftmc.maniacore.spigot.cmd.BroadcastCmd());
        getCommand("say").setExecutor(new net.firecraftmc.maniacore.spigot.cmd.SayCmd());
        getCommand("memory").setExecutor(new net.firecraftmc.maniacore.spigot.cmd.MemoryCmd());
        getCommand("stats").setExecutor(new net.firecraftmc.maniacore.spigot.cmd.StatsCmd());
        getCommand("toggle").setExecutor(new net.firecraftmc.maniacore.spigot.cmd.ToggleCmd());
        getCommand("ignore").setExecutor(new net.firecraftmc.maniacore.spigot.cmd.IgnoreCmd());
        net.firecraftmc.maniacore.spigot.cmd.MsgCmd msgCmd = new net.firecraftmc.maniacore.spigot.cmd.MsgCmd();
        getCommand("message").setExecutor(msgCmd);
        getCommand("reply").setExecutor(msgCmd);
        getCommand("rank").setExecutor(new net.firecraftmc.maniacore.spigot.cmd.RankCmd());
        getCommand("friends").setExecutor(new net.firecraftmc.maniacore.spigot.cmd.FriendsCmd());
        getCommand("setstat").setExecutor(new net.firecraftmc.maniacore.spigot.cmd.SetstatCommand());
        getCommand("perks").setExecutor(new net.firecraftmc.maniacore.spigot.cmd.PerkCmd());
        getCommand("mutations").setExecutor(new net.firecraftmc.maniacore.spigot.cmd.MutationsCmd());
        getCommand("tester").setExecutor(new net.firecraftmc.maniacore.spigot.cmd.TesterCmd());
        net.firecraftmc.maniacore.spigot.cmd.NicknameCmd nicknameCmd = new NicknameCmd();
        getCommand("nick").setExecutor(nicknameCmd);
        getCommand("unnick").setExecutor(nicknameCmd);
        getCommand("realname").setExecutor(nicknameCmd);
        SpawnCmd spawnCmd = new SpawnCmd(this);
        getCommand("setspawn").setExecutor(spawnCmd);
        getCommand("spawn").setExecutor(spawnCmd);
        getCommand("user").setExecutor(new UserCmd(this));
        
        new BukkitRunnable() {
            public void run() {
                getManiaDatabase().pushQueue();
            }
        }.runTaskTimerAsynchronously(this, 6000, 6000);

        maniaCore.setMessageHandler(new SpigotMessageHandler(this));
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        
        net.firecraftmc.maniacore.memory.MemoryHook playerUpdate = new net.firecraftmc.maniacore.memory.MemoryHook("Core Player Update");
        net.firecraftmc.maniacore.api.ManiaCore.getInstance().getMemoryManager().addMemoryHook(playerUpdate);
        new BukkitRunnable() {
            public void run() {
                net.firecraftmc.maniacore.memory.MemoryHook.Task task = playerUpdate.task().start();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getScoreboard() != null) {
                        Scoreboard scoreboard = player.getScoreboard();

                        for (net.firecraftmc.maniacore.api.ranks.Rank rank : net.firecraftmc.maniacore.api.ranks.Rank.values()) {
                            boolean noTeam = true;
                            for (Team team : scoreboard.getTeams()) {
                                if (team.getName().equalsIgnoreCase(CODE_CHARS[rank.ordinal()] + "_" + rank.getName())) {
                                    noTeam = false;
                                }
                            }
                            if (noTeam) {
                                scoreboard.registerNewTeam(CODE_CHARS[rank.ordinal()] + "_" + rank.getName());
                            }
                        }

                        for (Player p : Bukkit.getOnlinePlayers()) {
                            User user = maniaCore.getUserManager().getUser(p.getUniqueId());
                            Rank rank = user.getRank();
                            String name = user.getName();
                            if (user.getNickname().isActive()) {
                                rank = user.getNickname().getRank();
                                name = user.getNickname().getName();
                            }
                            Team team = scoreboard.getTeam(CODE_CHARS[rank.ordinal()] + "_" + rank.getName());
                            boolean existsInTeam = false;
                            if (team != null) {
                                for (String entry : team.getEntries()) {
                                    if (entry.equalsIgnoreCase(name)) {
                                        existsInTeam = true;
                                        break;
                                    }
                                }
                            }

                            for (Team t : scoreboard.getTeams()) {
                                if (t.getEntries().contains(name)) {
                                    if (!t.getName().equalsIgnoreCase(CODE_CHARS[rank.ordinal()] + "_" + rank.getName())) {
                                        t.removeEntry(name);
                                    }
                                }
                                if (t.getEntries().contains(user.getName()) && user.getNickname().isActive()) {
                                    t.removeEntry(user.getName());
                                }
                            }

                            if (!existsInTeam) {
                                try {
                                    team.addEntry(name);
                                } catch (Exception e) {
                                }
                            }
                        }
                    }
                }
                task.end();
            }
        }.runTaskTimer(this, 20L, 20L);
        maniaCore.getMemoryManager().addManiaPlugin(this);
        
        this.runTaskLater(() -> maniaCore.getServerManager().sendServerStart(getManiaCore().getServerManager().getCurrentServer().getName()), 1L);
        this.runTaskTimer(new Updater(this), 1L, 1L);
        Perks.PERKS.size();
        
        net.firecraftmc.maniacore.api.ManiaCore.getInstance().getChatManager().registerHandler(this, new ChatHandler() {
            public Set<UUID> getAllTargets() {
                Set<UUID> targets = new HashSet<>();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    targets.add(player.getUniqueId());
                }
                return targets;
            }
        }, Priority.LOWEST);
        
        if (getConfig().contains("spawnpoint")) {
            this.spawnpoint = (Spawnpoint) getConfig().get("spawnpoint");
        } else {
            this.spawnpoint = new Spawnpoint(Bukkit.getWorld("world").getSpawnLocation());
        }
    }
    
    public net.firecraftmc.maniacore.api.ManiaCore getManiaCore() {
        return maniaCore;
    }

    public void registerRecordTypes() {
        ManiaLib.getInstance().getDatabaseManager().registerRecordClasses(ManiaLib.getInstance().getMysqlDatabase(), PerkInfo.class, GameMap.class, Spawn.class);
    }

    public void setupDatabaseRecords() {
        net.firecraftmc.maniacore.api.ManiaCore.getInstance().getDatabase().registerRecordType(PerkInfoRecord.class);
    }

    public void setupRedisListeners() {
        net.firecraftmc.maniacore.api.redis.Redis.registerListener(new RankRedisListener());
        Redis.registerListener(new FriendsRedisListener());
    }

    public void setupUserManager() {
        maniaCore.setUserManager(new SpigotUserManager(this));
    }

    public void setupServerManager() {
        net.firecraftmc.maniacore.api.ManiaCore.getInstance().setServerManager(new SpigotServerManager(maniaCore));
        ManiaCore.getInstance().getServerManager().init();
    }

    @Override
    public void onDisable() {
        //ManiaCore.getInstance().getServerManager().sendServerStop(getManiaCore().getServerManager().getCurrentServer().getName());
        for (Skin skin : getManiaCore().getSkinManager().getSkins()) {
            maniaCore.getDatabase().addRecordToQueue(new SkinRecord(skin));
        }
        
        getConfig().set("spawnpoint", spawnpoint);
        this.maniaCore.getDatabase().pushQueue();
        saveConfig();
    }
    
    public Database getManiaDatabase() {
        return maniaCore.getDatabase();
    }
    
    @Override
    public String getVersion() {
        return getDescription().getVersion();
    }
    
    public net.firecraftmc.maniacore.plugin.ManiaTask runTask(Runnable runnable) {
        return new net.firecraftmc.maniacore.spigot.plugin.SpigotManiaTask(Bukkit.getScheduler().runTask(this, runnable));
    }
    
    public net.firecraftmc.maniacore.plugin.ManiaTask runTaskAsynchronously(Runnable runnable) {
        return new net.firecraftmc.maniacore.spigot.plugin.SpigotManiaTask(Bukkit.getScheduler().runTaskAsynchronously(this, runnable));
    }
    
    public net.firecraftmc.maniacore.plugin.ManiaTask runTaskLater(Runnable runnable, long delay) {
        return new net.firecraftmc.maniacore.spigot.plugin.SpigotManiaTask(Bukkit.getScheduler().runTaskLater(this, runnable, delay));
    }
    
    public net.firecraftmc.maniacore.plugin.ManiaTask runTaskLaterAsynchronously(Runnable runnable, long delay) {
        return new net.firecraftmc.maniacore.spigot.plugin.SpigotManiaTask(Bukkit.getScheduler().runTaskLaterAsynchronously(this, runnable, delay));
    }
    
    public net.firecraftmc.maniacore.plugin.ManiaTask runTaskTimer(Runnable runnable, long delay, long period) {
        return new net.firecraftmc.maniacore.spigot.plugin.SpigotManiaTask(Bukkit.getScheduler().runTaskTimer(this, runnable, delay, period));
    }
    
    public ManiaTask runTaskTimerAsynchronously(Runnable runnable, long delay, long period) {
        return new SpigotManiaTask(Bukkit.getScheduler().runTaskTimerAsynchronously(this, runnable, delay, period));
    }
}