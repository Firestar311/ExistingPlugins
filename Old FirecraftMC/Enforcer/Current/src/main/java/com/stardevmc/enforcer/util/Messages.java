package com.stardevmc.enforcer.util;

import com.stardevmc.enforcer.Enforcer;
import com.stardevmc.enforcer.objects.Colors;
import com.stardevmc.enforcer.objects.enums.Priority;
import com.stardevmc.enforcer.objects.punishment.*;
import com.starmediadev.lib.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static com.stardevmc.enforcer.objects.Variables.*;

public final class Messages {
    public static String CLEAR_SELECTION = "&aCleared your selection.";
    public static String CREATE_PRISON_OVERFLOW = "&dYou were an overflow inhabitant of your former prison, so you were moved to a newly created prison.";
    public static String IGNORE_PUNISH_CONFIRMATION = "&2You are ignoring the punishment confirmation for this punishment.";
    public static String INVALID_NUMBER = "&cYou provided an invalid number.";
    public static String INVALID_PRISON_AMOUNT = "&cYou provided an invalid number for the max players, defaulting to 5";
    public static String INVALID_PRISON_ID = "&cYou provided an invalid number for the prison id. The id will be auto-assigned.";
    public static String JAIL_NO_PRISONS = "&cThere are no prisons created. Jail punishments are disabled until at least 1 is created.";
    public static String LOCATION_NOT_IN_PRISON = "&cThat location is not within the prison bounds.";
    public static String MAX_AMOUNT_CHANGED_MOVED = "&dThe prison you were in had its max players changed to a lower amount, so you were moved to a new prison.";
    public static String MAX_AMOUNT_SAME = "&cThe amount you provided is the same as the current max players value.";
    public static String MOVE_PRISON_REMOVED = "&cThe prison you were a part of was removed, you have been moved to a new prison.";
    public static String NO_REASON = "&cYou must supply a reason.";
    public static String NO_ACTIVE_PUNISHMENT = "&cThere are no active " + TYPE + " against that player.";
    public static String NO_HISTORY_RESULTS = "&cYou do not have history results yet, please use /history <name> first.";
    public static String NO_SELECTION = "&cYou do not have a selection currently set.";
    public static String NO_STAFF_RESULTS = "&cYou do not have staff history results yet, please use /staffhistory <name> first.";
    public static String ONLY_PLAYERS_AND_CONSOLE_CMD = "&cOnly console or players may use that command.";
    public static String PLAYER_NEVER_JOINED = "&cThat player has never joined the server.";
    public static String PUNISH_FORMAT = VISIBILITY + "&6(" + PREFIX + ") &4&l[i] &e<{id}> &b" + TARGET_STATUS + TARGET + " &fwas " + COLOR + PUNISHMENT + " &fby &b" + PUNISHER + " &ffor &a" + REASON;
    public static String LENGTH_FORMAT = "&c(" + LENGTH + ")";
    public static String PERMANENT_FORMAT = "&c(Permanent)";
    public static String PARDON_FORMAT = VISIBILITY + "&6(" + PREFIX + ") &4&l[i] &b" + TARGET + " &fwas " + COLOR + "un" + PUNISHMENT + " &fby &b" + REMOVER + " &ffor &a" + REASON;
    public static String PRISON_SET_SPAWN = "&6(" + PREFIX + ") &4&l[i] &b" + ACTOR + " &fchanged the spawn for the prison &b" + JAIL_ID + " &fto their location.";
    public static String PRISON_CREATE = "&6(" + PREFIX + ") &4&l[i] &b" + ACTOR + " &fcreated a prison with id &b" + JAIL_ID + " &fat their location.";
    public static String PRISON_SET_MAX_PLAYERS = "&6(" + PREFIX + ") &4&l[i] &b" + ACTOR + " &fchanged the max players for the prison &b" + JAIL_ID + " &fto &b" + MAX_PLAYERS;
    public static String PRISON_REMOVE = "&6(" + PREFIX + ") &4&l[i] &b" + ACTOR + " &fremoved the prison with id &b" + JAIL_ID;
    public static String PRISON_SET_NAME = "&6(" + PREFIX + ") &4&l[i] &b" + ACTOR + " &fset the name of the prison &b" + JAIL_ID + " &fto &b" + DISPLAY;
    public static String USING_DISPLAYNAMES = "&6(" + PREFIX + ") &4&l[i] &b" + ACTOR + " &fchanged using display names to &b" + DISPLAY;
    public static String TRAINING_MODE_GLOBAL = "&6(" + PREFIX + ") &4&l[i] &b" + ACTOR + " &fchanged global training mode to &b" + DISPLAY;
    public static String SETTING_CONFIRMPUNISHMENTS = "&6(" + PREFIX + ") &4&l[i] &b" + ACTOR + " &fchanged confirming punishments to &b" + DISPLAY;
    public static String TRAINING_MODE_INDIVIDUAL = "&6(" + PREFIX + ") &4&l[i] &b" + ACTOR + " &fchanged training mode for &e" + TARGET + " &fto &b" + DISPLAY;
    public static String PUNISHMENT_KICK = "&a{server} - {TYPE}\n\n&fStaff: &b" + ACTOR + "\n&fReason: &b" + REASON + "\n&fExpires: &c<expire>\n&f{pt} ID: &b{id}";
    public static String PRISON_REDEFINE = "&6(" + PREFIX + ") &4&l[i] &b" + ACTOR + " &fredefined the bounds for the prison &b" + DISPLAY;
    public static String RULE_CREATE = "&6(" + PREFIX + ") &4&l[i] &e<" + RULE_ID + "> &b" + ACTOR + " &fcreated a rule with the name &b" + RULE_NAME + " &fand the internal id &b" + RULE_INTERNALID ;
    public static String RULE_SET_DESCRIPTION = "&6(" + PREFIX + ") &4&l[i] &b" + ACTOR + " &fset the description of the rule &b" + RULE_NAME + " &fto &b" + RULE_DESCRIPTION;
    public static String RULE_SET_PLAYER_DESCRIPTION = "&6(" + PREFIX + ") &4&l[i] &b" + ACTOR + " &fset the player description of the rule &b" + RULE_NAME + " &fto &b" + RULE_DESCRIPTION;
    public static String REPORT_CREATE = "&4(REPORT) &d<{id}> &e" + TARGET + " &cwas reported for &e" + REASON + " &cby &e" + ACTOR;
    public static String REPORT_CANCEL = "&4(REPORT} &d<{id}> &e" + ACTOR + " &ecancelled their report against &e" + TARGET;
    public static String WATCHLIST_MESSAGE = "&9(WATCHLIST) &b" + ACTOR + " &f<type> &e" + TARGET + " &f<tp> the watchlist.";
    public static String WATCHLIST_ADD_NOTE = "&9(WATCHLIST) &b" + ACTOR + " &fadded a note to &e" + TARGET + "'s &fwatchlist entry.";
    public static String WATCHLIST_SET_PRIORITY = "&9(WATCHLIST) &b" + ACTOR + " &fset the priority on &e" + TARGET + "'s &fwatchlist entry to <priority>.";
    public static String REPORT_ASSIGN = "&4{REPORT) &d<{id}> &e" + ACTOR + " assigned the report to &e" + TARGET;
    public static String ONLY_PLAYERS_CMD = "&cOnly players may use that command.";
    public static String NOT_ENOUGH_ARGS = "&cYou did not provide enough arguments.";
    public static String COULD_NOT_FIND_PLAYER = "&cCould not find a player by that name.";
    public static String WATCHLIST_PLAYER_JOIN = "&9(WATCHLIST) &e" + TARGET + " &fhas joined and is on the watchlist for &e" + REASON;
    public static String NO_NAME_PROVIDED = "&cYou must provide a name.";
    public static String ERROR_LIST_OF_RESULTS = "&cThere was a problem getting the list of results for that player.";
    public static String PRISON_LOCATION_CHANGED = "&dThe prison location was changed by &b{player} &dso you have been teleported to the new location.";
    public static String REPORT_FILED_AGAINST = "&aA report has been filed against &b" + TARGET;
    public static String TARGET_JAIL = "&cYou have been jailed by &7" + ACTOR + " &cfor the reason &7" + REASON;
    public static String TARGET_UNJAIL = "&aYou have been unjailed by &b" + ACTOR;
    public static String MASS_PUNISH = VISIBILITY + "&6(" + PREFIX + ") &4&l[i] &b" + TARGET_STATUS + TARGET + " &fwas " + PUNISHMENT + " &fby &b" + PUNISHER + " &ffor &a" + REASON;
    public static String PUNISHMENT_NO_ACTION = "&c" + TARGET + " tried to " + ACTION + " but is " + TYPE + ".";
    public static String PUNISHMENT_NOTIFY_SILENCE = "&c" + TARGET + " continues to " + ACTION + ", silencing notifications.";
    public static String HISTORY_INFRACTION_LIMIT = "&c" + TARGET + " has " + AMOUNT + " infractions in their history.";
    public static String PRISON_NO_ACTION = "&cYou cannot " + ACTION + " blocks while in prison.";
    public static String PRISON_OUTSIDE_BOUNDS = "&cYou were outside of the prison bounds, teleporting you to the spawn location.";
    public static String JAILED_WHILE_OFFLINE = "&aYou have been unjailed while you were offline.";
    public static String INVENTORY_RESTORED = "&7&oYour inventory items have been restored.";
    public static String INVENTORY_RESTORE_PROBLEM = "&cThere was a problem restoring your inventory. Please contact the plugin developer";
    
//    private static Map<Integer, String> KICK_FORMAT = new TreeMap<>();
//    private static Map<Integer, String> CONFIRM_FORMAT = new TreeMap<>();
    
    public static void sendNotifyMessage(String message) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission(Perms.NOTIFY_PUNISHMENTS)) {
                p.sendMessage(Utils.color(message));
            }
        }
    }
    
    public static String watchlistMessage(String target, String staff, String type) {
        String format = WATCHLIST_MESSAGE;
        if (type.equalsIgnoreCase("removed")) {
            format = format.replace("<tp>", "from");
        } else if (type.equalsIgnoreCase("added")) {
            format = format.replace("<tp>", "to");
        }
        
        format = format.replace("<type>", type);
        format = format.replace(ACTOR, staff);
        format = format.replace(TARGET, target);
        return Utils.color(format);
    }
    
    public static String noPermissionCommand(String permission) {
        return Utils.color("&cYou must have the permission &7(" + permission + ") &cto use that command.");
    }
    
    public static String noPermissionAction(String permission) {
        return Utils.color("&cYou must have the permission &7(" + permission + ") &cto perform that action.");
    }
    
    public static String formatPunishKick(Punishment IPunishment) {
        String format = PUNISHMENT_KICK;
        if (IPunishment instanceof BanPunishment) {
            format = format.replace("{TYPE}", Colors.BAN + "&lBANNED");
            format = format.replace("{pt}", "Ban");
            if (IPunishment instanceof Expireable) {
                format = format.replace("<expire>", ((Expireable) IPunishment).formatExpireTime());
            } else {
                format = format.replace("<expire>", "Permanent");
            }
        } else if (IPunishment instanceof KickPunishment) {
            format = format.replace("{TYPE}", Colors.KICK + "&lKICKED");
            format = format.replace("{pt}", "Kick");
            format = format.replace("<expire>", "N/A");
        }
        format = format.replace(ACTOR, IPunishment.getPunisherName());
        format = format.replace(REASON, IPunishment.getReason());
        format = format.replace("{id}", IPunishment.getId() + "");
        format = format.replace("{server}", Enforcer.getInstance().getSettingsManager().getServerName());
        
        return format;
    }
    
    public static String watchlistAddNoteMessage(String targetName, String actorName) {
        String format = WATCHLIST_ADD_NOTE;
        format = format.replace(ACTOR, actorName);
        format = format.replace(TARGET, targetName);
        return Utils.color(format);
    }
    
    public static String watchlistSetPriority(String targetName, String actorName, Priority priority) {
        String format = WATCHLIST_SET_PRIORITY;
        format = format.replace("<priority>", priority.name());
        format = format.replace(TARGET, targetName);
        format = format.replace(ACTOR, actorName);
        return Utils.color(format);
    }
    
    private Messages() {}
    
    
    public static void sendOutputMessage(Player player, String message, Enforcer plugin) {
        for (Player pm : Bukkit.getOnlinePlayers()) {
            if (!pm.hasPermission(Perms.NOTIFY_PUNISHMENTS)) {
                continue;
            }
            String msg = message;
            if (pm.getUniqueId().equals(player.getUniqueId())) {
                if (plugin.getActorModule().replaceActorName()) {
                    msg = msg.replace(ACTOR, "&lYou");
                    msg = msg.replace("their", "your");
                }
            } else {
                if (plugin.getSettingsManager().isUsingDisplayNames()) {
                    msg = msg.replace(ACTOR, player.getDisplayName());
                } else {
                    msg = msg.replace(ACTOR, player.getName());
                }
            }
            msg = msg.replace(PREFIX, plugin.getSettingsManager().getPrefix());
            pm.sendMessage(Utils.color(msg));
        }
    }
    
    public static String noActivePunishment(String punishment) {
        return NO_ACTIVE_PUNISHMENT.replace("<type>", punishment);
    }
    
    public static String prisonLocationChanged(String name) {
        return PRISON_LOCATION_CHANGED.replace("{player}", name);
    }
    
    public static String targetJail(String actor, String reason) {
        return TARGET_JAIL.replace(ACTOR, actor).replace(REASON, reason);
    }
    
    public static String playerUnjailed(String removerName) {
        return TARGET_UNJAIL.replace(ACTOR,removerName);
    }
    
    public static String reportFiledAgainst(String name) {
        return REPORT_FILED_AGAINST.replace(TARGET, name);
    }
    
    public static String punishmentNoAction(String name, String action, String type) {
        return PUNISHMENT_NO_ACTION.replace(TARGET, name).replace(ACTION, action).replace(TYPE, type);
    }
    
    public static String punishmentNotifySilence(String name, String action) {
        return PUNISHMENT_NOTIFY_SILENCE.replace(TARGET, name).replace(ACTION, action);
    }
    
    public static String historyInfractionLimit(String name, int amount) {
        return HISTORY_INFRACTION_LIMIT.replace(TARGET, name).replace(AMOUNT, name);
    }
    
    public static String prisonNoAction(String action) {
        return PRISON_NO_ACTION.replace(ACTION, action);
    }
}
