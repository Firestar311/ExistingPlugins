package net.brutuspvp.core.managers;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.firestar311.fireutils.classes.Utils;

import net.brutuspvp.core.BrutusCore;
import net.brutuspvp.core.enums.Channel;
import net.brutuspvp.core.model.BrutusUser;
import net.brutuspvp.core.model.OfflineBrutusUser;

public class PlayerManager implements Listener {

	private BrutusCore plugin;
	private File file;
	private FileConfiguration config;

	private HashMap<UUID, OfflineBrutusUser> offlineUsers = new HashMap<UUID, OfflineBrutusUser>();
	private HashMap<UUID, BrutusUser> onlineUsers = new HashMap<UUID, BrutusUser>();

	public PlayerManager(BrutusCore plugin) {
		this.plugin = plugin;
		plugin.registerListener(this);
		file = Utils.createYamlFile(plugin, "players");
		config = Utils.createYamlConfig(plugin, file, "players");
		
		for (File file : plugin.getUserFolder().listFiles()) {
			try {
				UUID uuid = UUID.fromString(file.getName().replace(".yml", ""));
				OfflineBrutusUser offlineUser = new OfflineBrutusUser(Bukkit.getOfflinePlayer(uuid));
				this.offlineUsers.put(uuid, offlineUser);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		for (Player player : Bukkit.getOnlinePlayers()) {
			
			OfflineBrutusUser offlineUser = null;
			
			if (this.offlineUsers.containsKey(player.getUniqueId())) {
				offlineUser = offlineUsers.get(player.getUniqueId());
				this.offlineUsers.remove(player.getUniqueId());
			}
			
			BrutusUser user = null;
			
			if (offlineUser != null) {
				user = new BrutusUser(offlineUser);
			} else {
				user = new BrutusUser(player);
			}

			if (!config.contains("players." + player.getName())) {
				config.set("players." + player.getName(), player.getUniqueId().toString());
				Utils.saveFile(file, config);
			}

			for (Sign sign : BrutusCore.getSignChanges()) {
				player.sendSignChange(sign.getLocation(), sign.getLines());
			}

			onlineUsers.put(player.getUniqueId(), user);

			if (plugin.punishments().isJailed(player.getUniqueId())) {
				user.setChannel(Channel.JAIL);
			}

			if (plugin.courts().inTrial(player)) {
				user.setChannel(Channel.TRIAL);
			}

			if (user.getChannel() == null) {
				user.setChannel(Channel.GLOBAL);
			}

			onlineUsers.put(player.getUniqueId(), user);
			ArrayList<String> lines = new ArrayList<String>();
			lines.add("The economy system is a work in progress.");
			lines.add("Shops, interest, denari voting rewards and other money things are broken.");
			BrutusCore.sendDeveloperAlert(player, lines);
		}
	}

	public BrutusUser getBrutusUser(UUID uuid) {
		return onlineUsers.get(uuid);
	}

	public BrutusUser getBrutusUser(String name) {
		return getBrutusUser(getUUID(name));
	}

	public BrutusUser getBrutusUser(Player player) {
		return getBrutusUser(player.getUniqueId());
	}

	public Collection<BrutusUser> getOnlineUsers() {
		return onlineUsers.values();
	}

	public UUID getUUID(String name) {
		if (config.getString("players." + name) == null || config.getString("players." + name).equals("")) {
			return null;
		}
		return UUID.fromString(config.getString("players." + name));
	}

	public void savePlayerData() {
		for (BrutusUser user : onlineUsers.values()) {
			user.saveUserData();
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		
		OfflineBrutusUser offlineUser = null;
		
		if (this.offlineUsers.containsKey(player.getUniqueId())) {
			offlineUser = offlineUsers.get(player.getUniqueId());
			this.offlineUsers.remove(player.getUniqueId());
		}
		
		BrutusUser user = null;
		
		if (offlineUser != null) {
			user = new BrutusUser(offlineUser);
		} else {
			user = new BrutusUser(player);
		}

		if (!config.contains("players." + player.getName())) {
			config.set("players." + player.getName(), player.getUniqueId().toString());
			Utils.saveFile(file, config);
		}

		for (Sign sign : BrutusCore.getSignChanges()) {
			player.sendSignChange(sign.getLocation(), sign.getLines());
		}

		onlineUsers.put(player.getUniqueId(), user);

		if (plugin.punishments().isJailed(player.getUniqueId())) {
			user.setChannel(Channel.JAIL);
		}

		if (plugin.courts().inTrial(player)) {
			user.setChannel(Channel.TRIAL);
		}

		onlineUsers.put(player.getUniqueId(), user);
		if (!player.getName().equalsIgnoreCase("Firestar311")) {
			ArrayList<String> lines = new ArrayList<String>();
			lines.add("The economy system is a work in progress.");
			lines.add("Shops, interest, denari voting rewards and other money things are broken.");
			BrutusCore.sendDeveloperAlert(player, lines);
		}

		if (!plugin.getDevVanish().contains(UUID.fromString("3f7891ce-5a73-4d52-a2ba-299839053fdc"))) {

			new BukkitRunnable() {
				public void run() {
					if (plugin.isFirestar311Online()) {
						if (!player.getName().equalsIgnoreCase("Firestar311")) {
							player.sendMessage("§4-----------------------------------------------------");
							player.sendMessage("§cNOTE: Firestar311 (Lead Developer) is currently online.");
							player.sendMessage(
									"      §cIf you try to speak to or message him, it may not be seen as he is usually testing things.");
							player.sendMessage(
									"      §cThis is not being rude, just that he is busy, thanks for understanding!");
							player.sendMessage("§4-----------------------------------------------------");
						}
					}
				}
			}.runTaskLater(plugin, 5L);
		}

		for (UUID uuid : plugin.getDevVanish()) {
			Player vanished = Bukkit.getPlayer(uuid);
			if (vanished == null)
				continue;

			if (!player.getUniqueId().equals(UUID.fromString("3f7891ce-5a73-4d52-a2ba-299839053fdc"))) {
				if (!plugin.getDevVanish().contains(player.getUniqueId())) {
					player.hidePlayer(vanished);
				}
			}
		}
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e) {
		BrutusUser user = onlineUsers.get(e.getPlayer().getUniqueId());
		user.saveUserData();
		onlineUsers.remove(user.getUniqueId());
		offlineUsers.put(e.getPlayer().getUniqueId(), new OfflineBrutusUser(Bukkit.getOfflinePlayer(e.getPlayer().getUniqueId())));
	}
}