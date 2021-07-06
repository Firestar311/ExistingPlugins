package net.brutuspvp.core.managers;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.earth2me.essentials.User;
import com.firestar311.fireutils.classes.Utils;

import net.brutuspvp.core.BrutusCore;

@SuppressWarnings("deprecation")
public class PlayTimeManager implements CommandExecutor {
	private File file;
	private FileConfiguration config;

	public PlayTimeManager(BrutusCore plugin) {
		file = Utils.createYamlFile(plugin, "playtime");
		config = Utils.createYamlConfig(plugin, file, "players");

		new BukkitRunnable() {
			public void run() {
				for (String u : config.getConfigurationSection("players").getKeys(false)) {
					UUID uuid = UUID.fromString(u);
					Player player = Bukkit.getPlayer(uuid);
					if (player == null)
						continue;
					User user = plugin.getEssentials().getUser(player);
					if (user.isAfk())
						continue;
					long time = config.getLong("players." + uuid);
					config.set("players." + uuid, time + 5);
				}
				for (Player p : Bukkit.getOnlinePlayers()) {
					if (!config.contains("players." + p.getUniqueId())) {
						config.set("players." + p.getUniqueId(), 1);
					}
				}

				Utils.saveFile(plugin, file, config);
			}
		}.runTaskTimer(plugin, 0L, 5*20L);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 0) {
			if(sender instanceof Player) {
				Player player = (Player) sender;
				long totalPlaytimeSeconds = config.getLong("players." + player.getUniqueId());

				long days = TimeUnit.SECONDS.toDays(totalPlaytimeSeconds);
				long hours = TimeUnit.SECONDS.toHours(totalPlaytimeSeconds) % 24;
				long minutes = TimeUnit.SECONDS.toMinutes(totalPlaytimeSeconds) % 60;
				long seconds = totalPlaytimeSeconds % 60;

				String message = String.format("§bYour playtime is %dd %dh %dm %ds.", days, hours, minutes, seconds);

				sender.sendMessage(message);
			} else {
				sender.sendMessage("§cOnly players may check their own playtime, Try /playtime <player>");
			}
		} else if (args.length == 1) {
			OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
			
			if (offlinePlayer == null) {
				sender.sendMessage("§cThat player does not exist. Try again.");
				return true;
			}

			long totalPlaytimeSeconds = config.getLong("players." + offlinePlayer.getUniqueId());

			long days = TimeUnit.SECONDS.toDays(totalPlaytimeSeconds);
			long hours = TimeUnit.SECONDS.toHours(totalPlaytimeSeconds) % 24;
			long minutes = TimeUnit.SECONDS.toMinutes(totalPlaytimeSeconds) % 60;
			long seconds = totalPlaytimeSeconds % 60;

			String message = String.format("§b" + offlinePlayer.getName() + "'s playtime is %dd %dh %dm %ds.", days, hours, minutes, seconds);

			sender.sendMessage(message);
		} else {
			sender.sendMessage("§cUsage: /playtime <name>");
		}
		return true;
	}
}