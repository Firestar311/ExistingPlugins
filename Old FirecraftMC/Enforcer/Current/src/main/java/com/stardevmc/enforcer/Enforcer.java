package com.stardevmc.enforcer;

import com.stardevmc.enforcer.manager.*;
import com.stardevmc.enforcer.module.*;
import com.stardevmc.enforcer.objects.Variables;
import com.stardevmc.enforcer.objects.prison.Inmate;
import com.stardevmc.enforcer.objects.prison.Prison;
import com.stardevmc.enforcer.objects.actor.ConsoleActor;
import com.stardevmc.enforcer.objects.actor.PlayerActor;
import com.stardevmc.enforcer.objects.punishment.*;
import com.stardevmc.enforcer.objects.reports.Report;
import com.stardevmc.enforcer.objects.watchlist.*;
import com.stardevmc.enforcer.objects.rules.*;
import com.stardevmc.enforcer.objects.target.*;
import com.stardevmc.enforcer.objects.wave.Wave;
import com.stardevmc.enforcer.objects.wave.WaveEntry;
import com.stardevmc.enforcer.util.*;
import com.stardevmc.enforcer.objects.evidence.Evidence;
import com.starmediadev.lib.user.UserManager;
import com.starmediadev.lib.util.Utils;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;

public final class Enforcer extends JavaPlugin {
    
    private static Enforcer instance;
    
    static {
        Utils.registerConfigClasses(Report.class, Prison.class, ConsoleActor.class, PlayerActor.class, PlayerTarget.class, IPTarget.class, IPListTarget.class, BlacklistPunishment.class, JailPunishment.class, KickPunishment.class, PermanentBan.class, PermanentMute.class, WarnPunishment.class, Rule.class, RuleViolation.class, RulePunishment.class, Evidence.class, WatchlistEntry.class, WatchlistNote.class, TemporaryBan.class, TemporaryMute.class, WaveEntry.class, Wave.class, Inmate.class);
    }
    
    private ActorModule actorModule;
    private TargetModule targetModule;
    private HistoryModule historyModule;
    private PardonModule pardonModule;
    private Permission permission;
    private PrisonModule prisonModule;
    private PunishmentModule punishmentModule;
    private ReportModule reportModule;
    private RuleModule ruleModule;
    private SettingsManager settingsManager;
    private TrainingModule trainingModule;
    private WatchlistModule watchlistModule;
    private WaveModule waveModule;
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.color("&cOnly players may use that command."));
            return true;
        }
        
        Player player = ((Player) sender);
        
        if (!player.hasPermission(Perms.ENFORCER_ADMIN)) {
            player.sendMessage(Utils.color("&cInsufficient permission"));
            return true;
        }
        
        if (args.length == 0) {
            player.sendMessage(Utils.color("&aEnforcer Information"));
            player.sendMessage(Utils.color("&7Version: &e" + this.getDescription().getVersion()));
            player.sendMessage(Utils.color("&7Author: &eFirestar311"));
            player.sendMessage(Utils.color("&7---Settings---"));
            player.sendMessage(Utils.color("&7Using Display Names: &e" + getSettingsManager().isUsingDisplayNames()));
            player.sendMessage(Utils.color("&7Must Confirm Punishments: &e" + getPunishmentModule().confirmPunishments()));
            player.sendMessage(Utils.color("&7Prefix: &e" + getSettingsManager().getPrefix()));
            player.sendMessage(Utils.color("&7Server Name : &e" + getSettingsManager().getServerName()));
            return true;
        }
        
        if (Utils.checkCmdAliases(args, 0, "settings", "s")) {
            if (Utils.checkCmdAliases(args, 1, "toggledisplaynames", "tdn")) {
                if (!player.hasPermission(Perms.SETTINGS_DISPLAYNAMES)) {
                    player.sendMessage(Utils.color("&cYou do not have permission to toggle display names."));
                    return true;
                }
                getSettingsManager().setUsingDisplayNames(!getSettingsManager().isUsingDisplayNames());
                String message = Messages.USING_DISPLAYNAMES;
                message = message.replace(Variables.DISPLAY, getSettingsManager().isUsingDisplayNames() + "");
                sendOutputMessage(player, message);
            } else if (Utils.checkCmdAliases(args, 1, "confirmpunishments", "cp")) {
                if (!player.hasPermission(Perms.SETTINGS_CONFIRM_PUNISHMENTS)) {
                    player.sendMessage(Utils.color("&cYou cannot change the confirm punishments setting."));
                    return true;
                }
                getPunishmentModule().setConfirmPunishments(!getPunishmentModule().confirmPunishments());
                String message = Messages.SETTING_CONFIRMPUNISHMENTS;
                message = message.replace(Variables.DISPLAY, getPunishmentModule().confirmPunishments() + "");
                sendOutputMessage(player, message);
            } else if (Utils.checkCmdAliases(args, 1, "prefix")) {
                if (!player.hasPermission(Perms.SETTINGS_PREFIX)) {
                    player.sendMessage(Utils.color("&cYou cannot change the prefix."));
                    return true;
                }
                
                if (!(args.length > 0)) {
                    player.sendMessage(Utils.color("&cYou must provide a prefix to set."));
                    return true;
                }
                
                getSettingsManager().setPrefix(args[2]);
                player.sendMessage(Utils.color("&aYou set the prefix to " + getSettingsManager().getPrefix()));
            } else if (Utils.checkCmdAliases(args, 1, "server")) {
                if (!player.hasPermission(Perms.SETTINGS_SERVER)) {
                    player.sendMessage(Utils.color("&cYou cannot change the server."));
                    return true;
                }
                
                if (!(args.length > 0)) {
                    player.sendMessage(Utils.color("&cYou must provide a server name to set."));
                    return true;
                }
                
                getSettingsManager().setServerName(args[2]);
                player.sendMessage(Utils.color("&aYou set the server name to " + getSettingsManager().getServerName()));
            } else if (Utils.checkCmdAliases(args, 1, "replaceactorname", "ran")) {
                if (!player.hasPermission(Perms.SETTINGS_ACTOR_NAME)) {
                    player.sendMessage("&cYou cannot change that setting.");
                    return true;
                }
                
                getActorModule().setReplaceActorName(!getActorModule().replaceActorName());
                player.sendMessage("&aYou changed the setting &breplace actor names &ato &e" + getActorModule().replaceActorName()); //TODO Proper message
            }
        }
        
        
        return true;
    }
    
    public void onDisable() {
        this.punishmentModule.desetup();
        this.prisonModule.desetup();
        this.ruleModule.desetup();
        this.trainingModule.desetup();
        this.reportModule.desetup();
        this.waveModule.desetup();
        this.saveConfig();
    }
    
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();
        this.actorModule = new ActorModule(this);
        this.targetModule = new TargetModule(this);
        this.actorModule.setup();
        this.targetModule.setup();
        
        this.settingsManager = new SettingsManager(this);
        this.punishmentModule = new PunishmentModule(this, "punish", "ban", "tempban", "mute", "tempmute", "warn", "kick", "jail", "punishment", "blacklist");
        this.prisonModule = new PrisonModule(this, "prison");
        this.ruleModule = new RuleModule(this, "moderatorrules", "rules");
        this.historyModule = new HistoryModule(this, "history", "staffhistory");
        this.pardonModule = new PardonModule(this, "unban", "unmute", "unjail", "pardon", "unblacklist");
        this.trainingModule = new TrainingModule(this, "trainingmode");
        this.reportModule = new ReportModule(this, "report", "reportadmin");
        this.watchlistModule = new WatchlistModule(this, "watchlist", "quickteleport");
        this.waveModule = new WaveModule(this, "wave", "wave");
        this.punishmentModule.setup();
        this.prisonModule.setup();
        this.ruleModule.setup();
        this.reportModule.setup();
        this.trainingModule.setup();
        this.historyModule.setup();
        this.watchlistModule.setup();
        this.pardonModule.setup();
        this.waveModule.setup();
        
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (rsp != null) {
            this.permission = rsp.getProvider();
        } else {
            System.out.println("Could not find a Vault permissions provider, defaulting to regular permissions.");
        }
        
        new BukkitRunnable() {
            public void run() {
                Set<Punishment> punishments = getPunishmentModule().getManager().getActivePunishments();
                for (Punishment punishment : punishments) {
                    if (punishment instanceof Expireable) {
                        Expireable expireable = ((Expireable) punishment);
                        if (expireable.isExpired()) {
                            expireable.onExpire();
                            
                            if (punishment instanceof MutePunishment) {
                                Player p = punishment.getTarget().getPlayer();
                                if (p != null) {
                                    p.sendMessage(Utils.color("&aYour temporary mute has expired."));
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(this, 1200, 20);
    }
    
    public SettingsManager getSettingsManager() {
        return settingsManager;
    }
    
    private void sendOutputMessage(Player player, String message) {
        Messages.sendOutputMessage(player, message, this);
    }
    
    public static Enforcer getInstance() {
        return instance;
    }
    
    public Permission getPermission() {
        return permission;
    }
    
    public UserManager getPlayerManager() {
        return getServer().getServicesManager().getRegistration(UserManager.class).getProvider();
    }
    
    public ActorModule getActorModule() {
        return actorModule;
    }
    
    public TargetModule getTargetModule() {
        return targetModule;
    }
    
    public HistoryModule getHistoryModule() {
        return historyModule;
    }
    
    public PardonModule getPardonModule() {
        return pardonModule;
    }
    
    public PrisonModule getPrisonModule() {
        return prisonModule;
    }
    
    public PunishmentModule getPunishmentModule() {
        return punishmentModule;
    }
    
    public ReportModule getReportModule() {
        return reportModule;
    }
    
    public RuleModule getRuleModule() {
        return ruleModule;
    }
    
    public TrainingModule getTrainingModule() {
        return trainingModule;
    }
    
    public WatchlistModule getWatchlistModule() {
        return watchlistModule;
    }
    
    public WaveModule getWaveModule() {
        return waveModule;
    }
}