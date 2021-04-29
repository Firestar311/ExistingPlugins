package net.firecraftmc.hub;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import net.firecraftmc.maniacore.api.CenturionsCore;
import net.firecraftmc.maniacore.api.ranks.Rank;
import net.firecraftmc.maniacore.api.server.CenturionsServer;
import net.firecraftmc.maniacore.api.stats.Stats;
import net.firecraftmc.maniacore.api.util.CenturionsUtils;
import net.firecraftmc.maniacore.spigot.user.PlayerBoard;
import net.firecraftmc.maniacore.spigot.user.SpigotUser;
import org.bukkit.ChatColor;

public class HubBoard extends PlayerBoard {
    private final int hubLine;
    private final int inGameLine;
    private final int winsLine;
    private final int coinsLine;
    private final SpigotUser user;
    private final int rankLine;
    private final int killsLine;
    private CenturionsCore centurionsCore;
    
    public HubBoard(SpigotUser spigotUser) {
        super(CenturionsUtils.color("&6&lCENTURIONS BG"));
        this.user = spigotUser;
        addLine("", ChatColor.GOLD + "" + ChatColor.BOLD + "RANK", "");
        centurionsCore = CenturionsCore.getInstance();
        this.rankLine = addLine(ChatColor.WHITE + "", ChatColor.DARK_PURPLE.toString(), CenturionsUtils.color(spigotUser.getRank().getBaseColor() + "&l" + spigotUser.getRank().getName().toUpperCase()));
        addLine("", ChatColor.LIGHT_PURPLE.toString(), "");
        addLine("", ChatColor.GOLD + "" + ChatColor.BOLD + "ONLINE", "");
        int hubOnline = 0, gameOnline = 0;
        for (ServerObject server : TimoCloudAPI.getUniversalAPI().getServerGroup("Hub").getServers()) {
            hubOnline += server.getOnlinePlayerCount();
        }
        for (ServerObject server : TimoCloudAPI.getUniversalAPI().getServerGroup("HG").getServers()) {
            gameOnline += server.getOnlinePlayerCount();
        }
        this.hubLine = addLine("", ChatColor.WHITE + "Hub: " + ChatColor.GREEN, "" + hubOnline);
        this.inGameLine = addLine("", ChatColor.WHITE + "In Game: " + ChatColor.GREEN, "" + gameOnline);
        addLine("", ChatColor.YELLOW.toString(), "");
        addLine("", ChatColor.GOLD + "" + ChatColor.BOLD + "STATS", "");
        int totalWins = user.getStat(Stats.HG_WINS).getAsInt();
        int totalKills = user.getStat(Stats.HG_KILLS).getAsInt();
        this.winsLine = addLine("", ChatColor.WHITE + "Wins: " + ChatColor.YELLOW, "" + totalWins);
        this.killsLine = addLine("", ChatColor.WHITE + "Kills: " + ChatColor.YELLOW, "" + totalKills);
        this.coinsLine = addLine("", ChatColor.WHITE + "Coins: " + ChatColor.YELLOW, "" + spigotUser.getStat(Stats.COINS).getAsInt());
        addLine("", ChatColor.GRAY.toString(), "");
        addLine("", ChatColor.GOLD + "" + ChatColor.BOLD + "SERVER", "");
        CenturionsServer currentServer = centurionsCore.getServerManager().getCurrentServer();
        String serverName;
        if (currentServer != null) {
            serverName = currentServer.getName();
        } else {
            serverName = "";
        }
        addLine("", ChatColor.WHITE + "", serverName);
        addLine(ChatColor.YELLOW + "play.", "centurionsbg.net", "");
        spigotUser.setScoreboard(this);
        send(spigotUser.getBukkitPlayer());
    }
    
    public void update() {
        Rank rank = user.getRank();
        setLine(rankLine, CenturionsUtils.color(rank.getBaseColor() + "&l" + rank.getName().toUpperCase()));
        int hubOnline = 0, gameOnline = 0;
        for (ServerObject server : TimoCloudAPI.getUniversalAPI().getServerGroup("Hub").getServers()) {
            hubOnline += server.getOnlinePlayerCount();
        }
        for (ServerObject server : TimoCloudAPI.getUniversalAPI().getServerGroup("HG").getServers()) {
            gameOnline += server.getOnlinePlayerCount();
        }
        setLine(hubLine, hubOnline + "");
        setLine(inGameLine, gameOnline + "");
        int totalWins = user.getStat(Stats.HG_WINS).getAsInt();
        int totalKills = user.getStat(Stats.HG_KILLS).getAsInt();
        setLine(winsLine, totalWins + "");
        setLine(killsLine, totalKills + "");
        setLine(coinsLine, user.getStat(Stats.COINS).getAsInt() + "");
    }
}
