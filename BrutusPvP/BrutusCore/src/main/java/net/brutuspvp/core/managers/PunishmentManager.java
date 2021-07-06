package net.brutuspvp.core.managers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.firestar311.fireutils.classes.Utils;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;

import net.brutuspvp.core.BrutusCore;
import net.brutuspvp.core.Perms;
import net.brutuspvp.core.enums.Channel;
import net.brutuspvp.core.enums.Type;
import net.brutuspvp.core.model.Ban;
import net.brutuspvp.core.model.BrutusUser;
import net.brutuspvp.core.model.IPBan;
import net.brutuspvp.core.model.Jail;
import net.brutuspvp.core.model.JailedUser;
import net.brutuspvp.core.model.Mute;
import net.brutuspvp.core.model.Tempban;
import net.brutuspvp.core.model.Tempmute;
import net.brutuspvp.core.model.abstraction.Punishment;

@SuppressWarnings({ "unused", "deprecation" })
public class PunishmentManager implements Listener, CommandExecutor {
	
	private File file;
	private FileConfiguration config;
	private File logFile;
	private BrutusCore plugin;

	private HashMap<UUID, Ban> bans = new HashMap<UUID, Ban>();
	private HashMap<UUID, IPBan> ipbans = new HashMap<UUID, IPBan>();
	private HashMap<UUID, Mute> mutes = new HashMap<UUID, Mute>();
	protected HashMap<UUID, Tempban> tempbans = new HashMap<UUID, Tempban>();
	protected HashMap<UUID, Tempmute> tempmutes = new HashMap<UUID, Tempmute>();
	private HashMap<String, Jail> jails = new HashMap<String, Jail>();
	private TreeMap<Integer, JailedUser> jailed = new TreeMap<Integer, JailedUser>();

	public PunishmentManager(BrutusCore passedPlugin) {
		file = Utils.createYamlFile(passedPlugin, "punishments");
		config = Utils.createYamlConfig(passedPlugin, file, "bans", "tempbans", "mutes", "tempmutes", "jails",
				"jailed");
		logFile = Utils.createYamlFile(passedPlugin, "logs");
		plugin = passedPlugin;
		this.loadPunishments();
		plugin.registerListener(this);

		new PunishmentCheckExpire().runTaskTimer(plugin, 20, 20L);
	}

	public void savePunishments() {
		config.set("bans", null);
		config.set("tempbans", null);
		config.set("mutes", null);
		config.set("tempmutes", null);
		config.set("jails", null);
		config.set("jailed", null);
		Utils.saveFile(file, config);
		config = Utils.createYamlConfig(plugin, file, "bans", "tempbans", "mutes", "tempmutes", "jails", "jailed");
		for (Entry<UUID, Ban> entry : bans.entrySet()) {
			Ban ban = entry.getValue();
			config.set("bans." + ban.getPlayer().toString() + ".actor", ban.getActor().toString());
			config.set("bans." + ban.getPlayer().toString() + ".reason", ban.getReason());
		}

		for (Entry<UUID, Mute> entry : mutes.entrySet()) {
			Mute mute = entry.getValue();
			config.set("mutes." + mute.getPlayer().toString() + ".actor", mute.getActor().toString());
			config.set("mutes." + mute.getPlayer().toString() + ".reason", mute.getReason());
		}

		for (Entry<Integer, JailedUser> entry : jailed.entrySet()) {
			JailedUser user = entry.getValue();
			config.set("jailed." + entry.getKey() + ".player", user.getPlayer().toString());
			config.set("jailed." + entry.getKey() + ".actor", user.getActor().toString());
			config.set("jailed." + entry.getKey() + ".reason", user.getReason());
			config.set("jailed." + entry.getKey() + ".jail", user.getJail().getJailName());
		}

		for (Entry<UUID, Tempban> entry : tempbans.entrySet()) {
			Tempban ban = entry.getValue();
			config.set("tempbans." + ban.getPlayer().toString() + ".actor", ban.getActor().toString());
			config.set("tempbans." + ban.getPlayer().toString() + ".reason", ban.getReason());
			config.set("tempbans." + ban.getPlayer().toString() + ".expire", ban.getExpire());
		}

		for (Entry<UUID, Tempmute> entry : tempmutes.entrySet()) {
			Tempmute mute = entry.getValue();
			config.set("tempmutes." + mute.getPlayer().toString() + ".actor", mute.getActor().toString());
			config.set("tempmutes." + mute.getPlayer().toString() + ".reason", mute.getReason());
			config.set("tempmutes." + mute.getPlayer().toString() + ".expire", mute.getExpire());
		}

		for (Entry<String, Jail> entry : jails.entrySet()) {
			Jail jail = entry.getValue();
			Utils.saveLocation(file, config, jail.getTeleportPoint(), "jails." + jail.getJailName() + ".tppoint");
			ArrayList<String> players = new ArrayList<String>();
			for (UUID uuid : jail.getPlayers()) {
				players.add(uuid.toString());
			}
			config.set("jails." + jail.getJailName() + ".players", players);
		}
		Utils.saveFile(file, config);
	}

	private void loadPunishments() {
		for (String u : config.getConfigurationSection("bans").getKeys(false)) {
			UUID player = UUID.fromString(u);
			UUID actor = UUID.fromString(config.getString("bans." + u + ".actor"));
			String reason = config.getString("bans." + u + ".reason");
			Ban ban = new Ban(player, actor, reason, Type.BAN);
			bans.put(player, ban);
		}

		for (String u : config.getConfigurationSection("mutes").getKeys(false)) {
			UUID player = UUID.fromString(u);
			UUID actor = UUID.fromString(config.getString("mutes." + u + ".actor"));
			String reason = config.getString("mutes." + u + ".reason");
			Mute mute = new Mute(player, actor, reason, Type.MUTE);
			mutes.put(player, mute);
		}

		for (String j : config.getConfigurationSection("jails").getKeys(false)) {
			Location tppoint = Utils.getLocation(config, "jails." + j + ".tppoint");
			List<String> ps = config.getStringList("jails." + j + ".players");

			ProtectedCuboidRegion region = (ProtectedCuboidRegion) plugin.getWorldGuard()
					.getRegionManager(tppoint.getWorld()).getRegion(j);
			if (region == null) {
				continue;
			}

			Jail jail = new Jail(j, region);
			for (String u : ps) {
				jail.addPlayer(UUID.fromString(u));
			}
			jail.setTeleportPoint(tppoint);
			jails.put(j, jail);
		}

		for (String i : config.getConfigurationSection("jailed").getKeys(false)) {
			UUID player = UUID.fromString(config.getString("jailed." + i + ".player"));
			UUID actor = UUID.fromString(config.getString("jailed." + i + ".actor"));
			String reason = config.getString("jailed." + i + ".reason");
			String jailName = config.getString("jailed." + i + ".jail");
			Jail jail = jails.get(jailName);
			if (jail == null) {
				continue;
			}

			jailed.put(jailed.size() + 1, new JailedUser(player, actor, reason, jail));
		}

		for (String u : config.getConfigurationSection("tempbans").getKeys(false)) {
			UUID player = UUID.fromString(u);
			UUID actor = UUID.fromString(config.getString("tempbans." + u + ".actor"));
			String reason = config.getString("tempbans." + u + ".reason");
			long expire = config.getLong("tempbans." + u + ".expire");
			Tempban ban = new Tempban(player, actor, reason, Type.TEMPBAN, expire);
			tempbans.put(player, ban);
		}

		for (String u : config.getConfigurationSection("tempmutes").getKeys(false)) {
			UUID player = UUID.fromString(u);
			UUID actor = UUID.fromString(config.getString("tempmutes." + u + ".actor"));
			String reason = config.getString("tempmutes." + u + ".reason");
			long expire = config.getLong("tempmutes." + u + ".expire");
			Tempmute mute = new Tempmute(player, actor, reason, Type.TEMPMUTE, expire);
			tempmutes.put(player, mute);
		}
	}

	public void banPlayer(CommandSender rawActor, UUID player, String reason) {
		UUID actor = null;
		if (rawActor instanceof ConsoleCommandSender) {

		} else if (rawActor instanceof Player) {
			actor = ((Player) rawActor).getUniqueId();
		}
		Ban ban = new Ban(player, actor, reason, Type.BAN);
		bans.put(player, ban);
		if (Bukkit.getServer().getOfflinePlayer(player).isOnline()) {
			// TODO Format the kick message better
			Bukkit.getServer().getPlayer(player).kickPlayer("§cYou have been banned.");
		}
		broadcastPunishMessage(rawActor, player, "banned", reason);
	}

	public void banPlayer(CommandSender rawActor, UUID player, long expire, String reason) {
		UUID actor = null;
		if (rawActor instanceof ConsoleCommandSender) {

		} else if (rawActor instanceof Player) {
			actor = ((Player) rawActor).getUniqueId();
		}
		Tempban ban = new Tempban(player, actor, reason, Type.TEMPBAN, expire);
		tempbans.put(player, ban);
		if (Bukkit.getServer().getOfflinePlayer(player).isOnline()) {
			// TODO Format the kick message better
			Bukkit.getServer().getPlayer(player).kickPlayer("§cYou have been banned.");
		}
		broadcastPunishMessage(rawActor, player, "tempbanned", reason);
	}

	public void unbanPlayer(CommandSender rawActor, UUID player) {
		UUID actor = null;
		if (rawActor instanceof ConsoleCommandSender) {

		} else if (rawActor instanceof Player) {
			actor = ((Player) rawActor).getUniqueId();
		}

		if (bans.containsKey(player) || tempbans.containsKey(player)) {
			if (bans.containsKey(player)) {
				bans.remove(player);
			} else {
				tempbans.remove(player);
			}
			broadcastUnpunishMessage(rawActor, player, "unban");
		} else {
			rawActor.sendMessage("§cThat player is not banned.");
		}
	}

	public String getBanReason(UUID player) {
		if (bans.containsKey(player)) {
			return bans.get(player).getReason();
		} else if (tempbans.containsKey(player)) {
			return tempbans.get(player).getReason();
		}
		return "Not banned";
	}

	public boolean isBanned(UUID player) {
		if (bans.containsKey(player)) {
			return true;
		} else if (tempbans.containsKey(player)) {
			return true;
		}
		return false;
	}
	
	public void banPlayer(CommandSender rawActor, String ip, String reason) {
		UUID actor = null;
		if (rawActor instanceof ConsoleCommandSender) {

		} else if (rawActor instanceof Player) {
			actor = ((Player) rawActor).getUniqueId();
		}
		
	}

	public void mutePlayer(CommandSender rawActor, UUID player, String reason) {
		UUID actor = null;
		if (rawActor instanceof ConsoleCommandSender) {

		} else if (rawActor instanceof Player) {
			actor = ((Player) rawActor).getUniqueId();
		}
		Mute mute = new Mute(player, actor, reason, Type.MUTE);
		mutes.put(player, mute);
		broadcastPunishMessage(rawActor, player, "muted", reason);
	}

	public void mutePlayer(CommandSender rawActor, UUID player, long expire, String reason) {
		UUID actor = null;
		if (rawActor instanceof ConsoleCommandSender) {

		} else if (rawActor instanceof Player) {
			actor = ((Player) rawActor).getUniqueId();
		}
		Tempmute tempmute = new Tempmute(player, actor, reason, Type.TEMPMUTE, expire);
		tempmutes.put(player, tempmute);
		broadcastPunishMessage(rawActor, player, "tempmuted", reason);
	}

	public void unmutePlayer(CommandSender rawActor, UUID player) {
		UUID actor = null;
		if (rawActor instanceof ConsoleCommandSender) {

		} else if (rawActor instanceof Player) {
			actor = ((Player) rawActor).getUniqueId();
		}

		if (mutes.containsKey(player) || tempmutes.containsKey(player)) {
			if (mutes.containsKey(player)) {
				mutes.remove(player);
			} else {
				tempmutes.remove(player);
			}
			broadcastUnpunishMessage(rawActor, player, "unmuted");
		} else {
			rawActor.sendMessage("§cThat player is not muted.");
		}
	}

	public String getMuteReason(UUID player) {
		if (mutes.containsKey(player)) {
			return mutes.get(player).getReason();
		} else if (tempmutes.containsKey(player)) {
			return tempmutes.get(player).getReason();
		}
		return "Not muted";
	}

	public boolean isMuted(UUID player) {
		if (mutes.containsKey(player)) {
			return true;
		} else if (tempmutes.containsKey(player)) {
			return true;
		}
		return false;
	}

	public void kickPlayer(CommandSender actor, UUID player, String reason) {
		if (Bukkit.getServer().getOfflinePlayer(player) != null) {
			if (Bukkit.getServer().getOfflinePlayer(player).isOnline()) {
				// TODO Format the kick message
				Bukkit.getServer().getPlayer(player).kickPlayer("§cYou have been kicked.");
				broadcastPunishMessage(actor, player, "kicked", reason);
			} else {
				actor.sendMessage("§cThat player is not online");
			}
		} else {
			actor.sendMessage("§cThat player does not exist.");
		}
	}

	public void jailPlayer(CommandSender actor, UUID player, String reason, String jailName) {
		Jail jail = jails.get(jailName);
		Player target = Bukkit.getPlayer(player);
		BrutusUser user = plugin.players().getBrutusUser(target);
		if (jail == null) {
			actor.sendMessage("§cThat jail does not exist.");
			return;
		}

		if (actor instanceof Player) {
			Player a = (Player) actor;
			jailed.put(jailed.size() + 1, new JailedUser(player, a.getUniqueId(), reason, jail));
			user.setChannel(Channel.JAIL);
			jail.addPlayer(player);
			target.teleport(jail.getTeleportPoint());
			jails.put(jail.getJailName(), jail);
			broadcastPunishMessage(actor, player, "jailed", reason);
		} else {
			actor.sendMessage("§cNot implemented.");
		}
	}

	public void unjailPlayer(CommandSender actor, UUID player) {
		Integer i = 0;

		for (Entry<Integer, JailedUser> entry : jailed.entrySet()) {
			JailedUser ju = entry.getValue();
			if (ju.getPlayer().equals(player)) {
				i = entry.getKey();
				break;
			}
		}

		for (Jail jail : jails.values()) {
			if (jail.getPlayers().contains(player)) {
				jail.removePlayer(player);
				Player p = Bukkit.getPlayer(player);
				if (p != null) {
					p.teleport(p.getWorld().getSpawnLocation());
					BrutusUser j = plugin.players().getBrutusUser(player);
					j.setChannel(Channel.GLOBAL);
				}
				continue;
			}
		}
		jailed.remove(i);
		
		TreeMap<Integer, JailedUser> newJailed = new TreeMap<Integer, JailedUser>();
		int value = 0;
		for(Entry<Integer, JailedUser> entry : jailed.entrySet()) {
			newJailed.put(value++, entry.getValue());
		}
		
		jailed.clear();
		
		for(Entry<Integer, JailedUser> entry : newJailed.entrySet()) {
			jailed.put(entry.getKey(), entry.getValue());
		}
		
		broadcastUnpunishMessage(actor, player, "unjailed");
	}

	public String getJailReason(UUID player) {
		for (Entry<Integer, JailedUser> entry : jailed.entrySet()) {
			JailedUser ju = entry.getValue();
			if (ju.getPlayer().equals(player)) {
				return ju.getReason();
			}
		}
		return "Not Jailed";
	}

	public boolean isJailed(UUID player) {
		for (Entry<Integer, JailedUser> entry : jailed.entrySet()) {
			JailedUser ju = entry.getValue();
			if (ju.getPlayer().equals(player)) {
				return true;
			}
		}
		return false;
	}
	
	public JailedUser getNextJailedUser() {
		return jailed.higherEntry(2).getValue();
	}
	
	public JailedUser getJailedUser(UUID player) {
		
		for (JailedUser jailedUser : jailed.values()) {
			if (jailedUser.getPlayer().equals(player)) {
				return jailedUser;
			}
		}
		return null;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockBreak(BlockBreakEvent e) {
		Player player = e.getPlayer();
		if (isJailed(player.getUniqueId())) {
			e.setCancelled(true);
			player.sendMessage("§cYou cannot break blocks in jail.");
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockPlace(BlockPlaceEvent e) {
		Player player = e.getPlayer();
		if (isJailed(player.getUniqueId())) {
			e.setCancelled(true);
			player.sendMessage("§cYou cannot place blocks in jail.");
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
		Player player = e.getPlayer();
		if (isJailed(player.getUniqueId())) {
			if (e.getMessage().startsWith("/")) {
				e.setCancelled(true);
				player.sendMessage("§cYou are not allowed to use commands in jail.");
			}
		}
	}

	private void log(String player, String actor, String reason, Type type) {
		Calendar cal = Calendar.getInstance();
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int year = cal.get(Calendar.YEAR);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		int second = cal.get(Calendar.SECOND);
		String date = month + ":" + day + ":" + year + ":" + hour + ":" + minute + ":" + second;
		try {
			FileWriter fw = new FileWriter(logFile, true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write("[" + type + "] " + date + " " + player + " " + actor + " " + reason);
			bw.newLine();
			fw.flush();
			bw.close();
			fw.close();
		} catch (Exception e) {
			BrutusCore.createBrutusError(e, "Log Punishment");
		}
	}

	private void log(String player, String actor, String time, String reason) {
		Calendar cal = Calendar.getInstance();
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int year = cal.get(Calendar.YEAR);
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		int second = cal.get(Calendar.SECOND);
		String date = month + ":" + day + ":" + year + ":" + hour + ":" + minute + ":" + second;
		try {
			FileWriter fw = new FileWriter(logFile, true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(date + " " + player + " " + actor + " " + time + reason);
			bw.newLine();
			fw.flush();
			bw.close();
			fw.close();
		} catch (Exception e) {
			BrutusCore.createBrutusError(e, "Log Punishment");
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerLoginEvent event) {
		Player p = event.getPlayer();
		if (isBanned(p.getUniqueId())) {
			// TODO Change the ban message
			event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "§cYou are banned.");
		}
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		try {
			boolean noPermission = true;
			if (args.length < 1) {
				sender.sendMessage("§cNot enough arguments.");
				return true;
			}

			if (Bukkit.getOfflinePlayer(args[0]) == null) {
				if (cmd.getName() != "jail") {
					sender.sendMessage("§cThat player does not exist.");
					return true;
				}
			}

			UUID target = null;

			if (cmd.getName() == "jail") {
				target = Bukkit.getOfflinePlayer(args[1]).getUniqueId();
			} else {
				target = Bukkit.getOfflinePlayer(args[0]).getUniqueId();
			}
			ArrayList<String> formats = new ArrayList<String>(
					Arrays.asList("years", "year", "y", "months", "month", "m", "weeks", "week", "w", "days", "day",
							"d", "hours", "hour", "h", "minutes", "minute", "min", "seconds", "second", "s"));

			if (cmd.getName().equalsIgnoreCase("ban")) {
				if (sender.hasPermission(Perms.BAN_COMMAND)) {
					String reason = "";
					for (int i = 1; i < args.length; i++) {
						reason += args[i] + " ";
					}
					banPlayer(sender, target, reason);
				} else {
					noPermission = false;
				}
			} else if (cmd.getName().equalsIgnoreCase("tempban")) {
				if (sender.hasPermission(Perms.TEMPBAN_COMMAND)) {
					if (isBanned(target)) {
						sender.sendMessage("§cThat player is already banned.");
						return true;
					}
					String rawtime = args[1];
					String format = "";

					for (String f : formats) {
						if (rawtime.toLowerCase().contains(f)) {
							format = f;
							break;
						}
					}
					rawtime = rawtime.replace(format, "");
					int timeValue = 0;
					try {
						timeValue = Integer.parseInt(rawtime);
					} catch (NumberFormatException e) {
						sender.sendMessage("§cPlease enter a valid time format.");
						return true;
					}
					if (format == "years" || format == "year" || format == "y") {
						timeValue += timeValue * 60 * 60 * 24 * 7 * 4 * 12;
					} else if (format == "months" || format == "month" || format == "m") {
						timeValue += timeValue * 60 * 60 * 24 * 7 * 4;
					} else if (format == "weeks" || format == "week" || format == "w") {
						timeValue += timeValue * 60 * 60 * 24 * 7;
					} else if (format == "days" || format == "day" || format == "d") {
						timeValue += timeValue * 60 * 60 * 24;
					} else if (format == "hours" || format == "hour" || format == "d") {
						timeValue += timeValue * 60 * 60;
					} else if (format == "minutes" || format == "minute" || format == "min") {
						timeValue += timeValue * 60;
					}

					String reason = "";
					for (int i = 2; i < args.length; i++) {
						reason += args[i] + " ";
					}
					banPlayer(sender, target, timeValue, reason);
				} else {
					noPermission = false;
				}
			} else if (cmd.getName().equalsIgnoreCase("unban")) {
				if (sender.hasPermission(Perms.UNBAN_COMMAND)) {
					if (!isBanned(target)) {
						sender.sendMessage("§cThat player is not banned.");
						return true;
					}
					unbanPlayer(sender, target);
				} else {
					noPermission = false;
				}
			} else if (cmd.getName().equalsIgnoreCase("mute")) {
				if (sender.hasPermission(Perms.MUTE_COMMAND)) {
					String reason = "";
					for (int i = 1; i < args.length; i++) {
						reason += args[i] + " ";
					}
					mutePlayer(sender, target, reason);
				} else {
					noPermission = false;
				}
			} else if (cmd.getName().equalsIgnoreCase("tempmute")) {
				if (isMuted(target)) {
					sender.sendMessage("§cThat player is already muted.");
					return true;
				}
				String rawtime = args[1];
				String format = "";

				for (String f : formats) {
					if (rawtime.toLowerCase().contains(f)) {
						format = f;
						break;
					}
				}

				rawtime = rawtime.replace(format, "");
				long timeValue = 0;
				try {
					timeValue = Integer.parseInt(rawtime);
				} catch (NumberFormatException e) {
					sender.sendMessage("§cPlease enter a valid time format.");
					return true;
				}
				if (format == "years" || format == "year" || format == "y") {
					timeValue = timeValue * 60 * 60 * 24 * 7 * 4 * 12;
				} else if (format == "months" || format == "month" || format == "m") {
					timeValue = timeValue * 60 * 60 * 24 * 7 * 4;
				} else if (format == "weeks" || format == "week" || format == "w") {
					timeValue = timeValue * 60 * 60 * 24 * 7;
				} else if (format == "days" || format == "day" || format == "d") {
					timeValue = timeValue * 60 * 60 * 24;
				} else if (format == "hours" || format == "hour" || format == "d") {
					timeValue = timeValue * 60 * 60;
				} else if (format == "minutes" || format == "minute" || format == "min") {
					timeValue = timeValue * 60;
				}

				timeValue = timeValue + (System.currentTimeMillis() * 1000);

				String reason = "";
				for (int i = 2; i < args.length; i++) {
					reason += args[i] + " ";
				}
				mutePlayer(sender, target, timeValue, reason);
			} else if (cmd.getName().equalsIgnoreCase("unmute")) {
				if (sender.hasPermission(Perms.UNMUTE_COMMAND)) {
					if (!isMuted(target)) {
						sender.sendMessage("§cThat player is not muted.");
						return true;
					}
					unmutePlayer(sender, target);
				} else {
					noPermission = false;
				}
			} else if (cmd.getName().equalsIgnoreCase("kick")) {
				if (sender.hasPermission(Perms.KICK_COMMAND)) {
					String reason = "";
					for (int i = 1; i < args.length; i++) {
						reason += args[i] + " ";
					}
					kickPlayer(sender, target, reason);
				} else {
					noPermission = false;
				}
			} else if (cmd.getName().equalsIgnoreCase("jail")) {
				if (sender.hasPermission(Perms.JAIL_COMMAND)) {
					if (args[0].equalsIgnoreCase("create")) {
						if (!sender.hasPermission(Perms.CREATE_JAIL)) {
							sender.sendMessage("§cYou do not have permission to create a jail.");
							return true;
						}
						if (!(sender instanceof Player)) {
							sender.sendMessage("§cOnly players may create jails.");
							return true;
						}

						Player player = (Player) sender;

						Selection sel = plugin.getWorldEdit().getSelection(player);
						if (sel == null) {
							player.sendMessage("§cYou must make a WorldEdit selection!");
							return true;
						}
						if (!(sel instanceof CuboidSelection)) {
							player.sendMessage("§cThe WorldEdit selection must be Cuboid!");
							return true;
						}
						CuboidSelection selection = (CuboidSelection) sel;
						ProtectedCuboidRegion region = new ProtectedCuboidRegion(args[1],
								new BlockVector(selection.getNativeMaximumPoint()),
								new BlockVector(selection.getNativeMinimumPoint()));
						plugin.getWorldGuard().getRegionManager(player.getWorld()).addRegion(region);
						jails.put(args[1], new Jail(args[1], region));
						player.sendMessage("§aCreated a jail with the name: §b" + args[1]);
						player.sendMessage(
								"§aNow set the teleport point with §b/jail settelportpoint <name> §aor §b/jail stpp <name>");
					} else if (args[0].equalsIgnoreCase("setteleportpoint") || args[0].equalsIgnoreCase("stpp")) {
						if (!sender.hasPermission(Perms.CREATE_JAIL)) {
							sender.sendMessage("§cYou do not have permission to set the teleport point of a jail.");
							return true;
						}
						if (!(sender instanceof Player)) {
							sender.sendMessage("§cOnly players may do that.");
							return true;
						}

						Player player = (Player) sender;

						Jail jail = jails.get(args[1]);
						if (jail == null) {
							player.sendMessage("§cThat is not a valid jail.");
							return true;
						}

						jail.setTeleportPoint(player.getLocation());
						jails.put(jail.getJailName(), jail);

						player.sendMessage(
								"§aSet the teleport point of §b" + jail.getJailName() + " §ato your location.");
					} else if (args[0].equalsIgnoreCase("remove")) {
						Jail jail = jails.get(args[1]);
						if (jail == null) {
							sender.sendMessage("§cThat jail does not exist.");
							return true;
						}

						jails.remove(jail.getJailName());
						sender.sendMessage("§aSuccessfully removed jail §b" + jail.getJailName());
					} else {
						Player playerTarget = Bukkit.getPlayer(target);
						if (playerTarget == null) {
							sender.sendMessage("§cOnly online players may be jailed.");
							return true;
						}

						Jail jail = jails.get(args[1]);
						if (jail == null) {
							sender.sendMessage("§cThat jail does not exist.");
							return true;
						}

						String reason = "";
						for (int i = 2; i < args.length; i++) {
							reason += args[i] + " ";
						}

						jailPlayer(sender, target, reason, jail.getJailName());
					}
				} else {
					noPermission = false;
				}
			} else if (cmd.getName().equalsIgnoreCase("unjail")) {
				if (sender.hasPermission(Perms.UNJAIL_COMMAND)) {
					if (!isJailed(target)) {
						sender.sendMessage("§cThat player is not jailed.");
						return true;
					}
					unjailPlayer(sender, target);
				} else {
					noPermission = false;
				}
			}
			if (!noPermission) {
				sender.sendMessage(plugin.settings().getNoPermissionMessage());
			}
		} catch (Exception e) {
			BrutusCore.createBrutusError(e, "Punishment Commands");
		}
		return true;
	}

	private void broadcastPunishMessage(CommandSender rawActor, UUID target, String type, String reason) {
		if (rawActor instanceof Player) {
			Player a = (Player) rawActor;
			Bukkit.getServer().broadcastMessage(plugin.settings().getPunishMessage(
					Bukkit.getServer().getOfflinePlayer(target).getName(), type, a.getName(), reason));
		} else if (rawActor instanceof ConsoleCommandSender) {
			Bukkit.getServer().broadcastMessage(plugin.settings()
					.getPunishMessage(Bukkit.getServer().getOfflinePlayer(target).getName(), type, "Console", reason));
		}
	}

	private void broadcastUnpunishMessage(CommandSender rawActor, UUID target, String type) {
		if (rawActor instanceof Player) {
			Player a = (Player) rawActor;
			Bukkit.getServer().broadcastMessage(plugin.settings()
					.getUnpunishMessage(Bukkit.getServer().getOfflinePlayer(target).getName(), type, a.getName()));
		} else if (rawActor instanceof ConsoleCommandSender) {
			Bukkit.getServer().broadcastMessage(plugin.settings()
					.getUnpunishMessage(Bukkit.getServer().getOfflinePlayer(target).getName(), type, "Console"));
		}
	}
	
	class PunishmentCheckExpire extends BukkitRunnable {
		public void run() {
			ArrayList<Punishment> punishments = new ArrayList<Punishment>();
			for (Tempban ban : tempbans.values()) {
				punishments.add(ban);
			}

			for (Tempmute mute : tempmutes.values()) {
				punishments.add(mute);
			}

			for (Punishment punishment : punishments) {
				if ((System.currentTimeMillis() * 1000) >= punishment.getExpire()) {
					if (punishment instanceof Tempban) {
						tempbans.remove(punishment.getPlayer());
					}

					if (punishment instanceof Tempmute) {
						if (Bukkit.getOfflinePlayer(punishment.getPlayer()).isOnline()) {
							Bukkit.getPlayer(punishment.getPlayer()).sendMessage("§aYour tempmute has expired.");
						}
						tempmutes.remove(punishment.getPlayer());
					}
				}
			}
		}
	}
}