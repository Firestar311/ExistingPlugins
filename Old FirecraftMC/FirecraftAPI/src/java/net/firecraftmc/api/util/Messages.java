package net.firecraftmc.api.util;

import net.firecraftmc.api.enums.Channel;
import net.firecraftmc.api.enums.Rank;
import net.firecraftmc.api.model.Report;
import net.firecraftmc.api.model.server.FirecraftServer;
import net.firecraftmc.api.model.server.Warp;
import net.firecraftmc.api.punishments.Punishment;

/**
 * All of the messages used between the Core, Socket and Shared
 */
public final class Messages {
    private Messages() {
    }
    /*
    Error color: RED (&c)
    None-Error Color: AQUA (&b)
    None-Error Value Color: GOLD (&e)
     */

    public static final String ERROR_COLOR = "&c";
    public static final String NORMAL_COLOR = "&7";
    public static final String VALUE_COLOR = "&e";

    //Action Bars
    public static final String actionBar_Nicked = "&fYou are currently &4NICKED";
    public static final String actionBar_Vanished = "&fYou are currently &9VANISHED";
    public static final String actionBar_Recording = "&fYou are currently &eRECORDING";
    public static final String actionBar_Staffmode = "&fYou are currently in &aSTAFF MODE";
    public static final String actionBar_Incognito = "&fYou are currently &5INCOGNITO";
    //Normal Messages
    public static final String noPermission = "<ec>You do not have permission to do that.";
    public static final String notEnoughArgs = "<ec>You do not have enough arguments.";
    public static final String onlyPlayers = "§cOnly players can do that.";
    public static final String jailedNoCmds = "<ec>You cannot use commands while in jail.";
    public static final String unAckWarnNoCmds = "<ec>You cannot use commands while you have an unacknowledged warning.";
    public static final String chatNoData = "<ec>You cannot speak because your player data has not be loaded yet.";
    public static final String chatJailed = "<ec>You cannot speak because you are currently jailed.";
    public static final String noTalkGlobal = "<ec>You cannot chat in global while vanished.";
    public static final String onlyStaff = "<ec>Only staff members may do that.";
    public static final String alreadyInChannel = "<ec>You are already speaking in that channel.";

    public static String channelSwitch(Channel channel) {
        return "<nc>You are now speaking in channel {channel}<nc>.".replace("{channel}", channel.getColor() + channel.toString());
    }

    public static final String invalidSubCommand = "<ec>The sub command you provided is not valid.";
    public static final String invalidGamemode = "<ec>The gamemode you provided was invalid.";
    public static final String noItemInHand = "<ec>You must have an item in your hand.";
    public static final String chatCleared = "<nc>Chat has been cleared.";

    public static final String resetNickError = "<ec>There was a problem while resetting your nickname.";
    
    public static String joinUnAckWarning(String code) {
        return "<ec>You have an unacknowledged warning, you must type the code <nc>{code} before you can speak.".replace("{code}", code);
    }

    public static String banMessage(Punishment punishment, String expire) {
        String message = "&4&lBANNED\n&fStaff: &c{punisher}\n&fReason: &c{reason}\n&fExpires: &c{expire}\n&fPunishment ID: &c{id}";
        message = message.replace("{punisher}", punishment.getPunisherName());
        message = message.replace("{reason}", punishment.getReason());
        message = message.replace("{expire}", expire);
        message = message.replace("{id}", punishment.getId() + "");
        return message;
    }

    public static final String setJail = "<nc>You have set the jail location to your current position.";
    public static final String punishInvalidTarget = "<ec>The name/uuid you provided is invalid.";
    public static final String noPunishRank = "<ec>You cannot punish players that are equal to or higher than your rank.";
    public static final String punishNoReason = "<ec>You must supply a reason for the punishment.";
    public static final String punishmentCreateIssue = "<ec>There was an issue creating the punishment.";

    public static final String jailNotSet = "<ec>The jail location is not set. Please contact an Admin or higher.";

     public static String kickMessage(String punisher, String reason) {
        return "&a&lKICKED\n&fStaff: &c{punisher}\n&fReason: &c{reason}\n".replace("{punisher}", punisher).replace("{reason}", reason);
    }

    public static String warnMessage(String punisher, String reason, String code) {
        return "<ec>You have been warned by {punisher} for {reason}. You must acknowledge this warning before you can speak again.\nType the code <nc>{code} <ec>in chat.".replace("{punisher}", punisher).replace("{reason}", reason).replace("{code}", code);
    }

    public static final String notLookingAtSign = "<ec>You are not looking at a sign.";
    public static final String invalidLineNumber = "<ec>You provided an invalid number for the line to edit.";
    public static final String noMoreThan16Char = "<ec>Signs cannot have more than 16 characters per line.";

    public static String setLine(String line, String text) {
        return "<nc>Set the line &e{line} &bto &e{text}&b.".replace("{line}", line).replace("{text}", text);
    }

    public static String tpRequestExpire_Requester(String target) {
        return "<nc>Your teleport request to {target} <nc>has expired.".replace("{target}", target);
    }

    public static String tpRequestExpire_Target(String requester) {
        return "<nc>The teleport request from <vc>{requester} <nc>has expired.".replace("{requester}", requester);
    }

    public static String couldNotFindPlayer(String name) {
        return "<ec>Could not find the player " + name;
    }

    public static String tpTargetInvalid(String number) {
        return "<ec>The name for the {number} player is invalid.".replace("{number}", number);
    }

    public static final String noPermToTpHigherRank = "<ec>You are not allowed to forcefully teleport players equal to or higher than your rank.";
    public static final String back = "<nc>You teleported to your last location.";
    public static final String noBackLocation = "<ec>You do not have a location to go back to.";

    public static String tpAllNotTeleported(String actor) {
        return "{actor} <nc>issued a tpall but you were not teleported because of your rank.".replace("{actor}", actor);
    }

    public static String tpAllTeleported(String actor) {
        return "{actor} <nc>issued a tpall and you were teleported to them.".replace("{actor}", actor);
    }

    public static String tpRequestSend(String target) {
        return "<nc>Sent a teleport request to <vc>{target}&b.".replace("{target}", target);
    }

    public static String tpRequestReceive(String target) {
        return "<vc>{target} <nc>has sent you a teleport request.\nType &a/tpaccept <nc>or <ec>/tpdeny <nc>to respond.".replace("{target}", target);
    }

    public static final String couldNotFindRequest = "<ec>Could not find a request, did it expire?";
    public static final String requesterOffline = "<ec>The one who sent the request is no longer online.";

    public static String requestRespondReceiver(String action, String requester) {
        return "<nc>You {action} <vc>{requester}<nc>'s teleport request.".replace("{requester}", requester).replace("{action}", action);
    }

    public static String requestRespondSender(String action, String receiver) {
        return "{receiver} <nc>has {action} your teleport request.".replace("{receiver}", receiver).replace("{action}", action);
    }

    public static final String notVanished = "<ec>You are not currently vanished.";

    public static String duplicateOption(String option) {
        return "<ec>The option &e{option} <ec>has duplicates, ignoring them.".replace("{option}", option);
    }

    public static String optionToggle(String option, boolean value) {
        return "<nc>You have toggled <vc>{option}<nc> to <vc>{value}".replace("{option}", option).replace("{value}", value + "");
    }

    public static String cannotActionVanished(String action) {
        return "<ec>You cannot &e{action} <ec>while vanished.".replace("{action}", action);
    }

    public static String setMainRank(String target, Rank rank) {
        String prefix = (rank.equals(Rank.DEFAULT)) ? "&8Default" : rank.getPrefix();
        return "<nc>You have set {rc}{target}<nc>'s &emainrank <nc>to {rank}<nc>.".replace("{rc}", rank.getBaseColor()).replace("{target}", target).replace("{rank}", prefix);
    }

    public static final String socketRankUpdate = "<nc>Your rank has been updated. Please relog if you experience issues.";

    public static String weatherChange(String type, String world) {
        return "<nc>You have set the weather to <vc>{type} <nc>in &e{world}&b.".replace("{type}", type).replace("{world}", world);
    }

    public static String timeChange(String time, String world) {
        return "<nc>You have set the time to <vc>{time} <nc>in <vc>{world}<nc>.".replace("{time}", time).replace("{world}", world);
    }

    public static final String invalidTime = "<ec>The number you provided is an invalid time.";

    public static String broadcast(String message) {
        return "&d&l[Broadcast] &a&l{msg}".replace("{msg}", message.toUpperCase());
    }

    public static String socketBroadcast(String message) {
        return "&d&l[Socket Broadcast] &4&l{msg}".replace("{msg}", message.toUpperCase());
    }

    public static String setHome(String homeName) {
        return "<nc>You have set a home with the name <vc>{home} <nc>to your current location.".replace("{home}", homeName);
    }

    public static String delHome(String homeName) {
        return "<nc>You have deleted a home with the name <vc>{home}<nc>.".replace("{home}", homeName);
    }

    public static String homeTeleport(String homeName) {
        return "<nc>You have teleported to the home <vc>{home}<nc>.".replace("{home}", homeName);
    }

    public static final String homeNotExist = "<ec>You do not have a home with that name.";

    public static String setWarp(Warp warp) {
        return warp.getMinimumRank().equals(Rank.DEFAULT) ? "<nc>You have set a warp with the name <vc>{warp}<nc>.".replace("{warp}", warp.getName()) : "<nc>You have set a warp with the name <vc>{warp} <nc>and the minimum rank {rank}<nc>.".replace("{warp}", warp.getName()).replace("{rank}", warp.getMinimumRank().getPrefix());
    }

    public static String delWarp(String warp) {
        return "<nc>You have deleted a warp with the name <vc>{warp}<nc>.".replace("{warp}", warp);
    }

    public static String warpTeleport(String warp) {
        return "<nc>You have teleported to the warp <vc>{warp}<nc>.".replace("{warp}", warp);
    }

    public static final String invalidRank = "<ec>You provided an invalid rank.";

    public static final String warpDoesNotExist = "<ec>A warp by that name does not exist.";

    public static String reportBcFormat(FirecraftServer server, int id, String reporter, String target, String reason) {
        return "&4[REPORT] <ec><ID:{id}> &a({serverid}) &d{reporter} &freported &d{target} &ffor &d{reason}".replace("{serverid}", server.getName().toUpperCase())
                .replace("{reporter}", reporter).replace("{target}", target).replace("{reason}", reason).replace("{id}", id + "");
    }

    public static String formatReportChange(Report report, String changed) {
        String[] changedArr = changed.split(" ");
        if (changedArr.length == 2) {
            if (changedArr[0].equalsIgnoreCase("assigned")) {
                return "<nc>Your report against <vc>" + report.getTargetName() + " <nc>has been assigned to <vc>" + changedArr[1];
            }
        } else if (changedArr.length == 3) {
            String base = "<nc>The <vc>{so} <nc>of your report against <vc>{target} <nc>has been changed to {value}".replace("{target}", report.getTargetName());
            if (changedArr[0].equalsIgnoreCase("changed")) {
                if (changedArr[1].equalsIgnoreCase("status")) {
                    base = base.replace("{so}", "status");
                    Report.Status value = Report.Status.valueOf(changedArr[2]);
                    base = base.replace("{value}", value.getColor() + value.toString());
                } else if (changedArr[1].equalsIgnoreCase("outcome")) {
                    base = base.replace("{so}", "outcome");
                    Report.Outcome value = Report.Outcome.valueOf(changedArr[2]);
                    base = base.replace("{value}", value.getColor() + value.toString());
                }
                return base;
            }
        }
        return "";
    }

    public static final String recordingNoUse = "<ec>You cannot do that while you are in recording mode.";
    public static final String recordingNoMessage = "<ec>That player is recording, you cannot message them.";
    public static final String notOnline = "<ec>That player is not online.";
    public static String staffReportLogin(int notClosed, int notAssigned, int assignedNotClosed) {
        return "<nc>There are a total of <vc>" + notClosed + " <nc>reports that are not closed.\n" +
        "<nc>There are a total of <vc>" + notAssigned + " <nc>reports that are not assigned.\n" +
        "<nc>There are a total of <vc>" + assignedNotClosed + " <nc>reports that are assigned to you and not closed.";
    }
    public static final String mojangUUIDError = "<ec>There was an error getting the unique id of that player from Mojang";
    public static final String profileError = "<ec>There was an error getting the profile of that player.";
    public static String fct_setPrefix(String prefix) {
        return "<nc>You have set your prefix to " + prefix;
    }
    public static final String fct_resetPrefix = "<nc>You have reset your prefix";
    public static String listHeader(int amount) {
        return "<nc>There are a total of <vc>" + amount + " <nc>currently online.";
    }

    public static String ignoreAction(String action, String tf, String name) {
        return "<nc>You {action) <vc>{name} <nc>{tf} your ignored users list.".replace("{action}", action).replace("{tf}", tf).replace("{name}", name);
    }

    public static final String recordingModeOff = "<nc>You have turned off recording mode, all restrictions lifted.";

    public static String staffListHeader(int playerCount, int serverCount) {
        return "<nc>There are a total of <vc>" + playerCount + " <nc>staff on <vc>" + serverCount + " <nc>serverid(s)";
    }

    public static String reportInvalidValue(String type) {
        return "<ec>The " + type + " you provided is invalid.";
    }

    public static String reportTeleport(int id) {
        return "<nc>You teleported to the location of the report with the id <vc>" + id;
    }

    public static final String tpAllNoFCT = "<nc>You teleported all players except Firecraft Team members to you.";
    public static final String tpAll = "<nc>You teleported all players to you.";
    public static final String serverNotSet = "<ec>The server information is not set, please contact a member of The Firecraft Team.";
}