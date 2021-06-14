package net.firecraftmc.maniacore;

import lombok.Getter;
import lombok.Setter;
import net.firecraftmc.maniacore.api.CenturionsCore;
import net.firecraftmc.maniacore.api.chat.ChatHandler;
import net.firecraftmc.maniacore.api.ranks.Rank;
import net.firecraftmc.maniacore.api.ranks.RankRedisListener;
import net.firecraftmc.maniacore.api.records.SkinRecord;
import net.firecraftmc.maniacore.api.redis.Redis;
import net.firecraftmc.maniacore.api.skin.Skin;
import net.firecraftmc.maniacore.api.user.User;
import net.firecraftmc.maniacore.memory.MemoryHook;
import net.firecraftmc.maniacore.plugin.CenturionsPlugin;
import net.firecraftmc.maniacore.plugin.CenturionsTask;
import net.firecraftmc.maniacore.spigot.anticheat.SpartanManager;
import net.firecraftmc.maniacore.spigot.cmd.BroadcastCmd;
import net.firecraftmc.maniacore.spigot.cmd.FriendsCmd;
import net.firecraftmc.maniacore.spigot.cmd.IgnoreCmd;
import net.firecraftmc.maniacore.spigot.cmd.IncognitoCmd;
import net.firecraftmc.maniacore.spigot.cmd.MemoryCmd;
import net.firecraftmc.maniacore.spigot.cmd.MsgCmd;
import net.firecraftmc.maniacore.spigot.cmd.MutationsCmd;
import net.firecraftmc.maniacore.spigot.cmd.NicknameCmd;
import net.firecraftmc.maniacore.spigot.cmd.PerkCmd;
import net.firecraftmc.maniacore.spigot.cmd.RankCmd;
import net.firecraftmc.maniacore.spigot.cmd.SayCmd;
import net.firecraftmc.maniacore.spigot.cmd.SetstatCommand;
import net.firecraftmc.maniacore.spigot.cmd.SpawnCmd;
import net.firecraftmc.maniacore.spigot.cmd.StatsCmd;
import net.firecraftmc.maniacore.spigot.cmd.TesterCmd;
import net.firecraftmc.maniacore.spigot.cmd.ToggleCmd;
import net.firecraftmc.maniacore.spigot.cmd.UserCmd;
import net.firecraftmc.maniacore.spigot.communication.SpigotMessageHandler;
import net.firecraftmc.maniacore.spigot.map.GameMap;
import net.firecraftmc.maniacore.spigot.map.Spawn;
import net.firecraftmc.maniacore.spigot.perks.PerkInfo;
import net.firecraftmc.maniacore.spigot.perks.PerkInfoRecord;
import net.firecraftmc.maniacore.spigot.perks.Perks;
import net.firecraftmc.maniacore.spigot.plugin.SpigotCenturionsTask;
import net.firecraftmc.maniacore.spigot.server.SpigotServerManager;
import net.firecraftmc.maniacore.spigot.updater.Updater;
import net.firecraftmc.maniacore.spigot.user.FriendsRedisListener;
import net.firecraftmc.maniacore.spigot.user.SpigotUserManager;
import net.firecraftmc.maniacore.spigot.util.Spawnpoint;
import net.firecraftmc.manialib.CenturionsLib;
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

public final class CenturionsCorePlugin extends JavaPlugin implements Listener, CenturionsPlugin {
    
    private CenturionsCore centurionsCore;
    
    static {
        ConfigurationSerialization.registerClass(Spawnpoint.class);
    }
    
    private static final char[] CODE_CHARS = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
    
    @Getter @Setter private Spawnpoint spawnpoint;
    
    @Override
    public void onEnable() {
        CenturionsCore.setInstance(this.centurionsCore = new CenturionsCore());
        centurionsCore.init(getLogger(), this);
        centurionsCore.setLogger(getLogger());
        this.saveDefaultConfig();
        runTask(() -> {
            //This makes sure that there is a user manager registered after the server has finished loading
            if (centurionsCore.getUserManager() == null) {
                SpigotUserManager userManager = new SpigotUserManager(this);
                centurionsCore.setUserManager(userManager);
            }
            getServer().getPluginManager().registerEvents((SpigotUserManager) centurionsCore.getUserManager(), this);
        });
        
        getServer().getPluginManager().registerEvents(new SpartanManager(), this);
        
        getCommand("incognito").setExecutor(new IncognitoCmd(this));
        getCommand("broadcast").setExecutor(new BroadcastCmd());
        getCommand("say").setExecutor(new SayCmd());
        getCommand("memory").setExecutor(new MemoryCmd());
        getCommand("stats").setExecutor(new StatsCmd());
        getCommand("toggle").setExecutor(new ToggleCmd());
        getCommand("ignore").setExecutor(new IgnoreCmd());
        MsgCmd msgCmd = new MsgCmd();
        getCommand("message").setExecutor(msgCmd);
        getCommand("reply").setExecutor(msgCmd);
        getCommand("rank").setExecutor(new RankCmd());
        getCommand("friends").setExecutor(new FriendsCmd());
        getCommand("setstat").setExecutor(new SetstatCommand());
        getCommand("perks").setExecutor(new PerkCmd());
        getCommand("mutations").setExecutor(new MutationsCmd());
        getCommand("tester").setExecutor(new TesterCmd());
        NicknameCmd nicknameCmd = new NicknameCmd();
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

        centurionsCore.setMessageHandler(new SpigotMessageHandler(this));
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        
        MemoryHook playerUpdate = new MemoryHook("Core Player Update");
        CenturionsCore.getInstance().getMemoryManager().addMemoryHook(playerUpdate);
        new BukkitRunnable() {
            public void run() {
                MemoryHook.Task task = playerUpdate.task().start();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getScoreboard() != null) {
                        Scoreboard scoreboard = player.getScoreboard();

                        for (Rank rank : Rank.values()) {
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
                            User user = centurionsCore.getUserManager().getUser(p.getUniqueId());
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
        centurionsCore.getMemoryManager().addManiaPlugin(this);
        
        this.runTaskLater(() -> centurionsCore.getServerManager().sendServerStart(getCenturionsCore().getServerManager().getCurrentServer().getName()), 1L);
        this.runTaskTimer(new Updater(this), 1L, 1L);
        Perks.PERKS.size();
        
        CenturionsCore.getInstance().getChatManager().registerHandler(this, new ChatHandler() {
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
    
    public CenturionsCore getCenturionsCore() {
        return centurionsCore;
    }

    public void registerRecordTypes() {
        CenturionsLib.getInstance().getDatabaseManager().registerRecordClasses(CenturionsLib.getInstance().getMysqlDatabase(), PerkInfo.class, GameMap.class, Spawn.class);
    }

    public void setupDatabaseRecords() {
        CenturionsCore.getInstance().getDatabase().registerRecordType(PerkInfoRecord.class);
    }

    public void setupRedisListeners() {
        Redis.registerListener(new RankRedisListener());
        Redis.registerListener(new FriendsRedisListener());
    }

    public void setupUserManager() {
        centurionsCore.setUserManager(new SpigotUserManager(this));
    }

    public void setupServerManager() {
        CenturionsCore.getInstance().setServerManager(new SpigotServerManager(centurionsCore));
        CenturionsCore.getInstance().getServerManager().init();
    }

    @Override
    public void onDisable() {
        //CenturionsCore.getInstance().getServerManager().sendServerStop(getCenturionsCore().getServerManager().getCurrentServer().getName());
        for (Skin skin : getCenturionsCore().getSkinManager().getSkins()) {
            centurionsCore.getDatabase().addRecordToQueue(new SkinRecord(skin));
        }
        
        getConfig().set("spawnpoint", spawnpoint);
        this.centurionsCore.getDatabase().pushQueue();
        saveConfig();
    }
    
    public Database getManiaDatabase() {
        return centurionsCore.getDatabase();
    }
    
    @Override
    public String getVersion() {
        return getDescription().getVersion();
    }
    
    public CenturionsTask runTask(Runnable runnable) {
        return new SpigotCenturionsTask(Bukkit.getScheduler().runTask(this, runnable));
    }
    
    public CenturionsTask runTaskAsynchronously(Runnable runnable) {
        return new SpigotCenturionsTask(Bukkit.getScheduler().runTaskAsynchronously(this, runnable));
    }
    
    public CenturionsTask runTaskLater(Runnable runnable, long delay) {
        return new SpigotCenturionsTask(Bukkit.getScheduler().runTaskLater(this, runnable, delay));
    }
    
    public CenturionsTask runTaskLaterAsynchronously(Runnable runnable, long delay) {
        return new SpigotCenturionsTask(Bukkit.getScheduler().runTaskLaterAsynchronously(this, runnable, delay));
    }
    
    public CenturionsTask runTaskTimer(Runnable runnable, long delay, long period) {
        return new SpigotCenturionsTask(Bukkit.getScheduler().runTaskTimer(this, runnable, delay, period));
    }
    
    public CenturionsTask runTaskTimerAsynchronously(Runnable runnable, long delay, long period) {
        return new SpigotCenturionsTask(Bukkit.getScheduler().runTaskTimerAsynchronously(this, runnable, delay, period));
    }
}