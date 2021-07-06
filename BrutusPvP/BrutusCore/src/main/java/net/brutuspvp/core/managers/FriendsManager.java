package net.brutuspvp.core.managers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import com.firestar311.fireutils.classes.Utils;

import net.brutuspvp.core.BrutusCore;
import net.brutuspvp.core.enums.Toggle;
import net.brutuspvp.core.model.BrutusUser;
import net.brutuspvp.core.model.FriendRequest;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;

public class FriendsManager implements CommandExecutor, Listener {

	private BrutusCore plugin;

	private ArrayList<FriendRequest> friendRequests = new ArrayList<FriendRequest>();
	private HashMap<UUID, UUID> removeFriends = new HashMap<UUID, UUID>();
	
	private File file;
	private FileConfiguration config;

	public FriendsManager(BrutusCore plugin) {
		this.plugin = plugin;
		this.plugin.registerListener(this);
		
		file = Utils.createYamlFile(plugin, "removefriends");
		config = Utils.createYamlConfig(plugin, file, "list");
		
		this.load();

		new BukkitRunnable() {
			public void run() {
				for (int i = 0; i < friendRequests.size(); i++) {
					FriendRequest fr = friendRequests.get(i);
					if (fr.getTimeout() <= 1) {
						friendRequests.remove(i);
						i--;
					} else {
						fr.setTimeout(fr.getTimeout() - 1);
						friendRequests.set(i, fr);
					}
				}
				
				for (BrutusUser user : plugin.players().getOnlineUsers()) {
					if (removeFriends.get(user.getUniqueId()) != null) {
						UUID remove = removeFriends.get(user.getUniqueId());
						OfflinePlayer of = Bukkit.getOfflinePlayer(remove);
						user.removeFriend(remove);
						user.sendMessage("§b" + of.getName() + " has removed you from their friends list since while you were offline.");
						removeFriends.remove(user.getUniqueId());
					}
				}
			}
		}.runTaskTimer(plugin, 20, 20);
	}
	
	public void save() {
		for (Entry<UUID, UUID> entry : removeFriends.entrySet()) {
			ArrayList<String> list = new ArrayList<String>();
			list.add(entry.getKey().toString() + ":" + entry.getValue().toString());
			config.set("list", list);
			Utils.saveFile(file, config);
		}
	}
	
	public void load() {
		for (String s : config.getStringList("list")) {
			String[] ids = s.split(":");
			UUID first = UUID.fromString(ids[0]);
			UUID second = UUID.fromString(ids[1]);
			removeFriends.put(first, second);
		}
	}

	public void sendFriendRequest(BrutusUser requester, BrutusUser toadd) {
		if (toadd == null) {
			requester.sendMessage("That is not a valid player.");
			return;
		}
		FriendRequest request = new FriendRequest(requester.getUniqueId(), toadd.getUniqueId());
		request.setTimeout(60);
		friendRequests.add(request);
		
		requester.sendMessage("§6--------------------------------");
		requester.sendMessage("§aSent a friend request to " + toadd.getDisplayName());
		requester.sendMessage("§bThey have 60 seconds to accept before it times out.");
		requester.sendMessage("§6--------------------------------");
		toadd.sendMessage("§6--------------------------------");
		toadd.sendMessage(requester.getDisplayName() + " §ahas sent you a friend request.");
		TextComponent frcmd = new TextComponent("Options: ");
		TextComponent acceptButton = new TextComponent("[ACCEPT]");
		acceptButton.setColor(net.md_5.bungee.api.ChatColor.GREEN);
		acceptButton.setBold(true);
		acceptButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/friends accept " + requester.getName()));
		TextComponent denyButton = new TextComponent("[DENY]");
		denyButton.setColor(net.md_5.bungee.api.ChatColor.RED);
		denyButton.setBold(true);
		denyButton.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/friends deny " + requester.getName()));
		frcmd.setColor(net.md_5.bungee.api.ChatColor.GRAY);
		frcmd.addExtra(acceptButton);
		frcmd.addExtra(" - ");
		frcmd.addExtra(denyButton);
		toadd.sendMessage(frcmd);
		toadd.sendMessage("§6--------------------------------");
	}

	public void removeFriend(BrutusUser user, String toremove) {
		UUID tormve = plugin.players().getUUID(toremove);
		if (tormve == null) {
			user.sendMessage("§cThat is not a valid player.");
			return;
		}
		if (user.getFriends().contains(tormve)) {
			user.removeFriend(tormve);
			OfflinePlayer of = Bukkit.getServer().getOfflinePlayer(tormve);
			user.sendMessage(
					"§cYou have removed the user §b{name} §cfrom your friends list.".replace("{name}", of.getName()));
			removeFriends.put(of.getUniqueId(), user.getUniqueId());
			if (of.isOnline()) {
				Player p = of.getPlayer();
				BrutusUser tr = plugin.players().getBrutusUser(p);
				tr.removeFriend(user.getUniqueId());
				p.sendMessage("§b{name} §chas removed you from their friends list.".replace("{name}", user.getName()));
			}
		} else {
			user.sendMessage("§cThat player is not on your friends list.");
			return;
		}
	}

	public void listFriends(BrutusUser user) {
		if (user.getFriends() == null) {
			user.sendMessage("§cYou have not added any friends yet.");
			return;
		}
		
		ArrayList<UUID> rawFriendsList = user.getFriends();

		if (rawFriendsList.isEmpty() || rawFriendsList == null) {
			user.sendMessage("§cYou have not added any friends yet.");
			return;
		}

		LinkedList<Player> onlineFriends = new LinkedList<Player>();
		LinkedList<OfflinePlayer> offlineFriends = new LinkedList<OfflinePlayer>();
		for (UUID f : rawFriendsList) {
			if (Bukkit.getServer().getPlayer(f) != null) {
				onlineFriends.add(Bukkit.getServer().getPlayer(f));
			} else {
				offlineFriends.add(Bukkit.getServer().getOfflinePlayer(f));
			}
		}

		LinkedList<String> coloredList = new LinkedList<String>();

		onlineFriends.forEach(p -> coloredList.add(ChatColor.GREEN + p.getName() + " is online."));
		offlineFriends.forEach(of -> coloredList.add(ChatColor.RED + of.getName() + " is offline."));

		user.sendMessage("§6Listing your confirmed friends.");
		// TODO Change this to pagination in the future.
		coloredList.forEach(f -> user.sendMessage(f));
	}

	public void listActiveFriendRequests(BrutusUser user) {
		ArrayList<String> requests = new ArrayList<String>();
		for (FriendRequest request : friendRequests) {
			if (request.getToAdd().equals(user.getUniqueId())) {
				requests.add(Bukkit.getServer().getPlayer(request.getRequester()).getName());
			}
		}

		user.sendMessage("§2Here is a list of your active friend requests: ");
		for (String f : requests) {
			TextComponent message = new TextComponent(f + " -> Click here to accept!");
			message.setColor(net.md_5.bungee.api.ChatColor.GREEN);
			message.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/friends accept " + f));
			user.sendMessage(message);
		}
		user.sendMessage("§2End of friend requests.");
	}

	public void acceptFriendRequest(BrutusUser accepter, String requester) {
		for (int i=0; i<friendRequests.size(); i++) {
			FriendRequest request = friendRequests.get(0);
			if (request.getToAdd().equals(accepter.getUniqueId())) {
				UUID requesterUUID = request.getRequester();
				if (requesterUUID != null) {
					Player requesterPlayer = Bukkit.getPlayer(requesterUUID);
					if (requesterPlayer != null) {
						BrutusUser requesterUser = plugin.players().getBrutusUser(requesterUUID);
						accepter.addFriend(requesterUser.getUniqueId());
						requesterUser.addFriend(accepter.getUniqueId());
						accepter.sendMessage("§aYou have accepted §b{name}§a's friend request.".replace("{name}",
								requesterUser.getName()));
						requesterUser.sendMessage("§b{name} §ahas accepted your friend request."
								                  .replace("{name}", accepter.getName()));
					} else {
						accepter.sendMessage("§cThat user is not online.");
						return;
					}
				} else {
					accepter.sendMessage("§cThat user has not sent a friend request to you.");
					return;
				}
				friendRequests.remove(i);
				return;
			}
		}
	}

	public void denyFriendRequest(BrutusUser denyer, String requester) {
		for (int i=0; i<friendRequests.size(); i++) {
			FriendRequest request = friendRequests.get(0);
			if (request.getToAdd().equals(denyer.getUniqueId())) {
				UUID requesterUUID = request.getRequester();
				if (requesterUUID != null) {
					Player requesterPlayer = Bukkit.getPlayer(requesterUUID);
					if (requesterPlayer != null) {
						BrutusUser requesterUser = plugin.players().getBrutusUser(requesterUUID);
						denyer.sendMessage("§aYou have denied §b{name}§a's friend request.".replace("{name}",
								requesterUser.getName()));
						requesterUser.sendMessage("§b{name} §ahas denied your friend request."
								.replace("{name}", denyer.getName()));
						friendRequests.remove(i);
						return;
					} else {
						denyer.sendMessage("§cThat user is not online.");
						return;
					}
				} else {
					denyer.sendMessage("§cThat user has not sent a friend request to you.");
					return;
				}
			}
		}
	}

	public void toggleFriendRequest(BrutusUser user) {
		user.setToggle(Toggle.FRIEND_REQUEST, !user.getToggle(Toggle.FRIEND_REQUEST));
		if (user.getToggle(Toggle.FRIEND_REQUEST)) {
			user.sendMessage("§aYou have enabled friend requests.");
			return;
		} else {
			user.sendMessage("§cYou have disabled friend requests.");
			return;
		}
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 0) {
			sender.sendMessage("List of subcommands coming soon.");
		} else if (args.length > 0) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(plugin.settings().getPlayerOnlyCommandMessage());
				return true;
			}

			Player player = (Player) sender;
			BrutusUser user = plugin.players().getBrutusUser(player);

			if (args[0].equalsIgnoreCase("add")) {
				if (args.length > 1) {
					BrutusUser toadd = plugin.players().getBrutusUser(args[1]);
					this.sendFriendRequest(user, toadd);
				} else {
					user.sendMessage("§cYou must enter an online player's name to add them as a friend.");
					return true;
				}
			} else if (args[0].equalsIgnoreCase("remove")) {
				if (args.length > 1) {
					this.removeFriend(user, args[1]);
				} else {
					user.sendMessage("§cYou must enter an online player's name to add them as a friend.");
					return true;
				}
			} else if (args[0].equalsIgnoreCase("list")) {
				this.listFriends(user);
			} else if (args[0].equalsIgnoreCase("requests")) {
				this.listActiveFriendRequests(user);
			} else if (args[0].equalsIgnoreCase("accept")) {
				if (args.length > 1) {
					this.acceptFriendRequest(user, args[1]);
				} else {
					user.sendMessage("§cYou must enter a player's name to accept them as a friend.");
					return true;
				}
			} else if (args[0].equalsIgnoreCase("deny")) {
				if (args.length > 1) {
					this.denyFriendRequest(user, args[1]);
				} else {
					user.sendMessage("§cYou must enter a player's name to deny their friend request.");
					return true;
				}
			} else if (args[0].equalsIgnoreCase("toggle")) {
//				this.toggleFriendRequest(user);
				user.sendMessage("§cThis subcommand is not fully implemented.");
				return true;
			} else {
				sender.sendMessage("§cThat is not a valid sub command.");
				return true;
			}
		} else {
			sender.sendMessage("§cThat is not a valid sub command.");
			return true;
		}

		return true;
	}
}