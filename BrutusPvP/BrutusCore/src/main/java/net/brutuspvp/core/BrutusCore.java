package net.brutuspvp.core;

import com.firestar311.fireutils.classes.Utils;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import net.brutuspvp.core.managers.*;
import net.ess3.api.IEssentials;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class BrutusCore extends JavaPlugin implements Listener {

    private Player firestar311 = null;

    private static BrutusCore instance;

    private WorldEditPlugin worldEdit;
    private WorldGuardPlugin worldGuard;
    private IEssentials essentials;

    private Permission perm;
    private Economy vaultEcon;

    private File errorFolder = new File(getDataFolder() + File.separator + "errors");
    private File userFolder = new File(getDataFolder() + File.separator + "userdata");

    private BrutusScoreboardManager scoreboardManager;
    private CourtManager courtManager;
    private FriendsManager friendsManager;
    private LivingSpaceManager livingSpaceManager;
    private PlayerManager playerManager;
    private PunishmentManager punishmentManager;
    private ReportsManager reportsManager;
    private SettingsManager settingsManager;
    private VanishManager vanishManager;
    private EconomyManager economyManager;
    private JobManager jobManager;
    private GUIManager guiManager;
    private VotifierManager votifierManager;
    private MOTDManager motdManager;
    private ChairManager chairManager;
    private PMManager pmManager;
    private ChatManager chatManager;

    private static ArrayList<Sign> signChanges = new ArrayList<>();

    private ArrayList<UUID> devVanish = new ArrayList<>();

    private final static ArrayList<UUID> judges = new ArrayList<>(
            Arrays.asList(UUID.fromString("b6add577-30e2-4718-95df-59b4ce239769"),
                    UUID.fromString("72368745-a9fc-4a44-bf7b-25dbfa5ced6f"),
                    UUID.fromString("3f7891ce-5a73-4d52-a2ba-299839053fdc"),
                    UUID.fromString("a92a710d-246f-4bdb-b5f5-b645032b9f5f")));
    public static final String prefix = "§6[BrutusCore] ";

    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();
        settingsManager = new SettingsManager(this);
        try {
            if (!errorFolder.exists()) {
                errorFolder.mkdirs();
            }

            if (!userFolder.exists()) {
                userFolder.mkdirs();
            }

            if (!setupPermissions() && !setupEconomy()) {
                getLogger().log(Level.SEVERE, "Vault hook failed. Plugin is Disabling");
                getServer().getPluginManager().disablePlugin(this);
            }

            worldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
            worldGuard = (WorldGuardPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");
            essentials = (IEssentials) Bukkit.getServer().getPluginManager().getPlugin("Essentials");

            courtManager = new CourtManager(this);
            livingSpaceManager = new LivingSpaceManager(this);
            punishmentManager = new PunishmentManager(this);
            reportsManager = new ReportsManager(this);
            vanishManager = new VanishManager(this);
            jobManager = new JobManager(this);
            guiManager = new GUIManager(this);
            votifierManager = new VotifierManager(this);
            motdManager = new MOTDManager(this);
            chairManager = new ChairManager(this);
            pmManager = new PMManager(this);
            chatManager = new ChatManager(this);
            playerManager = new PlayerManager(this);
            friendsManager = new FriendsManager(this);
            economyManager = new EconomyManager(this);
            scoreboardManager = new BrutusScoreboardManager(this);

            this.getCommand("playtime").setExecutor(new PlayTimeManager(this));
            this.getCommand("chat").setExecutor(chatManager);
            this.registerExecutor(pmManager, "pm", "reply");
            this.getCommand("vanish").setExecutor(vanishManager);
            this.getCommand("toggledeathchest").setExecutor(new DeathChestManager(this));
            this.getCommand("livingspaces").setExecutor(livingSpaceManager);
            this.registerExecutor(punishmentManager, "ban", "tempban", "unban", "mute", "tempmute", "unmute", "kick",
                    "jail", "unjail");
            this.getCommand("courts").setExecutor(courtManager);
            this.registerExecutor(reportsManager, "report", "ticket");
            this.getCommand("friends").setExecutor(friendsManager);
            this.registerExecutor(economyManager, "money", "shops", "balance", "balancetop", "account",
                    "sharedaccount");
            this.getCommand("jobs").setExecutor(jobManager);
            this.getCommand("gui").setExecutor(guiManager);
            this.getCommand("vote").setExecutor(votifierManager);

            new BukkitRunnable() {
                public void run() {
                    Bukkit.broadcastMessage(prefix + "§7Saving Plugin Data");
                    try {
                        livingspaces().saveLivingSpaces();
                        punishments().savePunishments();
                        courts().saveCourts();
                        reports().saveReports();
                        players().savePlayerData();
                        friends().save();
                        votifier().saveData();
                        Bukkit.broadcastMessage(prefix + "§7Plugin Data Saved Successfully");
                    } catch (Exception e) {
                        BrutusCore.createBrutusError(e, "Save Plugin Data");
                    }
                }
            }.runTaskTimerAsynchronously(this, settings().getSaveInterval(), settings().getSaveInterval());

            for (Player p : Bukkit.getOnlinePlayers()) {
                scoreboardManager.updateScoreboard(p);
                for (Sign sign : signChanges) {
                    p.sendSignChange(sign.getLocation(), sign.getLines());
                }
            }

            if (Bukkit.getPlayer(UUID.fromString("3f7891ce-5a73-4d52-a2ba-299839053fdc")) != null) {
                firestar311 = Bukkit.getPlayer(UUID.fromString("3f7891ce-5a73-4d52-a2ba-299839053fdc"));
            }

            this.getServer().getPluginManager().registerEvents(this, this);

            // BEVault brutusEcon = new BEVault(this);
            // this.getServer().getServicesManager().register(BEVault.class,
            // brutusEcon, this, ServicePriority.Highest);

        } catch (Exception e) {
            BrutusCore.createBrutusError(e, "BrutusCore Constructor");
        }
    }

    public void onDisable() {
        try {
            livingspaces().saveLivingSpaces();
            punishments().savePunishments();
            courts().saveCourts();
            reports().saveReports();
            players().savePlayerData();
            friends().save();
            votifier().saveData();
        } catch (Exception e) {
            BrutusCore.createBrutusError(e, "Save Plugin Data");
        }
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("saveplugindata")) {
            if (sender.hasPermission(Perms.SAVE_PLUGIN_DATA)) {
                sender.sendMessage(prefix + "§7Saving Plugin Data");
                try {
                    livingspaces().saveLivingSpaces();
                    punishments().savePunishments();
                    courts().saveCourts();
                    reports().saveReports();
                    players().savePlayerData();
                    friends().save();
                    votifier().saveData();
                    sender.sendMessage(prefix + "§7Plugin Data Saved Successfully");
                } catch (Exception e) {
                    BrutusCore.createBrutusError(e, "Save Plugin Data");
                }
            } else {
                sender.sendMessage(settings().getNoPermissionMessage());
            }
        } else if (cmd.getName().equalsIgnoreCase("clearerrors")) {
            if (sender instanceof ConsoleCommandSender) {
                sender.sendMessage("§cOnly Firestar311 may clear errors.");
                return true;
            } else if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.getUniqueId().equals(UUID.fromString("3f7891ce-5a73-4d52-a2ba-299839053fdc"))) {
                    sender.sendMessage("§aStarted the deletion of error files.");
                    new BukkitRunnable() {
                        public void run() {
                            File[] errors = errorFolder.listFiles();
                            if (errors != null) {
                                for (File file : errors) {
                                    file.delete();
                                }
                            }
                            sender.sendMessage("§aSuccessfully cleared all error files.");
                        }
                    }.runTaskAsynchronously(this);
                } else {
                    player.sendMessage("§cOnly Firestar311 may clear errors.");
                    return true;
                }
            }
        } else if (cmd.getName().equalsIgnoreCase("getuuid")) {
            if (args.length > 0) {
                sender.sendMessage("§b" + args[0] + "§a's UUID is §b" + players().getUUID(args[0]).toString());
                return true;
            }
        } else if (cmd.getName().equalsIgnoreCase("devvanish")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("§cOnly players may use this command.");
                return true;
            } else {
                Player player = (Player) sender;
                if (player.getUniqueId().equals(UUID.fromString("3f7891ce-5a73-4d52-a2ba-299839053fdc"))) {
                    if (args.length == 0) {
                        if (devVanish.contains(player.getUniqueId())) {
                            devVanish.remove(player.getUniqueId());
                            player.sendMessage("§aSuccessfully §cdisabled §aDev Vanish for you.");

                            for (Player p : Bukkit.getOnlinePlayers()) {
                                p.showPlayer(player);
                            }

                            return true;
                        } else {
                            devVanish.add(player.getUniqueId());
                            player.sendMessage("§aSuccessfully §2enabled §aDev Vanish for you.");

                            for (Player p : Bukkit.getOnlinePlayers()) {
                                if (!p.getUniqueId().equals(UUID.fromString("3f7891ce-5a73-4d52-a2ba-299839053fdc"))) {
                                    if (!devVanish.contains(p.getUniqueId())) {
                                        p.hidePlayer(player);
                                    }
                                }
                            }

                            return true;
                        }
                    } else if (args.length == 1) {
                        Player target = Bukkit.getPlayer(args[0]);
                        if (target == null) {
                            player.sendMessage("§cThe name of the player is invalid. They must be online.");
                            return true;
                        }
                        if (devVanish.contains(target.getUniqueId())) {
                            devVanish.remove(target.getUniqueId());
                            for (Player p : Bukkit.getOnlinePlayers()) {
                                p.showPlayer(player);
                            }

                            for (UUID uuid : this.devVanish) {
                                Player p = Bukkit.getPlayer(uuid);
                                if (p == null) continue;

                                target.hidePlayer(p);
                            }

                            player.sendMessage("§aSuccessfully §cdisabled §aDev Vanish for §b" + target.getName());
                            target.sendMessage("§aYou were §cremoved §afrom Dev Vanish by §b" + player.getName());
                            return true;
                        } else {
                            devVanish.add(target.getUniqueId());

                            for (Player p : Bukkit.getOnlinePlayers()) {
                                if (!p.getUniqueId().equals(UUID.fromString("3f7891ce-5a73-4d52-a2ba-299839053fdc"))) {
                                    if (!devVanish.contains(p.getUniqueId())) {
                                        p.hidePlayer(target);
                                    }
                                }
                            }

                            for (UUID uuid : this.devVanish) {
                                Player p = Bukkit.getPlayer(uuid);
                                if (p == null) continue;

                                if (!target.canSee(p)) {
                                    target.showPlayer(p);
                                }
                            }
                            player.sendMessage("§aSuccessfully §2enabled §aDev Vanish for §b" + target.getName());
                            target.sendMessage("§aDev Vanish was §2enabled §afor you by §b" + player.getName());
                            return true;
                        }

                    }
                } else {
                    player.sendMessage("§cOnly Firestar311 can set Dev Vanish for self and others.");
                    return true;
                }
            }
        }
        return true;
    }

    public ArrayList<UUID> getDevVanish() {
        return new ArrayList<>(devVanish);
    }

    /**
     * Work on switching all other listeners to use this method.
     *
     * @param listener The Listener to Register.
     */
    public void registerListener(Listener listener) {
        this.getServer().getPluginManager().registerEvents(listener, this);
    }

    public IEssentials getEssentials() {
        return essentials;
    }

    public Permission getPermission() {
        return perm;
    }

    public Economy getVaultEconomy() {
        return vaultEcon;
    }

    public WorldEditPlugin getWorldEdit() {
        return worldEdit;
    }

    public WorldGuardPlugin getWorldGuard() {
        return worldGuard;
    }

    public File getUserFolder() {
        return userFolder;
    }

    public static BrutusCore getInstance() {
        return instance;
    }

    public static void addSignChange(Sign sign) {
        signChanges.add(sign);
    }

    public static ArrayList<Sign> getSignChanges() {
        return new ArrayList<>(signChanges);
    }

    public static ArrayList<UUID> getJudges() {
        return new ArrayList<>(judges);
    }

    public CourtManager courts() {
        return courtManager;
    }

    public FriendsManager friends() {
        return friendsManager;
    }

    public LivingSpaceManager livingspaces() {
        return livingSpaceManager;
    }

    public PlayerManager players() {
        return playerManager;
    }

    public PunishmentManager punishments() {
        return punishmentManager;
    }

    public ReportsManager reports() {
        return reportsManager;
    }

    public SettingsManager settings() {
        return settingsManager;
    }

    public VanishManager vanish() {
        return vanishManager;
    }

    public EconomyManager economy() {
        return economyManager;
    }

    public JobManager jobs() {
        return jobManager;
    }

    public GUIManager gui() {
        return guiManager;
    }

    public VotifierManager votifier() {
        return votifierManager;
    }

    public BrutusScoreboardManager scoreboard() {
        return scoreboardManager;
    }

    public MOTDManager motd() {
        return motdManager;
    }

    public ChairManager chairs() {
        return chairManager;
    }

    public ChatManager chat() {
        return chatManager;
    }

    public PMManager messaging() {
        return pmManager;
    }

    private boolean setupPermissions() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        if (rsp == null) {
            return false;
        }
        perm = rsp.getProvider();
        return perm != null;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        vaultEcon = rsp.getProvider();
        return vaultEcon != null;
    }

    public static void sendDeveloperAlert(Player player, List<String> lines) {
        new BukkitRunnable() {
            public void run() {
                player.sendMessage("");
                player.sendMessage("§4§l--------------------------------");
                player.sendMessage("§c§l       [DEVELOPER ALERT]        ");
                player.sendMessage("§bThis is a notice from the developer of the server.");
                player.sendMessage("");
                lines.forEach(l -> player.sendMessage("§d" + l));
                player.sendMessage("");
                player.sendMessage("§4§l--------------------------------");
                player.sendMessage("");
            }
        }.runTaskLater(BrutusCore.getInstance(), 20);
    }

    // Firestar311 Online, Join, leaving methods
    public boolean isFirestar311Online() {
        return firestar311 != null;
    }

    public Player getFirestar311() {
        return firestar311;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        final Player player = e.getPlayer();

        boolean playerFirestar311 = false;

        if (firestar311 == null) {
            if (player.getUniqueId().equals(UUID.fromString("3f7891ce-5a73-4d52-a2ba-299839053fdc"))) {
                System.out.println("Firestar311 Joined");
                firestar311 = Bukkit.getPlayer(UUID.fromString("3f7891ce-5a73-4d52-a2ba-299839053fdc"));
                playerFirestar311 = true;
            }
        }

        final boolean playerFirestar311Task = playerFirestar311;

        new BukkitRunnable() {
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (!p.getName().equalsIgnoreCase("Firestar311") && playerFirestar311Task) {
                        p.sendMessage("§4-----------------------------------------------------");
                        p.sendMessage("§cNOTE: Firestar311 (Lead Developer) has just joined.");
                        p.sendMessage(
                                "      §cIf you try to speak to or message him, it may not be seen as he is usually testing things.");
                        p.sendMessage(
                                "      §cThis is not being rude, just that he is busy, thanks for understanding!");
                        p.sendMessage("§4-----------------------------------------------------");
                    }
                }
            }
        }.runTaskLater(this, 5L);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        final Player player = e.getPlayer();

        boolean playerFirestar311 = false;

        if (firestar311 != null) {
            if (player.getUniqueId().equals(firestar311.getUniqueId())) {
                firestar311 = null;
                playerFirestar311 = true;
            }
        }

        final boolean playerFirestar311Task = playerFirestar311;

        new BukkitRunnable() {
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (!p.getName().equalsIgnoreCase("Firestar311") && playerFirestar311Task) {
                        p.sendMessage("§4-----------------------------------------------------");
                        p.sendMessage("§cNOTE: Firestar311 (Lead Developer) has just left.");
                        p.sendMessage("§4-----------------------------------------------------");
                    }
                }
            }
        }.runTaskLater(this, 5L);
    }

    public static void createBrutusError(Exception e, String reason) {
        instance.settings().setErrors(instance.settings().getErrors() + 1);
        int errors = instance.settings().getErrors();
        File file = new File(instance.errorFolder + File.separator + "error" + errors + ".txt");
        Utils.createError(file, e);
        if (!(Bukkit.getOfflinePlayer(UUID.fromString("3f7891ce-5a73-4d52-a2ba-299839053fdc")).isOnline())) {
            String msg = prefix + "§4There has been an error while executing: §d" + reason + "\n" + prefix
                    + "§cError ID: §d" + errors + "\n" + prefix + "§4Please give the Error ID to §5Firestar311 §4ASAP!";
            Bukkit.broadcastMessage(msg);
        } else {
            Player firestar311 = Bukkit.getPlayer(UUID.fromString("3f7891ce-5a73-4d52-a2ba-299839053fdc"));
            firestar311.sendMessage("§4An error has occured. Error ID §d" + errors + " §4Location: §d" + reason);
        }
    }

    public static void createBrutusError(CommandSender sender, Exception e, String reason) {
        BrutusCore.getInstance().settings().setErrors(instance.settings().getErrors() + 1);
        int errors = instance.settings().getErrors();
        File file = new File(instance.errorFolder + "errors" + File.separator + "error" + errors + ".txt");
        Utils.createError(file, e);
        if (!(Bukkit.getOfflinePlayer(UUID.fromString("3f7891ce-5a73-4d52-a2ba-299839053fdc")).isOnline())) {
            sender.sendMessage(
                    "§4An internal error has occured. Please give this Error ID to §5Firestar311: §d" + errors);
        } else {
            Player firestar311 = Bukkit.getPlayer(UUID.fromString("3f7891ce-5a73-4d52-a2ba-299839053fdc"));
            firestar311.sendMessage("§4An error has occured. Error ID §d" + errors + " §4Location: §d" + reason);
        }
    }

    private void registerExecutor(CommandExecutor executor, String... commands) {
        for (String command : commands) {
            this.getCommand(command).setExecutor(executor);
        }
    }
}