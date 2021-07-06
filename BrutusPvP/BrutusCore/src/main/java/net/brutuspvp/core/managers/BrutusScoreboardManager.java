package net.brutuspvp.core.managers;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import net.brutuspvp.core.BrutusCore;
import net.brutuspvp.core.model.BrutusUser;

public class BrutusScoreboardManager implements Listener {

	private HashMap<UUID, Scoreboard> boards = new HashMap<UUID, Scoreboard>();
	private BrutusCore plugin;

	public BrutusScoreboardManager(BrutusCore passedPlugin) {
		plugin = passedPlugin;
		plugin.registerListener(this);

//		new BukkitRunnable() {
//			public void run() {
//				for (Player player : Bukkit.getOnlinePlayers()) {
//					updateScoreboard(player);
//				}
//			}
//		}.runTaskTimer(plugin, 20L, 2);
	}

	public void updateScoreboard(Player player) {
		try {
			BrutusUser user = plugin.players().getBrutusUser(player);
			if (boards.get(player.getUniqueId()) != null) {
				int counter = 0;
				for (Player p : Bukkit.getOnlinePlayers()) {
					if (!plugin.getDevVanish().contains(p.getUniqueId())) {
						counter++;
					}
				}
				boards.get(player.getUniqueId()).getTeam("pcount").setPrefix("§c" + counter);
				boards.get(player.getUniqueId()).getTeam("pcount").setSuffix("§c" + Bukkit.getServer().getMaxPlayers());
				boards.get(player.getUniqueId()).getTeam("balance").setSuffix(0 + "");
				//boards.get(player.getUniqueId()).getTeam("balance").setSuffix(plugin.getVaultEconomy().getBalance(player) + "");
				String channel = user.getChannel().getColorBold() + user.getChannel().toString();
				boards.get(player.getUniqueId()).getTeam("channel").setSuffix(channel);
			} else {
				String pname = player.getName();
				Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
				Objective obj = board.registerNewObjective("main", "dummy");
				int line = 15;
				obj.setDisplaySlot(DisplaySlot.SIDEBAR);
				obj.setDisplayName("§4BrutusPvP Rome");

				Team you = board.registerNewTeam("you");
				you.addEntry("YOU");
				you.setPrefix("§b§l");

				Team name = board.registerNewTeam("name");
				name.addEntry(pname);

				Team space1 = board.registerNewTeam("space1");
				space1.addEntry(" ");

				Team players = board.registerNewTeam("players");
				players.addEntry("PLAYERS");
				players.setPrefix("§a§l");

				Team pcount = board.registerNewTeam("pcount");
				pcount.addEntry("§e/");
				pcount.setPrefix("§c" + Bukkit.getServer().getOnlinePlayers().size());
				pcount.setSuffix("§c" + Bukkit.getServer().getMaxPlayers());

				Team space2 = board.registerNewTeam("space2");
				space2.addEntry("  ");

				Team channel = board.registerNewTeam("channel");
				channel.addEntry("Channel: ");
				channel.setPrefix("§7");
				String userchannel = user.getChannel().getColorBold() + user.getChannel().toString();
				if(userchannel != null) {
					channel.setSuffix(userchannel);
				}

				Team balance = board.registerNewTeam("balance");
				balance.addEntry("Denari: §e");
				balance.setPrefix("§6");
				balance.setSuffix(plugin.getVaultEconomy().getBalance(player) + "");

				obj.getScore("YOU").setScore(line--);
				obj.getScore(player.getName()).setScore(line--);
				obj.getScore(" ").setScore(line--);
				obj.getScore("PLAYERS").setScore(line--);
				obj.getScore("§e/").setScore(line--);
				obj.getScore("  ").setScore(line--);
				obj.getScore("Channel: ").setScore(line--);
				obj.getScore("Denari: §e").setScore(line--);
				player.setScoreboard(board);
				boards.put(player.getUniqueId(), board);
			}
		} catch (Exception e) {
			BrutusCore.createBrutusError(e, "Update Scoreboards");
		}
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e) {
		boards.remove(e.getPlayer().getUniqueId());
	}
}