package net.brutuspvp.core.managers;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import net.brutuspvp.core.BrutusCore;
import net.brutuspvp.core.Perms;
import net.brutuspvp.core.enums.Toggle;
import net.brutuspvp.core.model.BrutusUser;

public class PMManager implements CommandExecutor {
	
	private BrutusCore plugin;
	
	public PMManager(BrutusCore plugin) {
		this.plugin = plugin;
	}

	private HashMap<String, String> lastPM = new HashMap<String, String>();

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (cmd.getName().equalsIgnoreCase("pm") || cmd.getName().equalsIgnoreCase("msg")
				|| cmd.getName().equalsIgnoreCase("message")) {
			String name = "";
			if (sender instanceof ConsoleCommandSender) {
				name = "Console";
			} else if (sender instanceof Player) {
				name = ((Player) sender).getName();
			} else {
				return true;
			}

			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("Console")) {
					ConsoleCommandSender target = Bukkit.getServer().getConsoleSender();
					if (args.length > 1) {
						StringBuilder sb = new StringBuilder();
						for (int i = 1; i < args.length; i++) {
							sb.append(args[i]).append(" ");
						}

						String format = "§c{sender} §6-> §c{target}§8: §7{message}";
						sender.sendMessage(format.replace("{sender}", "You").replace("{target}", "Console")
								.replace("{message}", sb.toString()));
						this.lastPM.put(name, target.getName());

						target.sendMessage(format.replace("{sender}", name).replace("{target}", "You")
								.replace("{message}", sb.toString()));
						this.lastPM.put("Console", name);

					} else {
						sender.sendMessage("§cYou must provide a message to send.");
						return true;
					}
				} else {
					Player t = Bukkit.getPlayer(args[0]);
					if (t == null) {
						sender.sendMessage("§cThat player is not online.");
						return true;
					}
					if (args.length > 1) {
						BrutusUser target = plugin.players().getBrutusUser(t);
						
						if (!target.getToggle(Toggle.MESSAGES)) {
							boolean override = false;
							
							if (sender.hasPermission(Perms.TOGGLE_MESSAGE_OVERRIDE)) {
								for (int i = 0; i < args.length; i++) {
									String a = args[i];
									if (a.equalsIgnoreCase("-override") || a.equalsIgnoreCase("-o")) {
										override = true;
									}
								}
							}
							if (!override) {
								sender.sendMessage("§cThat player has messaging disabled.");
								return true;
							}
						}
						
						StringBuilder sb = new StringBuilder();
						for (int i = 1; i < args.length; i++) {
							if (!args[i].equalsIgnoreCase("-override") || !args[i].equalsIgnoreCase("-o")) {
								sb.append(args[i]).append(" ");
							}
						}
						
						String format = "§c{sender} §6-> §c{target}§8: §7{message}";
						sender.sendMessage(format.replace("{sender}", "You").replace("{target}", target.getName())
								.replace("{message}", sb.toString()));
						this.lastPM.put(name, target.getName());

						target.sendMessage(format.replace("{sender}", name).replace("{target}", "You")
								.replace("{message}", sb.toString()));
						this.lastPM.put(target.getName(), name);

					} else {
						sender.sendMessage("§cYou must provide a message to send.");
						return true;
					}
				}
			} else {
				sender.sendMessage("§cYou must provide someone to message.");
				return true;
			}
		} else if (cmd.getName().equalsIgnoreCase("reply") || cmd.getName().equalsIgnoreCase("r")) {
			String name = "";
			if (sender instanceof ConsoleCommandSender) {
				name = "Console";
			} else if (sender instanceof Player) {
				name = ((Player) sender).getName();
			} else {
				return true;
			}
			String last = this.lastPM.get(name);

			if (last == null || last == "") {
				sender.sendMessage("§cYou have not messaged anyone recently.");
				return true;
			}

			if (last == "Console") {
				if (args.length > 0) {
					StringBuilder sb = new StringBuilder();
					for (int i = 0; i < args.length; i++) {
						sb.append(args[i]).append(" ");
					}

					String format = "§c{sender} §6-> §c{target}§8: §7{message}";
					sender.sendMessage(format.replace("{sender}", "You").replace("{target}", "Console")
							.replace("{message}", sb.toString()));
					this.lastPM.put(name, "Console");

					Bukkit.getServer().getConsoleSender().sendMessage(format.replace("{sender}", name)
							.replace("{target}", "You").replace("{message}", sb.toString()));
					this.lastPM.put("Console", name);
				} else {
					sender.sendMessage("§cYou must provide a message to send.");
					return true;
				}
			} else {
				Player target = Bukkit.getPlayer(last);
				if (target == null) {
					sender.sendMessage("§cThe person you last messaged is no longer online.");
					return true;
				}
				if (args.length > 0) {
					StringBuilder sb = new StringBuilder();
					for (int i = 0; i < args.length; i++) {
						sb.append(args[i]).append(" ");
					}

					String format = "§c{sender} §6-> §c{target}§8: §7{message}";
					sender.sendMessage(format.replace("{sender}", "You").replace("{target}", target.getName())
							.replace("{message}", sb.toString()));
					this.lastPM.put(name, target.getName());

					target.sendMessage(format.replace("{sender}", name).replace("{target}", "You").replace("{message}",
							sb.toString()));
					this.lastPM.put(target.getName(), name);
				} else {
					sender.sendMessage("§cYou must provide a message to send.");
					return true;
				}
			}
		}
		return true;
	}
}