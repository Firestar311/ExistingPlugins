package com.stardevmc.enforcer.modules.punishments.cmds;

import com.stardevmc.enforcer.Enforcer;
import com.stardevmc.enforcer.manager.PunishmentManager;
import com.stardevmc.enforcer.manager.RuleManager;
import com.stardevmc.enforcer.modules.punishments.gui.PunishGUI;
import com.stardevmc.enforcer.objects.Flag;
import com.stardevmc.enforcer.objects.actor.Actor;
import com.stardevmc.enforcer.objects.enums.Visibility;
import com.stardevmc.enforcer.objects.evidence.Evidence;
import com.stardevmc.enforcer.objects.evidence.EvidenceType;
import com.stardevmc.enforcer.objects.prison.Prison;
import com.stardevmc.enforcer.objects.punishment.*;
import com.stardevmc.enforcer.objects.punishment.Punishment.Type;
import com.stardevmc.enforcer.objects.rules.*;
import com.stardevmc.enforcer.objects.target.*;
import com.stardevmc.enforcer.objects.wave.Wave;
import com.stardevmc.enforcer.util.*;
import com.starmediadev.lib.user.User;
import com.starmediadev.lib.util.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.Map.Entry;

public class PunishmentCommands implements TabExecutor {
    
    private Enforcer plugin;
    
    private Map<String, List<PunishmentBuilder>> punishmentBuilders = new HashMap<>();
    
    public PunishmentCommands(Enforcer plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Actor actor = plugin.getActorModule().getManager().getActor(sender);
        if (actor == null) {
            sender.sendMessage(Utils.color(Messages.ONLY_PLAYERS_AND_CONSOLE_CMD));
            return true;
        }
        
        boolean confirmPunishments = plugin.getPunishmentModule().confirmPunishments();
        boolean activeWave = plugin.getWaveModule().mustHaveActiveWave();
        
        if (cmd.getName().equals("punishment")) {
            if (!(args.length > 0)) {
                return true;
            }
            
            if (!sender.hasPermission("enforcer.command.punishment")) {
                return true;
            }
            
            PunishmentManager punishmentManager = plugin.getPunishmentModule().getManager();
            
            if (args[0].equalsIgnoreCase("confirm")) {
                if (!(args.length > 1)) {
                    return true;
                }
                
                String code = args[1];
                if (!this.punishmentBuilders.containsKey(code)) {
                    return true;
                }
                
                List<PunishmentBuilder> builders = this.punishmentBuilders.get(code);
                for (PunishmentBuilder punishmentBuilder : builders) {
                    Punishment IPunishment = punishmentBuilder.build();
                    
                    if (IPunishment instanceof JailPunishment) {
                        punishmentManager.addJailPunishment((JailPunishment) IPunishment);
                    } else {
                        punishmentManager.addPunishment(IPunishment);
                    }
                    
                    IPunishment.executePunishment();
                }
            } else if (args[0].equalsIgnoreCase("cancel")) {
                if (!(args.length > 1)) {
                    return true;
                }
                
                String code = args[1];
                this.punishmentBuilders.remove(code);
                sender.sendMessage(Utils.color("&aCancelled that/those punishment(s)"));
            } else {
                Player player = ((Player) sender);
                
                Punishment IPunishment = punishmentManager.getPunishment(args[0]);
                
                if (IPunishment == null) {
                    player.sendMessage(Utils.color("&cCould not find a punishment with that id."));
                    return true;
                }
                
                if (Utils.checkCmdAliases(args, 1, "setevidence", "se")) {
                    if (!player.hasPermission(Perms.PUNISHMENTS_SET_EVIDENCE)) {
                        player.sendMessage(Messages.noPermissionCommand(Perms.PUNISHMENTS_SET_EVIDENCE));
                        return true;
                    }
                    
                    if (args.length != 2) {
                        player.sendMessage(Utils.color("&cUsage: /punishment <punishment id> setevidence|se <link>"));
                        return true;
                    }
                    
                    Evidence evidence = new Evidence(0, player.getUniqueId(), EvidenceType.STAFF, args[2]);
                    IPunishment.setEvidence(evidence);
                    player.sendMessage(Utils.color("&aYou set the evidence of the punishment &b" + IPunishment.getId() + " &ato &b" + evidence.getLink()));
                } else if (Utils.checkCmdAliases(args, 1, "info", "i")) {
                    Punishment punishment = punishmentManager.getPunishment(args[0]);
                    sender.sendMessage(Utils.color("&aID: " + punishment.getId()));
                    sender.sendMessage(Utils.color("&aActor: " + punishment.getActor().getName()));
                    sender.sendMessage(Utils.color("&aReason: " + punishment.getReason()));
                    sender.sendMessage(Utils.color("&aDate: " + Constants.DATE_FORMAT.format(new Date(punishment.getDate()))));
                    sender.sendMessage(Utils.color("&aActive: " + punishment.isActive()));
                    sender.sendMessage(Utils.color("&aTarget: " + punishment.getTarget().getName()));
                    sender.sendMessage(Utils.color("&aType: " + punishment.getType().getDisplayName()));
                    sender.sendMessage(Utils.color("&aTraining Mode: " + punishment.isTrainingPunishment()));
                    sender.sendMessage(Utils.color("&aWas Offline: " + punishment.wasOffline()));
                    sender.sendMessage(Utils.color("&aServer: " + punishment.getServer()));
                    sender.sendMessage(Utils.color("&aVisibility: " + punishment.getVisibility()));
                    if (punishment.getEvidence() != null) {
                        sender.sendMessage(Utils.color("&aEvidence: " + punishment.getEvidence()));
                    }
                    if (punishment instanceof Expireable) {
                        sender.sendMessage(Utils.color("&aExpire Date: " + Constants.DATE_FORMAT.format(new Date(((Expireable) punishment).getExpireDate()))));
                    }
                    if (punishment.getRuleId() != -1) {
                        sender.sendMessage(Utils.color("&aRule ID: " + punishment.getRuleId()));
                        sender.sendMessage(Utils.color("&aRule Violation: " + punishment.getViolationNumber()));
                    }
                    if (punishment.getRemover() != null) {
                        sender.sendMessage(Utils.color("&aRemover: " + punishment.getRemover().getName()));
                        sender.sendMessage(Utils.color("&aRemoved Date: " + Constants.DATE_FORMAT.format(new Date(punishment.getRemovedDate()))));
                        sender.sendMessage(Utils.color("&aRemoved Reason: " + punishment.getRemovedReason()));
                        sender.sendMessage(Utils.color("&aPardon Visibility: " + punishment.getPardonVisibility().name()));
                    }
                    if (punishment instanceof Acknowledgeable) {
                        sender.sendMessage(Utils.color("&aAcknowledged: " + ((Acknowledgeable) punishment).isAcknowledged()));
                    }
                    if (punishment instanceof JailPunishment) {
                        sender.sendMessage("&aPrison ID: " + ((JailPunishment) punishment).getPrisonId());
                    }
                }
            }
            
            return true;
        }
        
        String prefix = plugin.getSettingsManager().getPrefix();
        
        if (!(args.length > 0)) {
            sender.sendMessage(Utils.color("&cYou must provide a player to punish."));
            return true;
        }
        
        String targetArg = args[0];
        Target target = Enforcer.getInstance().getTargetModule().getManager().getTarget(targetArg);
        
        if (target == null) {
            sender.sendMessage(Utils.color("&cInvalid target: " + targetArg));
            return true;
        }
        
        if (target instanceof IPTarget || target instanceof IPListTarget) {
            if (cmd.getName().equalsIgnoreCase("blacklist")) {
                BlacklistPunishment blacklistPunishment = new BlacklistPunishment(plugin.getSettingsManager().getPrefix(), actor, target, EnforcerUtils.getReason(1, args), System.currentTimeMillis());
                plugin.getPunishmentModule().getManager().addPunishment(blacklistPunishment);
                blacklistPunishment.executePunishment();
            }
            return true;
        }
        
        if (!(target instanceof PlayerTarget)) {
            return true;
        }
        
        User info = ((PlayerTarget) target).getUser();
        
        if (Bukkit.getPlayer(info.getUniqueId()) == null) {
            if (!sender.hasPermission(Perms.OFFLINE_PUNISH)) {
                sender.sendMessage(Messages.noPermissionCommand(Perms.OFFLINE_PUNISH));
                return true;
            }
        }
        
        try {
            if (sender instanceof Player) {
                Player player = ((Player) sender);
                net.milkbowl.vault.permission.Permission perms = Enforcer.getInstance().getPermission();
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(info.getUniqueId());
                String groupName = perms.getPrimaryGroup(player.getWorld().getName(), offlinePlayer).toLowerCase();
                if (groupName != null && !groupName.equals("")) {
                    if (!player.hasPermission("enforcer.immunity." + groupName)) {
                        player.sendMessage(Utils.color("&cYou cannot punish that player because they are immune."));
                        return true;
                    }
                }
            }
        } catch (Exception ignored) {}
        
        Visibility visibility = Visibility.STAFF_ONLY;
        boolean ignoreTraining = false, ignoreConfirm = false, addToWave = false;
        EnumSet<Flag> flags = EnumSet.noneOf(Flag.class);
        
        for (String arg : args) {
            Flag flag = Flag.matchFlag(arg);
            if (flag != null) {
                flags.add(flag);
            }
        }
        
        for (Flag flag : flags) {
            if (flag == Flag.PUBLIC) {
                visibility = Visibility.PUBLIC;
            }
            if (flag == Flag.SILENT) {
                visibility = Visibility.SILENT;
            }
            if (flag == Flag.IGNORE_TRAINING) {
                if (!sender.hasPermission(Perms.FLAG_IGNORE_TRAINING)) {
                    sender.sendMessage(Utils.color("&cYou do not have permission to ignore training mode."));
                    return true;
                }
                ignoreTraining = true;
            }
            
            if (flag == Flag.IGNORE_CONFIRM) {
                if (!sender.hasPermission(Perms.FLAG_IGNORE_CONFIRM)) {
                    sender.sendMessage(Utils.color("&cYou do not have permission to ignore confirmation."));
                    return true;
                }
                
                ignoreConfirm = true;
            }
            
            if (flag == Flag.WAVE) {
                //Setting (Implemented in Wave Module, need to redo how modules work in Enforcer class, then a few permission checks
                addToWave = true;
            }
        }
        
        if (cmd.getName().equalsIgnoreCase("punish")) {
            if (sender instanceof ConsoleCommandSender) {
                sender.sendMessage(Utils.color(Messages.ONLY_PLAYERS_CMD));
                return true;
            }
            
            Player player = ((Player) sender);
            if (!player.hasPermission(Perms.PUNISH_COMMAND)) {
                player.sendMessage(Messages.noPermissionCommand(Perms.PUNISH_COMMAND));
                return true;
            }
            
            if (args.length == 1) {
                PunishGUI punishGUI = new PunishGUI(plugin, player, target);
                player.openInventory(punishGUI.getInventory());
                return true;
            }
            
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                if (!args[i].startsWith("-")) {
                    sb.append(args[i]);
                    if (i != args.length - 1) {
                        sb.append(" ");
                    }
                }
            }
            
            RuleManager ruleManager = plugin.getRuleModule().getManager();
            
            Rule rule = ruleManager.getRule(sb.toString());
            if (rule == null) {
                player.sendMessage(Utils.color("&cThe value you provided does not match to a valid rule."));
                return true;
            }
            
            if (!rule.hasPermission(player)) {
                player.sendMessage(Utils.color("&cYou do not have permission to punish with this rule."));
                return true;
            }
            
            Entry<Integer, Integer> offenseNumbers = ruleManager.getNextViolation(player.getUniqueId(), info.getUniqueId(), rule);
            
            RuleViolation offense = rule.getViolation(offenseNumbers.getKey());
            if (offense == null) {
                player.sendMessage(Utils.color("&cThere was a severe problem getting the next offense, use a manual punishment if an emergency, otherwise, contact the plugin developer"));
                return true;
            }
            
            if (!offense.hasPermission(player)) {
                player.sendMessage(Utils.color("&cYou do not have permission to punish with this offense."));
                return true;
            }
            
            String server = plugin.getSettingsManager().getPrefix();
            long currentTime = System.currentTimeMillis();
            UUID punisher = player.getUniqueId();
            String reason = rule.getName() + " Violation #" + offenseNumbers.getValue();
            List<PunishmentBuilder> puBuilders = new ArrayList<>();
            for (RulePunishment rulePunishment : offense.getPunishments().values()) {
                PunishmentBuilder puBuilder = new PunishmentBuilder(target);
                puBuilder.setType(rulePunishment.getType());
                puBuilder.setReason(reason).setPunisher(punisher).setServer(server).setDate(currentTime).setLength(rulePunishment.getLength());
                puBuilder.setRuleId(rule.getId());
                puBuilder.setViolationNumber(offenseNumbers.getValue());
                puBuilders.add(puBuilder);
            }
            
            if (!addToWave) {
                if (puBuilders.size() == 1) {
                    PunishmentBuilder puBuilder = puBuilders.get(0);
                    if (confirmPunishments) {
                        if (!ignoreConfirm) {
                            String code = Code.generateNewCode(6);
                            addPunishmentBuilder(code, puBuilder);
                            sendConfirmMessage(player, puBuilder, code);
                            return true;
                        }
                    }
                } else {
                    String code = Code.generateNewCode(6);
                    if (!ignoreConfirm) {
                        sendConfirmMessage(player, code, target, reason, puBuilders);
                        addPunishmentBuilders(code, puBuilders.toArray(new PunishmentBuilder[0]));
                        return true;
                    }
                }
                if (ignoreConfirm) {
                    player.sendMessage(Utils.color(Messages.IGNORE_PUNISH_CONFIRMATION));
                }
                
                for (PunishmentBuilder puBuilder : puBuilders) {
                    Punishment punishment = puBuilder.build();
                    plugin.getPunishmentModule().getManager().addPunishment(punishment);
                    punishment.executePunishment();
                }
            } else {
                Wave wave = plugin.getWaveModule().getManager().getWave("current");
                if (wave == null && activeWave) {
                    wave = plugin.getWaveModule().getManager().createWave(plugin.getActorModule().getManager().getActor("Console"));
                    plugin.getWaveModule().getManager().setCurrentWave(wave);
                }
                
                for (PunishmentBuilder builder : puBuilders) {
                    Punishment punishment = builder.build();
                    plugin.getPunishmentModule().getManager().addPunishment(punishment);
                    wave.addPunishment(punishment);
                    sender.sendMessage(Utils.color("&aYou added the punishment " + punishment.getId() + " to the wave " + wave.getId()));
                }
            }
        } else {
            long currentTime = System.currentTimeMillis();
            
            PunishmentBuilder puBuilder = new PunishmentBuilder(target);
            puBuilder.setDate(currentTime).setPunisher(actor).setVisibility(visibility).setServer(prefix);
            if (!ignoreTraining) {
                puBuilder.setTrainingMode(plugin.getTrainingModule().getManager().isTrainingMode(actor));
            }
            
            String reason = "";
            
            if (cmd.getName().equalsIgnoreCase("ban")) {
                if (!sender.hasPermission(Perms.BAN)) {
                    sender.sendMessage(Messages.noPermissionCommand(Perms.BAN));
                    return true;
                }
                reason = EnforcerUtils.getReason(1, args);
                
                puBuilder.setType(Type.PERMANENT_BAN);
            } else if (cmd.getName().equalsIgnoreCase("tempban")) {
                if (!sender.hasPermission(Perms.TEMP_BAN)) {
                    sender.sendMessage(Messages.noPermissionCommand(Perms.TEMP_BAN));
                    return true;
                }
                
                long expire = Utils.parseTime(args[1]);
                
                if (!(args.length > 2)) {
                    sender.sendMessage(Utils.color(Messages.NO_REASON));
                    return true;
                }
                reason = EnforcerUtils.getReason(2, args);
                puBuilder.setType(Type.TEMPORARY_BAN).setLength(expire);
            } else if (cmd.getName().equalsIgnoreCase("mute")) {
                if (!sender.hasPermission(Perms.MUTE)) {
                    sender.sendMessage(Messages.noPermissionCommand(Perms.MUTE));
                    return true;
                }
                
                reason = EnforcerUtils.getReason(1, args);
                puBuilder.setType(Type.PERMANENT_MUTE);
            } else if (cmd.getName().equalsIgnoreCase("tempmute")) {
                if (!sender.hasPermission(Perms.TEMP_MUTE)) {
                    sender.sendMessage(Messages.noPermissionCommand(Perms.TEMP_MUTE));
                    return true;
                }
                
                long expire = Utils.parseTime(args[1]);
                
                if (!(args.length > 2)) {
                    sender.sendMessage(Utils.color(Messages.NO_REASON));
                    return true;
                }
                
                reason = EnforcerUtils.getReason(2, args);
                puBuilder.setType(Type.TEMPORARY_MUTE).setLength(expire);
            } else if (cmd.getName().equalsIgnoreCase("kick")) {
                if (!sender.hasPermission(Perms.KICK)) {
                    sender.sendMessage(Messages.noPermissionCommand(Perms.KICK));
                    return true;
                }
                
                reason = EnforcerUtils.getReason(1, args);
                puBuilder.setType(Type.KICK);
            } else if (cmd.getName().equalsIgnoreCase("warn")) {
                if (!sender.hasPermission(Perms.WARN)) {
                    sender.sendMessage(Messages.noPermissionCommand(Perms.WARN));
                    return true;
                }
                
                reason = EnforcerUtils.getReason(1, args);
                puBuilder.setType(Type.WARN);
            } else if (cmd.getName().equalsIgnoreCase("jail")) {
                if (!sender.hasPermission(Perms.JAIL)) {
                    sender.sendMessage(Messages.noPermissionCommand(Perms.JAIL));
                    return true;
                }
                
                if (plugin.getPrisonModule().getManager().getPrisons().isEmpty()) {
                    sender.sendMessage(Utils.color(Messages.JAIL_NO_PRISONS));
                    return true;
                }
                
                reason = EnforcerUtils.getReason(1, args);
                puBuilder.setType(Type.JAIL);
                Prison prison = plugin.getPrisonModule().getManager().findPrison();
                puBuilder.setPrisonId(prison.getId());
            }
            
            if (StringUtils.isEmpty(reason)) {
                sender.sendMessage(Utils.color("&cYou must provide a reason."));
                return true;
            }
            
            puBuilder.setReason(reason);
            
            if (!addToWave) {
                if (confirmPunishments) {
                    if (!ignoreConfirm) {
                        String code = Code.generateNewCode(6);
                        addPunishmentBuilder(code, puBuilder);
                        if (sender instanceof Player) {
                            sendConfirmMessage(((Player) sender), puBuilder, code);
                        }
                        return true;
                    }
                    sender.sendMessage(Utils.color(Messages.IGNORE_PUNISH_CONFIRMATION));
                }
                
                Punishment punishment = puBuilder.build();
                plugin.getPunishmentModule().getManager().addPunishment(punishment);
                punishment.executePunishment();
            } else {
                Wave wave = plugin.getWaveModule().getManager().getWave("current");
                if (wave == null && activeWave) {
                    wave = plugin.getWaveModule().getManager().createWave(plugin.getActorModule().getManager().getActor("Console"));
                    plugin.getWaveModule().getManager().setCurrentWave(wave);
                }
                
                Punishment punishment = puBuilder.build();
                plugin.getPunishmentModule().getManager().addPunishment(punishment);
                wave.addPunishment(punishment);
                sender.sendMessage(Utils.color("&aYou added the punishment " + punishment.getId() + " to the wave " + wave.getId()));
            }
        }
        return true;
    }
    
    private void addPunishmentBuilder(String code, PunishmentBuilder builder) {
        if (this.punishmentBuilders.containsKey(code)) {
            this.punishmentBuilders.get(code).add(builder);
        } else {
            this.punishmentBuilders.put(code, new ArrayList<>(Collections.singletonList(builder)));
        }
    }
    
    private void sendConfirmMessage(Player player, PunishmentBuilder puBuilder, String code) {
        //TODO Convert this to the Messages class
        Target targetInfo = puBuilder.getTarget();
        player.sendMessage("");
        player.sendMessage(Utils.color("&4╔═════════════════════════════"));
        player.sendMessage(Utils.color("&4║ &fYou are about to punish &b" + targetInfo.getName() + " "));
        player.sendMessage(Utils.color("&4║ &fReason: &e" + puBuilder.getReason()));
        String punishmentString = EnforcerUtils.getPunishString(puBuilder);
        player.sendMessage(Utils.color("&4║ &fThis action will result in the punishment " + punishmentString));
        BaseComponent[] baseComponents = new ComponentBuilder("║ ").color(ChatColor.DARK_RED).append("Click to ").color(ChatColor.WHITE).append("[Confirm]").color(ChatColor.GREEN).bold(true).event(new HoverEvent(Action.SHOW_TEXT, new ComponentBuilder("Click to confirm punishment").create())).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/punishment confirm " + code)).append(" [Cancel]").color(ChatColor.RED).bold(true).event(new HoverEvent(Action.SHOW_TEXT, new ComponentBuilder("Click to cancel punishment").create())).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/punishment cancel " + code)).create();
        player.spigot().sendMessage(baseComponents);
        player.sendMessage(Utils.color("&4╚═════════════════════════════"));
        player.sendMessage("");
    }
    
    private void sendConfirmMessage(Player player, String code, Target target, String reason, List<PunishmentBuilder> builders) {
        //TODO Convert this to the Messages class
        player.sendMessage("");
        player.sendMessage(Utils.color("&4╔═════════════════════════════"));
        player.sendMessage(Utils.color("&4║ &fYou are about to punish &b" + target.getName() + " "));
        player.sendMessage(Utils.color("&4║ &fReason: &e" + reason));
        List<String> punishmentStrings = new ArrayList<>();
        for (PunishmentBuilder puBuilder : builders) {
            punishmentStrings.add(EnforcerUtils.getPunishString(puBuilder));
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < punishmentStrings.size(); i++) {
            String puString = punishmentStrings.get(i);
            sb.append(Utils.color(" &8- ")).append(Utils.color(puString));
            if (i < punishmentStrings.size() - 1) {
                sb.append("\n");
            }
        }
        
        BaseComponent[] punishmentComponents = new ComponentBuilder("║ ").color(ChatColor.DARK_RED).append("This action will result in ").color(ChatColor.WHITE).append(builders.size() + " punishments").color(ChatColor.DARK_AQUA).event(new HoverEvent(Action.SHOW_TEXT, TextComponent.fromLegacyText(sb.toString()))).create();
        player.spigot().sendMessage(punishmentComponents);
        //player.sendMessage(Utils.color("&4║ &fThis action will result in the punishment " + punishmentString));
        
        BaseComponent[] baseComponents = new ComponentBuilder("║ ").color(ChatColor.DARK_RED).append("Click to ").color(ChatColor.WHITE).append("[Confirm]").color(ChatColor.GREEN).bold(true).event(new HoverEvent(Action.SHOW_TEXT, new ComponentBuilder("Click to confirm punishment").create())).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/punishment confirm " + code)).append(" [Cancel]").color(ChatColor.RED).bold(true).event(new HoverEvent(Action.SHOW_TEXT, new ComponentBuilder("Click to cancel punishment").create())).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/punishment cancel " + code)).create();
        player.spigot().sendMessage(baseComponents);
        player.sendMessage(Utils.color("&4╚═════════════════════════════"));
        player.sendMessage("");
    }
    
    private void addPunishmentBuilders(String code, PunishmentBuilder... builders) {
        if (this.punishmentBuilders.containsKey(code)) {
            this.punishmentBuilders.get(code).addAll(Arrays.asList(builders));
        } else {
            this.punishmentBuilders.put(code, new ArrayList<>(Arrays.asList(builders)));
        }
    }
    
    private void checkTraining(Punishment IPunishment, boolean ignoreTraining) {
        if (plugin.getTrainingModule().getManager().isTrainingMode(IPunishment.getActor())) {
            if (ignoreTraining) {
                IPunishment.setTrainingMode(false);
            }
        }
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (cmd.getName().equalsIgnoreCase("punish") || cmd.getName().equalsIgnoreCase("pardon") || cmd.getName().equalsIgnoreCase("ban") || cmd.getName().equalsIgnoreCase("mute") || cmd.getName().equalsIgnoreCase("kick") || cmd.getName().equalsIgnoreCase("warn") || cmd.getName().equalsIgnoreCase("jail") || cmd.getName().equalsIgnoreCase("tempban") || cmd.getName().equalsIgnoreCase("tempmute")) {
            List<String> players = new ArrayList<>();
            for (User user : plugin.getPlayerManager().getUsers().values()) {
                if (!StringUtils.isEmpty(user.getLastName())) {
                    players.add(user.getLastName());
                }
            }
            
            return players;
        }
        
        return new ArrayList<>();
    }
}
