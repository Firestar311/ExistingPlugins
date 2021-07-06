package net.brutuspvp.core.managers;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.firestar311.fireutils.classes.Utils;

import net.brutuspvp.core.BrutusCore;
import net.brutuspvp.core.Perms;
import net.brutuspvp.core.Variables;
import net.brutuspvp.core.enums.Channel;
import net.brutuspvp.core.model.BrutusUser;
import net.brutuspvp.core.model.Jail;
import net.brutuspvp.core.model.JailedUser;
import net.brutuspvp.core.model.Trial;
import net.brutuspvp.core.model.abstraction.LivingSpace;

@SuppressWarnings("unused")
public class ChatManager implements Listener, CommandExecutor {

	private static boolean enabled = true;
	private BrutusCore plugin;

	public ChatManager(BrutusCore plugin) {
		plugin.registerListener(this);
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		try {
			if (cmd.getName().equalsIgnoreCase("chat") || cmd.getName().equalsIgnoreCase("c")) {
				if (args.length > 0) {
					boolean isChannel = false;
					try {
						Channel.valueOf(args[0].toUpperCase());
						isChannel = true;
					} catch (Exception e) {
					}

					if (Utils.checkArguments(args, 0, "toggle", "t")) {

						if (!sender.hasPermission(Perms.CHAT_TOGGLE)) {
							sender.sendMessage(plugin.settings().getNoPermissionMessage());
							return true;
						}

						enabled = !enabled;

						if (enabled) {
							sender.sendMessage(plugin.settings().getActorEnableChatMessage());
						} else {
							sender.sendMessage(plugin.settings().getActorDisableChatMessage());
						}

						if (sender instanceof ConsoleCommandSender) {
							for (Player p : Bukkit.getOnlinePlayers()) {
								if (enabled) {
									p.sendMessage(plugin.settings().getAllEnableChatMessage("CONSOLE"));
								} else {
									p.sendMessage(plugin.settings().getAllDisableChatMessage("CONSOLE"));
								}
							}
						} else if (sender instanceof Player) {
							Player player = (Player) sender;
							for (Player p : Bukkit.getOnlinePlayers()) {
								if (!p.getName().equalsIgnoreCase(player.getName())) {
									if (enabled) {
										p.sendMessage(plugin.settings().getAllEnableChatMessage(player.getName()));
									} else {
										p.sendMessage(plugin.settings().getAllDisableChatMessage(player.getName()));
									}
								}
							}
						}
					} else if (Utils.checkArguments(args, 0, "clear", "c")) {
						if (sender.hasPermission(Perms.CHAT_CLEAR)) {
							Bukkit.getOnlinePlayers().forEach(p -> {
								for (int i = 0; i < 200; i++) {
									p.sendMessage("");
								}
								if (sender instanceof ConsoleCommandSender) {
									p.sendMessage("§cCONSOLE §ahas cleared the chat!");
								} else if (sender instanceof Player) {
									Player player = (Player) sender;
									p.sendMessage("§c" + player.getName() + " §ahas cleared the chat!");
								}
							});
						} else {
							sender.sendMessage(plugin.settings().getNoPermissionMessage());
							return true;
						}
					} else if (isChannel) {
						Channel channel = Channel.valueOf(args[0].toUpperCase());
						if (sender instanceof Player) {
							Player player = (Player) sender;
							BrutusUser user = plugin.players().getBrutusUser(player);
							if (channel == Channel.STAFF) {
								if (!player.hasPermission(Perms.STAFF_CHAT)) {
									player.sendMessage(plugin.settings().getNoPermissionMessage());
									return true;
								}
							} else if (channel == Channel.TRADE) {
								if (!player.hasPermission(Perms.TRADE_CHANNEL)) {
									player.sendMessage(plugin.settings().getNoPermissionMessage());
									return true;
								}
							} else if (channel == Channel.LIVINGSPACE) {
								if (!player.hasPermission(Perms.LIVINGSPACE_CHANNEL)) {
									player.sendMessage(plugin.settings().getNoPermissionMessage());
									return true;
								}
							} else if (channel == Channel.TOWN) {
								player.sendMessage("§cThe §3§lTOWN §cchannel is not currently implemented");
							}
							user.setChannel(channel);
							String channelString = channel.getColorBold() + channel.toString();
							user.sendMessage("§aYou have set your channel to " + channelString);
						} else {
							sender.sendMessage(plugin.settings().getPlayerOnlyCommandMessage());
							return true;
						}
					} else if (Utils.checkArguments(args, 0, "hide", "h")) {
						if (args.length > 1) {
							if (sender instanceof Player) {
								Player player = (Player) sender;
								BrutusUser user = plugin.players().getBrutusUser(player);
								Channel channel = Channel.valueOf(args[1]);
								if (user.isHiddenChannel(channel)) {
									user.sendMessage("§cThat channel is already hidden.");
									return true;
								}

								user.addHiddenChannel(channel);
								String channelString = channel.getColorBold() + channel.toString();
								user.sendMessage("§cNow hiding " + channelString);
								return true;
							} else {
								sender.sendMessage(plugin.settings().getPlayerOnlyCommandMessage());
								return true;
							}
						} else {
							sender.sendMessage("§cYou must provide a channel to hide.");
						}
					} else if (Utils.checkArguments(args, 0, "unhide", "uh")) {
						if (args.length > 1) {
							if (sender instanceof Player) {
								Player player = (Player) sender;
								BrutusUser user = plugin.players().getBrutusUser(player);
								Channel channel = Channel.valueOf(args[1]);
								if (!user.isHiddenChannel(channel)) {
									user.sendMessage("§cThat channel is not hidden.");
									return true;
								}

								user.removeHiddenChannel(channel);
								String channelString = channel.getColorBold() + channel.toString();
								user.sendMessage("§aNo longer hiding " + channelString);
								return true;
							} else {
								sender.sendMessage(plugin.settings().getPlayerOnlyCommandMessage());
								return true;
							}
						} else {
							sender.sendMessage("§cYou must provide a channel to hide.");
						}
					} else {
						sender.sendMessage("§cThat is not a subcommand.");
						return true;
					}
				} else {
					sender.sendMessage("§cNot enough arguments.");
					return true;
				}
			}
		} catch (Exception e) {
			BrutusCore.createBrutusError(sender, e, "ChatManager onCommand()");
		}
		
		return true;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		try {
			Player player = e.getPlayer();
			BrutusUser user = plugin.players().getBrutusUser(player);
			Channel channel = (user.getChannel() == null) ? Channel.GLOBAL : user.getChannel();
			
			if (plugin.punishments().isMuted(player.getUniqueId()))	{
				Bukkit.getServer().getConsoleSender().sendMessage("MUTED " + player.getName() + ": " + e.getMessage());
			} else {
				Bukkit.getServer().getConsoleSender().sendMessage(channel.toString() + " " + player.getName() + ": " + e.getMessage());
			}

			if (plugin.punishments().isMuted(user.getUniqueId())) {
				// TODO Add how much time is left.
				user.sendMessage("§cYou may not speak while muted.");
				e.setCancelled(true);
				return;
			} else if (plugin.punishments().isJailed(user.getUniqueId())) {
				JailedUser jailed = plugin.punishments().getJailedUser(user.getUniqueId());
				BrutusUser jd = plugin.players().getBrutusUser(jailed.getPlayer());
				jd.setChannel(Channel.JAIL);
				if (jailed != null) {
					String chatFormat = "§3§l[§b§lJAIL§7:§b{name}§3§l] {displayname}§8: §b{message}";
					Jail jail = jailed.getJail();
					if (jail != null) {
						chatFormat = chatFormat.replace("{name}", jail.getJailName());
						chatFormat = chatFormat.replace("{displayname}", user.getDisplayName());
						chatFormat = chatFormat.replace("{message}", e.getMessage());
						e.setCancelled(true);
						for (UUID uuid : jail.getPlayers()) {
							Player jailMember = Bukkit.getPlayer(uuid);
							if (jailMember != null) {
								jailMember.sendMessage(chatFormat);
							}
						}
					}
				}
			} else if (plugin.courts().inTrial(player)) {
				Trial trial = null;

				for (Trial t : plugin.courts().getTrials().values()) {
					if (t.getParticipants().contains(player)) {
						trial = t;
						return;
					}
				}

				if (trial != null) {
					e.setCancelled(true);
					String chatFormat = "§e§l[§6§lTRIAL§e§l] {displayname}§8: §6{message}";
					chatFormat = chatFormat.replace("{displayname}", player.getDisplayName());
					chatFormat = chatFormat.replace("{message}", e.getMessage());

					for (Player p : trial.getParticipants()) {
						p.sendMessage(chatFormat);
					}
				}
			} else if (!enabled) {
				if (user.getChannel() == Channel.GLOBAL) {
					if (!user.hasPermission(Perms.CHAT_BYPASS)) {
						e.setCancelled(true);
						user.sendMessage("§cChat is currently disabled. Only Staff May Speak.");
					} else {
						String chatFormat = plugin.getConfig().getString("chat.formatting.global");
						chatFormat = chatFormat.replace(Variables.PLAYER_DISPLAYNAME, player.getDisplayName());
						chatFormat = chatFormat.replace(Variables.MESSAGE, e.getMessage());
						e.setCancelled(true);
						for (BrutusUser bu : plugin.players().getOnlineUsers()) { 
							if ((bu.getChannel() != Channel.TRIAL) || !(bu.getChannel() != Channel.JAIL)) {
								bu.sendMessage(chatFormat);
							}
						}
					}
				} else if (user.getChannel() == Channel.STAFF) {
					if (user.hasPermission(Perms.STAFF_CHAT)) {
						e.setCancelled(true);
						String chatFormat = "§2§l[§a§lSTAFF§2§l] {displayname}§8: §a{message}";
						chatFormat = chatFormat.replace("{displayname}", player.getDisplayName());
						chatFormat = chatFormat.replace("{message}", e.getMessage());
						for (BrutusUser u : plugin.players().getOnlineUsers()) {
							if (!u.isHiddenChannel(Channel.STAFF)) {
								if (u.hasPermission(Perms.STAFF_CHAT)) {
									u.sendMessage(chatFormat);
								}
							}
						}
					} else {
						user.sendMessage("§cYou do not have permission to speak in staff chat.");
						return;
					}
				}
		    } else if (user.getChannel() == Channel.TRADE) {
				e.setCancelled(true);

				String chatFormat = "§1§l[§9§lTRADE§1§l] {displayname}§8: §9{message}";
				chatFormat = chatFormat.replace("{displayname}", player.getDisplayName());
				chatFormat = chatFormat.replace("{message}", e.getMessage());
				for (BrutusUser u : plugin.players().getOnlineUsers()) {
					if (!u.isHiddenChannel(Channel.TRADE)) {
						u.sendMessage(chatFormat);
					}
				}
			} else if (user.getChannel() == Channel.STAFF) {
				if (user.hasPermission(Perms.STAFF_CHAT)) {
					e.setCancelled(true);
					String chatFormat = "§2§l[§a§lSTAFF§2§l] {displayname}§8: §a{message}";
					chatFormat = chatFormat.replace("{displayname}", player.getDisplayName());
					chatFormat = chatFormat.replace("{message}", e.getMessage());
					for (BrutusUser u : plugin.players().getOnlineUsers()) {
						if (!u.isHiddenChannel(Channel.STAFF)) {
							if (u.hasPermission(Perms.STAFF_CHAT)) {
								u.sendMessage(chatFormat);
							}
						}
					}
				} else {
					user.sendMessage("§cYou do not have permission to speak in staff chat.");
					return;
				}
			} else if (user.getChannel() == Channel.LIVINGSPACE) {
				LivingSpace space = plugin.livingspaces().getLivingSpace(user.getLocation());
				
				if (space == null) {
					user.sendMessage("§cYou are currently not within a living space. Please change your channel or go to one.");
					return;
				}
				
				if (!space.getMembers().contains(user.getName())) {
					user.sendMessage("§cYou are not a member of this LivingSpace. You may not speak in the channel.");
					return;
				}
				
				e.setCancelled(true);
				String chatFormat = "§6§l[§e§lLIVINGSPACE§7:§e{name}§6§l] {displayname}§8: §e{message}";
				chatFormat = chatFormat.replace("{name}", space.getName());
				chatFormat = chatFormat.replace("{displayname}", user.getDisplayName());
				chatFormat = chatFormat.replace("{message}", e.getMessage());
				for (String m : space.getMembers()) {
					Player p = Bukkit.getPlayer(m);
					if (p != null) {
						LivingSpace ls = plugin.livingspaces().getLivingSpace(p.getLocation());
						if (ls != null) {
							if (ls.getName().equalsIgnoreCase(space.getName())) {
								p.sendMessage(chatFormat);
							}
						}
					}
				}
			} else if (user.getChannel() == Channel.GLOBAL) {
				String chatFormat = plugin.settings().getChatFormat(player, e.getMessage());
				chatFormat = chatFormat.replace(Variables.PLAYER_DISPLAYNAME, player.getDisplayName());
				chatFormat = chatFormat.replace(Variables.MESSAGE, e.getMessage());
				e.setCancelled(true);
				for (BrutusUser bu : plugin.players().getOnlineUsers()) { 
					if (bu.getChannel() == Channel.TRIAL || bu.getChannel() == Channel.JAIL) {
						continue;
					} else {
						if (plugin.punishments().isJailed(bu.getUniqueId()) || plugin.courts().inTrial(bu.getUniqueId())) {
							continue;
						} else {
							bu.sendMessage(chatFormat);
						}
					}
				}
			} else {
				e.setCancelled(true);
			}
		} catch (Exception ex) {
			BrutusCore.createBrutusError(ex, "ChatManager AsyncPlayerChatEvent");
		}
	}
}