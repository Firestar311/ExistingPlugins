package net.firecraftmc.api.model.player;

import net.firecraftmc.api.FirecraftAPI;
import net.firecraftmc.api.enums.Channel;
import net.firecraftmc.api.enums.Rank;
import net.firecraftmc.api.model.server.FirecraftServer;
import net.firecraftmc.api.punishments.Punishment;
import net.firecraftmc.api.punishments.Punishment.Colors;
import net.firecraftmc.api.punishments.Punishment.Type;
import net.firecraftmc.api.toggles.Toggle;
import net.firecraftmc.api.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * The class that represents a scoreboard for FirecraftAPI
 * This stores all the teams to allow more control and a non-flicker scoreboard
 */
public class FirecraftScoreboard {
    private static final ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

    private final Scoreboard board;
    private final Objective objective;

    private final Map<SBField, Team> fields = new HashMap<>();

    public enum SBField {
        NAME, RANK, CHANNEL, NICKNAME, DATE, PLAYER_COUNT, BLANK, PLAYER_HEADER, VANISH, STAFFMODE
    }

    public FirecraftScoreboard(FirecraftPlayer player, FirecraftServer server) {
        this.board = scoreboardManager.getNewScoreboard();
        objective = board.registerNewObjective("firecraftmc", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName("§6FirecraftMC - " + server.getColor() + server.getName());

        addTeam(SBField.DATE, "date", ChatColor.AQUA, "§7" + dateFormat.format(new Date()), "", 15);
        addTeam(SBField.BLANK, "blank1", ChatColor.WHITE, "", "", 14);
        addTeam(SBField.NAME, "name", ChatColor.GREEN, "§7Name: ", player.getName(), 13);
        if (!player.getToggleValue(Toggle.RECORDING)) {
            if (player.getMainRank().equals(Rank.DEFAULT)) {
                addTeam(SBField.RANK, "rank", "§4§l", "§7Rank: ", "§8Default", 12);
            } else if (player.getMainRank().equals(Rank.FIRECRAFT_TEAM)) {
                addTeam(SBField.RANK, "rank", "§4§l", "§7Rank: ", "FIRECRAFT TEAM", 12);
            } else {
                addTeam(SBField.RANK, "rank", "§4§l", "§7Rank: ", player.getMainRank().getPrefix(), 12);
            }
        } else {
            addTeam(SBField.RANK, "rank", ChatColor.BLUE, "§7Rank: ", "§8Default", 12);
        }
        Channel channel = player.getChannel();
        addTeam(SBField.CHANNEL, "channel", ChatColor.BOLD, "§7Channel: ", channel.getColor() + channel.toString(), 11);
        if (player.hasRank(Rank.FAMOUS, Rank.VIP) || player.getMainRank().isEqualToOrHigher(Rank.TRIAL_ADMIN)) {
            if (player.getNick() != null) {
                addTeam(SBField.NICKNAME, "nickname", ChatColor.DARK_AQUA, "§7Nickname: ", player.getNick().getProfile().getName(), 10);
            } else {
                addTeam(SBField.NICKNAME, "nickname", ChatColor.DARK_AQUA, "§7Nickname: ", "§cNONE", 10);
            }
        }
        if (player.getMainRank().equals(Rank.VIP) || player.getMainRank().isEqualToOrHigher(Rank.MOD)) {
            if (player.isVanished()) {
                addTeam(SBField.VANISH, "vanish", ChatColor.DARK_RED, "§7Vanished: ", "§aTrue", 9);
            } else {
                addTeam(SBField.VANISH, "vanish", ChatColor.DARK_RED, "§7Vanished: ", "§cFalse", 9);
            }
        }
        
        if (Rank.isStaff(player.getMainRank())) {
            if (FirecraftAPI.getFirecraftCore().getStaffmodeManager().inStaffMode(player)) {
                addTeam(SBField.STAFFMODE, "staffmode", ChatColor.DARK_BLUE, "§7Staff Mode: ", "§aTrue", 8);
            } else {
                addTeam(SBField.STAFFMODE, "staffmode", ChatColor.DARK_BLUE, "§7Staff Mode: ", "§cFalse", 8);
            }
        }

        addTeam(SBField.BLANK, "blank2", ChatColor.GRAY, "", "", 7);
        addTeam(SBField.PLAYER_HEADER, "playerheader", ChatColor.DARK_GREEN, "§aOnline Players", "", 1);
        int online = 0;
        for (FirecraftPlayer p : FirecraftAPI.getFirecraftCore().getPlayerManager().getPlayers()) {
            if (player.getMainRank().isEqualToOrHigher(p.getMainRank())) {
                online++;
            }
        }
        int max = Bukkit.getServer().getMaxPlayers();
        addTeam(SBField.PLAYER_COUNT, "playercount", ChatColor.DARK_GRAY, "§2" + online, "§7/§9" + max, 0);
        updateScoreboard(player);
    }

    private void addTeam(SBField field, String name, ChatColor c, String prefix, String suffix, int score) {
        //TODO Add support for detecting the length of the text (Prefix/Suffix/Main) and splitting them up if over a certain amount
        Team team = board.registerNewTeam(name);
        team.addEntry(c.toString());
        team.setPrefix(prefix);
        team.setSuffix(suffix);
        objective.getScore(c.toString()).setScore(score);
        this.fields.put(field, team);
    }

    public void addTeam(SBField field, String name, String c, String prefix, String suffix, int score) {
        //TODO Add support for detecting the length of the text (Prefix/Suffix/Main) and splitting them up if over a certain amount
        Team team = board.registerNewTeam(name);
        team.addEntry(c);
        team.setPrefix(prefix);
        team.setSuffix(suffix);
        objective.getScore(c).setScore(score);
        this.fields.put(field, team);
    }

    public void updateScoreboard(FirecraftPlayer player) {
        Team rankTeam = fields.get(SBField.RANK);
        if (!player.getToggleValue(Toggle.RECORDING)) {
            if (player.getMainRank().equals(Rank.DEFAULT)) {
                rankTeam.setSuffix("§8Default");
            } else if (player.getMainRank().equals(Rank.FIRECRAFT_TEAM)) {
                rankTeam.setSuffix("FIRECRAFT TEAM");
            } else {
                rankTeam.setSuffix(player.getMainRank().getPrefix());
            }
        } else {
            rankTeam.setSuffix("§8Default");
        }
        Channel channel = player.getChannel();
        Team channelTeam = fields.get(SBField.CHANNEL);
        List<Punishment> punishments = FirecraftAPI.getDatabase().getPunishments(player.getUniqueId());
        boolean muted = false, jailed = false;
        if (punishments != null || !punishments.isEmpty()) {
            for (Punishment punishment : punishments) {
                if (punishment.isActive()) {
                    if (punishment.getType().equals(Type.MUTE) || punishment.getType().equals(Type.TEMP_MUTE)) {
                        muted = true;
                    } else if (punishment.getType().equals(Type.JAIL)) {
                        jailed = true;
                    }
                }
            }
        }
        
        String ch = "";
        if (muted || jailed) {
            if (muted && jailed) {
                ch = Colors.MUTE + "M§f, " + Colors.JAIL + "J";
            } else if (muted && !jailed) {
                ch = Colors.MUTE + "MUTED";
            } else if (jailed && !muted) {
                ch = Colors.JAIL + "JAILED";
            }
        } else {
            ch = channel.getColor() + channel.toString();
        }
        ch = Utils.color(ch);
        
        channelTeam.setSuffix(ch);
        Team nickTeam = fields.get(SBField.NICKNAME);
        if (nickTeam != null) {
            if (!player.getToggleValue(Toggle.RECORDING)) {
                if (player.hasRank(Rank.VIP, Rank.FAMOUS) || player.getMainRank().isEqualToOrHigher(Rank.TRIAL_ADMIN)) {
                    if (player.getNick() != null) {
                        nickTeam.setSuffix(player.getNick().getProfile().getName());
                    } else {
                        nickTeam.setSuffix("§cNONE");
                    }
                } else {
                    board.resetScores(ChatColor.DARK_AQUA.toString());
                    nickTeam.unregister();
                    fields.remove(SBField.NICKNAME);
                }
            } else {
                board.resetScores(ChatColor.DARK_AQUA.toString());
                nickTeam.unregister();
                fields.remove(SBField.NICKNAME);
            }
        } else {
            if (!player.getToggleValue(Toggle.RECORDING)) {
                if (player.hasRank(Rank.VIP, Rank.FAMOUS) || player.getMainRank().isEqualToOrHigher(Rank.TRIAL_ADMIN)) {
                    if (player.getNick() != null) {
                        addTeam(SBField.NICKNAME, "nickname", ChatColor.DARK_AQUA, "§7Nickname: ", player.getNick().getProfile().getName(), 10);
                    } else {
                        addTeam(SBField.NICKNAME, "nickname", ChatColor.DARK_AQUA, "§7Nickname: ", "§cNONE", 10);
                    }
                }
            }
        }

        Team vanishedTeam = fields.get(SBField.VANISH);
        if (vanishedTeam != null) {
            if (!player.getToggleValue(Toggle.RECORDING)) {
                if (player.getMainRank().equals(Rank.VIP) || player.getMainRank().isEqualToOrHigher(Rank.MOD)) {
                    if (player.isVanished()) {
                        vanishedTeam.setSuffix("§aTrue");
                    } else {
                        vanishedTeam.setSuffix("§cFalse");
                    }
                } else {
                    board.resetScores(ChatColor.DARK_RED.toString());
                    vanishedTeam.unregister();
                    fields.remove(SBField.VANISH);
                }
            } else {
                board.resetScores(ChatColor.DARK_RED.toString());
                vanishedTeam.unregister();
                fields.remove(SBField.VANISH);
            }
        } else {
            if (!player.getToggleValue(Toggle.RECORDING)) {
                if (player.getMainRank().equals(Rank.VIP) || player.getMainRank().isEqualToOrHigher(Rank.MOD)) {
                    if (player.isVanished()) {
                        addTeam(SBField.VANISH, "vanish", ChatColor.DARK_RED, "§7Vanished: ", "§aTrue", 9);
                    } else {
                        addTeam(SBField.VANISH, "vanish", ChatColor.DARK_RED, "§7Vanished: ", "§cFalse", 9);
                    }
                }
            }
        }
    
        Team staffmodeTeam = fields.get(SBField.STAFFMODE);
        if (staffmodeTeam != null) {
            if (!player.getToggleValue(Toggle.RECORDING)) {
                if (Rank.isStaff(player.getMainRank())) {
                    if (FirecraftAPI.getFirecraftCore().getStaffmodeManager().inStaffMode(player)) {
                        staffmodeTeam.setSuffix("§aTrue");
                    } else {
                        staffmodeTeam.setSuffix("§cFalse");
                    }
                } else {
                    board.resetScores(ChatColor.DARK_BLUE.toString());
                    staffmodeTeam.unregister();
                    fields.remove(SBField.STAFFMODE);
                }
            } else {
                board.resetScores(ChatColor.DARK_BLUE.toString());
                staffmodeTeam.unregister();
                fields.remove(SBField.STAFFMODE);
            }
        } else {
            if (!player.getToggleValue(Toggle.RECORDING)) {
                if (Rank.isStaff(player.getMainRank())) {
                    if (FirecraftAPI.getFirecraftCore().getStaffmodeManager().inStaffMode(player)) {
                        addTeam(SBField.STAFFMODE, "staffmode", ChatColor.DARK_BLUE, "§7Staff Mode: ", "§aTrue", 8);
                    } else {
                        addTeam(SBField.STAFFMODE, "staffmode", ChatColor.DARK_BLUE, "§7Staff Mode: ", "§cFalse", 8);
                    }
                }
            }
        }

        Team playerCountTeam = fields.get(SBField.PLAYER_COUNT);
        int online = 0;
        for (FirecraftPlayer p : FirecraftAPI.getFirecraftCore().getPlayerManager().getPlayers()) {
            if (player.getMainRank().isEqualToOrHigher(p.getMainRank())) {
                online++;
            }
        }
        playerCountTeam.setPrefix("§2" + online);
    }

    public void sendScoreboard(Player player) {
        if (player != null) {
            player.setScoreboard(board);
        }
    }
}
