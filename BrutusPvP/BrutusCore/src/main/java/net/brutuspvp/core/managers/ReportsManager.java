package net.brutuspvp.core.managers;

import java.io.File;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.firestar311.fireutils.classes.Utils;
import com.firestar311.fireutils.pagination.Paginator;
import com.firestar311.fireutils.pagination.PaginatorFactory;

import net.brutuspvp.core.BrutusCore;
import net.brutuspvp.core.Perms;
import net.brutuspvp.core.enums.Status;
import net.brutuspvp.core.enums.Type;
import net.brutuspvp.core.model.ChatReport;
import net.brutuspvp.core.model.PlayerReport;
import net.brutuspvp.core.model.TicketReport;
import net.brutuspvp.core.model.abstraction.Report;

@SuppressWarnings({"deprecation" })
public class ReportsManager implements CommandExecutor {

	private BrutusCore plugin;
	private File file;
	private FileConfiguration config;
	private TreeMap<Integer, Report> reports = new TreeMap<Integer, Report>();
	private HashMap<UUID, Paginator<Report>> paginators = new HashMap<UUID, Paginator<Report>>();

	// TODO Have proper handling of integer parsing with a try-catch block.
	public ReportsManager(BrutusCore passedPlugin) {
		plugin = passedPlugin;
		file = Utils.createYamlFile(passedPlugin, "reports");
		config = Utils.createYamlConfig(passedPlugin, file, "reports");
		this.loadReports();
	}

	public void saveReports() {
		for (Entry<Integer, Report> entry : reports.entrySet()) {
			Report report = entry.getValue();
			config.set("reports." + report.getId() + ".description", report.getDescription());
			config.set("reports." + report.getId() + ".submitter", report.getSubmitter().toString());
			config.set("reports." + report.getId() + ".status", report.getStatus().toString());
			config.set("reports." + report.getId() + ".type", report.getType().toString());
			if (report.getAssignee() != null) {
				config.set("reports." + report.getId() + ".assignee", report.getAssignee().toString());
			}

			if (report instanceof TicketReport) {
				TicketReport ticket = (TicketReport) report;
				Utils.saveLocation(file, config, ticket.getLocation(), "reports." + report.getId() + ".location");
			}

			if (report instanceof PlayerReport) {
				PlayerReport playerReport = (PlayerReport) report;
				config.set("reports." + report.getId() + ".accused", playerReport.getAccused().toString());
			}

			if (report instanceof ChatReport) {
				ChatReport playerReport = (ChatReport) report;
				config.set("reports." + report.getId() + ".accused", playerReport.getAccused().toString());
			}

			Utils.saveFile(file, config);
		}
	}

	private void loadReports() {
		for (String i : config.getConfigurationSection("reports").getKeys(false)) {
			int reportId = Integer.parseInt(i);
			String description = config.getString("reports." + reportId + ".description");
			UUID submitter = UUID.fromString(config.getString("reports." + reportId + ".submitter"));
			Status status = Status.valueOf(config.getString("reports." + reportId + ".status"));
			Type type = Type.valueOf(config.getString("reports." + reportId + ".type"));
			UUID assignee = null;
			if (config.contains("reports." + reportId + ".assignee")) {
				assignee = UUID.fromString(config.getString("reports." + reportId + ".assignee"));
			}

			if (type.equals(Type.TICKET)) {
				Location location = Utils.getLocation(config, "reports." + reportId + ".location");
				TicketReport ticket = new TicketReport(submitter, description, location);
				ticket.setStatus(status);
				if (assignee != null) {
					ticket.setAssignee(assignee);
				}
				reports.put(reportId, ticket);
			} else if (type.equals(Type.PLAYER)) {
				UUID accused = UUID.fromString(config.getString("reports." + reportId + ".accused"));
				PlayerReport playerReport = new PlayerReport(submitter, description, accused);
				playerReport.setStatus(status);
				if (assignee != null) {
					playerReport.setAssignee(assignee);
				}
				reports.put(reportId, playerReport);
			} else if (type.equals(Type.CHAT)) {
				UUID accused = UUID.fromString(config.getString("reports." + reportId + ".accused"));
				ChatReport chatReport = new ChatReport(submitter, description, accused);
				chatReport.setStatus(status);
				if (assignee != null) {
					chatReport.setAssignee(assignee);
				}
				reports.put(reportId, chatReport);
			}
		}
	}

	public void createTicketReport(Player submitter, String description, Location location) {
		TicketReport report = new TicketReport(submitter.getUniqueId(), description, location);
		report.setStatus(Status.OPEN);
		reports.put(report.getId(), report);
		submitter.sendMessage("§aYou have created a ticket. Your Ticket ID is §b" + report.getId());
		submitter.sendMessage("§aPlease keep this id in a safe location.");
		for (Player online : Bukkit.getOnlinePlayers()) {
			if (online.hasPermission(Perms.REPORTS_NOTIFY)) {
				online.sendMessage(
						"§4§l[§c§lT§4§l] §c" + submitter.getName() + " has created a ticket with id " + report.getId());
			}
		}
	}

	public void createPlayerReport(Player submitter, String description, UUID accused) {
		PlayerReport report = new PlayerReport(submitter.getUniqueId(), description, accused);
		report.setStatus(Status.OPEN);
		reports.put(report.getId(), report);
		submitter.sendMessage("§aYou have created a report against " + Bukkit.getOfflinePlayer(accused).getName()
				+ ". Your Report ID is §b" + report.getId());
		submitter.sendMessage("§aPlease keep this id in a safe location.");
		for (Player online : Bukkit.getOnlinePlayers()) {
			if (online.hasPermission(Perms.REPORTS_NOTIFY)) {
				online.sendMessage(
						"§4§l[§c§lR§4§l] §c" + submitter.getName() + " has created a report with id " + report.getId());
			}
		}
	}

	public void createChatReport(Player submitter, String description, UUID accused) {
		ChatReport report = new ChatReport(submitter.getUniqueId(), description, accused);
		report.setStatus(Status.OPEN);
		reports.put(report.getId(), report);
		submitter.sendMessage("§aYou have created a report against " + Bukkit.getOfflinePlayer(accused).getName()
				+ ". Your Report ID is §b" + report.getId());
		submitter.sendMessage("§aPlease keep this id in a safe location.");
		for (Player online : Bukkit.getOnlinePlayers()) {
			if (online.hasPermission(Perms.REPORTS_NOTIFY)) {
				online.sendMessage(
						"§4§l[§c§lR§4§l] §c" + submitter.getName() + " has created a report with id " + report.getId());
			}
		}
	}

	public Report getReport(int reportId) {
		return reports.get(reportId);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(plugin.settings().getPlayerOnlyCommandMessage());
			return true;
		}

		String cmdName = cmd.getName();

		Player player = (Player) sender;

		if (!(args.length > 0)) {
			player.sendMessage("§cUsage: /<command> <arguments>".replace("<command>", cmdName));
			return true;
		}

		if (args[0].equalsIgnoreCase("list")) {
			if (player.hasPermission(Perms.REPORTS_VIEW_OTHER)) {
				if (args.length == 1) {
					return this.createPaginator(player, 1);
				} else if (args.length == 2) {
					int page = 1;
					try {
						page = Integer.parseInt(args[1]);
					} catch (NumberFormatException e) {
						player.sendMessage("§cThat is not a valid page number, try again.");
						return true;
					}
					Paginator<Report> paginator = paginators.get(player.getUniqueId());
					if (paginator == null) {
						return this.createPaginator(player, page);
					} else {
						paginator.display(player, page);
					}
				}

				return true;
			} else {
				player.sendMessage(plugin.settings().getNoPermissionMessage());
				return true;
			}
		} else if (args[0].equalsIgnoreCase("view")) {
			if (player.hasPermission(Perms.REPORTS_VIEW_OTHER)) {
				if (args.length == 2) {
					int id = 0;
					try {
						id = Integer.parseInt(args[1]);
					} catch (NumberFormatException e) {
						player.sendMessage("§cThat is not a valid number.");
						return true;
					}

					if (id == 0) {
						player.sendMessage("§cThat is not a valid id.");
						return true;
					}

					Report report = reports.get(id);
					if (report == null) {
						player.sendMessage("§cThat is not a valid report id.");
						return true;
					}

					if (report instanceof TicketReport) {
						TicketReport ticket = (TicketReport) report;
						player.sendMessage("§aViewing details for ticket §c" + ticket.getId());
						player.sendMessage("§2--> Description: §b" + ticket.getDescription());
						player.sendMessage(
								"§2--> Submitter: §b" + Bukkit.getOfflinePlayer(ticket.getSubmitter()).getName());
						if (ticket.getAssignee() != null) {
							player.sendMessage(
									"§2--> Assignee: §b" + Bukkit.getOfflinePlayer(ticket.getAssignee()).getName());
						}
						player.sendMessage("§2--> Status: §b" + ticket.getStatus().toString());
						player.sendMessage("§2--> This ticket has a location, teleport to it with §b/ticket tp <id>");
					} else if (report instanceof PlayerReport) {
						PlayerReport playerReport = (PlayerReport) report;
						player.sendMessage("§aViewing details for report §c" + playerReport.getId());
						player.sendMessage("§2--> Description: §b" + playerReport.getDescription());
						player.sendMessage(
								"§2--> Submitter: §b" + Bukkit.getOfflinePlayer(playerReport.getSubmitter()).getName());
						player.sendMessage(
								"§2--> Accused: §b" + Bukkit.getOfflinePlayer(playerReport.getAccused()).getName());
						if (playerReport.getAssignee() != null) {
							player.sendMessage("§2--> Assignee: §b"
									+ Bukkit.getOfflinePlayer(playerReport.getAssignee()).getName());
						}
						player.sendMessage("§2--> Status: §b" + playerReport.getStatus().toString());
					} else if (report instanceof ChatReport) {
						ChatReport chatReport = (ChatReport) report;
						player.sendMessage("§aViewing details for report §c" + chatReport.getId());
						player.sendMessage("§2--> Description: §b" + chatReport.getDescription());
						player.sendMessage(
								"§2--> Submitter: §b" + Bukkit.getOfflinePlayer(chatReport.getSubmitter()).getName());
						player.sendMessage(
								"§2--> Accused: §b" + Bukkit.getOfflinePlayer(chatReport.getAccused()).getName());
						if (chatReport.getAssignee() != null) {
							player.sendMessage(
									"§2--> Assignee: §b" + Bukkit.getOfflinePlayer(chatReport.getAssignee()).getName());
						}
						player.sendMessage("§2--> Status: §b" + chatReport.getStatus().toString());
					}
				} else {
					player.sendMessage("§cUsage: /<command> <arguments>".replace("<command>", cmdName));
					return true;
				}
			} else {
				player.sendMessage(plugin.settings().getNoPermissionMessage());
				return true;
			}
			return true;
		} else if (args[0].equalsIgnoreCase("tp") || args[0].equalsIgnoreCase("teleport")) {
			if (player.hasPermission(Perms.REPORTS_TELEPORT)) {
				if (args.length == 2) {
					Report report = reports.get(Integer.parseInt(args[1]));
					if (report == null) {
						player.sendMessage("§cThat is not a valid report.");
						return true;
					}
					if (!(report instanceof TicketReport)) {
						player.sendMessage("§cThat is not a TicketReport.");
						return true;
					}

					TicketReport ticket = (TicketReport) report;
					player.teleport(ticket.getLocation());
					player.sendMessage("§aYou have been teleported to the location of ticket §b" + ticket.getId());
					return true;
				} else {
					player.sendMessage("§cUsage: /<command> tp <id>".replace("<command>", cmdName));
					return true;
				}
			} else {
				player.sendMessage(plugin.settings().getNoPermissionMessage());
				return true;
			}
		} else if (args[0].equalsIgnoreCase("setstatus")) {
			if (player.hasPermission(Perms.REPORTS_CHANGE_STATUS)) {
				if (args.length == 3) {
					Report report = reports.get(Integer.parseInt(args[1]));
					if (report == null) {
						player.sendMessage("§cThat is not a valid report.");
						return true;
					}
					Status status = Status.valueOf(args[2].toUpperCase());
					if (status == null) {
						player.sendMessage("§cThat is not a valid status value.");
						return true;
					}

					report.setStatus(status);
					reports.put(report.getId(), report);
					player.sendMessage(
							"§aSet the status of the report §b" + report.getId() + " §ato §b" + args[2].toUpperCase());
					return true;
				} else {
					player.sendMessage(
							"§cUsage: /<command> setstatus <id> <OPEN|IN_REVIEW|CLOSED>".replace("<command>", cmdName));
					return true;
				}
			} else {
				player.sendMessage(plugin.settings().getNoPermissionMessage());
				return true;
			}
		} else if (args[0].equalsIgnoreCase("assign")) {
			if (player.hasPermission(Perms.REPORTS_ASSIGN_SELF) || player.hasPermission(Perms.REPORTS_ASSIGN_OTHER)) {
				if (args.length == 2) {
					Report report = reports.get(Integer.parseInt(args[1]));
					if (report == null) {
						player.sendMessage("§cThat is not a valid report id.");
						return true;
					}

					report.setAssignee(player.getUniqueId());
					reports.put(report.getId(), report);
					player.sendMessage("§aYou have assigned §b" + report.getId() + " §ato yourself.");
					return true;
				} else if (args.length == 3) {
					if (player.hasPermission(Perms.REPORTS_ASSIGN_OTHER)) {
						OfflinePlayer assigned = Bukkit.getOfflinePlayer(args[1]);
						if (assigned == null) {
							player.sendMessage("§cThe provided name is not a valid player.");
							return true;
						}

						Report report = reports.get(Integer.parseInt(args[2]));
						if (report == null) {
							player.sendMessage("§cThat is not a valid report id.");
							return true;
						}

						report.setAssignee(assigned.getUniqueId());
						reports.put(report.getId(), report);
						player.sendMessage("§aYou have assigned §b" + report.getId() + " §ato §b" + assigned.getName());
						return true;
					} else {
						player.sendMessage(plugin.settings().getNoPermissionMessage());
						return true;
					}
				}
			} else {
				player.sendMessage(plugin.settings().getNoPermissionMessage());
				return true;
			}
		}

		if (cmd.getName().equalsIgnoreCase("ticket") || cmd.getName().equalsIgnoreCase("tickets")) {
			if (args.length >= 1) {
				String description = "";
				for (int i = 0; i < args.length; i++) {
					description += args[i] + " ";
				}

				createTicketReport(player, description, player.getLocation());
				return true;
			}
			return true;
		} else if (cmd.getName().equalsIgnoreCase("reports") || cmd.getName().equalsIgnoreCase("report")) {
			if (args.length >= 1) {
				String description = "";
				for (int i = 1; i < args.length; i++) {
					description += args[i] + " ";
				}

				OfflinePlayer accused = Bukkit.getOfflinePlayer(args[0]);
				if (accused == null) {
					player.sendMessage("§cThat player is not a valid player.");
					return true;
				}

				createPlayerReport(player, description, accused.getUniqueId());
				return true;
			}
			return true;
		}
		return true;
	}

	private boolean createPaginator(Player player, Integer displaySlot) {
		TreeMap<Integer, Report> reports = new TreeMap<Integer, Report>();
		for (Entry<Integer, Report> entry : this.reports.entrySet()) {
			Report report = entry.getValue();
			if (report.getStatus().equals(Status.OPEN) || report.getStatus().equals(Status.IN_REVIEW)) {
				reports.put(entry.getKey(), entry.getValue());
			}
		}

		if (reports.isEmpty()) {
			player.sendMessage("§cAll reports are closed.");
			return true;
		}

		PaginatorFactory<Report> paginatorFactory = new PaginatorFactory<Report>();
		paginatorFactory.setMaxElements(7).setHeader("§6Reports page {pagenumber} out of {totalpages}")
		                                  .setFooter("§6Use /reports list {nextpage} to view the next page");
		
		for (Report report : reports.values()) {
			paginatorFactory.addElement(report, reports.values().size());
		}

		if (paginatorFactory.getPages().isEmpty()) {
			player.sendMessage("§cThere are no pages.");
			return true;
		} else {
			Paginator<Report> paginator = paginatorFactory.build();
			paginators.put(player.getUniqueId(), paginator);
			paginator.display(player, displaySlot);
		}
		return true;
	}
}