package net.brutuspvp.core.managers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.firestar311.fireutils.classes.Utils;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;

import net.brutuspvp.core.BrutusCore;
import net.brutuspvp.core.Perms;
import net.brutuspvp.core.enums.Status;
import net.brutuspvp.core.enums.Type;
import net.brutuspvp.core.model.CourtCase;
import net.brutuspvp.core.model.CourtRoom;
import net.brutuspvp.core.model.Evidence;
import net.brutuspvp.core.model.Trial;

@SuppressWarnings({ "deprecation" })
public class CourtManager implements Listener, CommandExecutor {

	private BrutusCore plugin;
	private File file;
	private FileConfiguration config;
	private ArrayList<UUID> adminMode = new ArrayList<UUID>();
	private HashMap<String, CourtRoom> courtRooms = new HashMap<String, CourtRoom>();
	private HashMap<Integer, CourtCase> courtCases = new HashMap<Integer, CourtCase>();
	private HashMap<Integer, Trial> trials = new HashMap<Integer, Trial>();
	private ArrayList<UUID> inTrial = new ArrayList<UUID>();

	public CourtManager(BrutusCore passedPlugin) {
		plugin = passedPlugin;
		file = Utils.createYamlFile(plugin, "courts");
		config = Utils.createYamlConfig(plugin, file, "courtrooms", "courtcases");
		plugin.registerListener(this);
		this.loadCourts();
	}

	public void saveCourts() {
		try {
			for (Entry<String, CourtRoom> entry : courtRooms.entrySet()) {
				CourtRoom courtRoom = entry.getValue();
				Utils.saveLocation(file, config, courtRoom.getJudgeLocation(),
						"courtrooms." + courtRoom.getName() + ".judgelocation");
				Utils.saveLocation(file, config, courtRoom.getStaffLocation(),
						"courtrooms." + courtRoom.getName() + ".stafflocation");
				Utils.saveLocation(file, config, courtRoom.getAccusedLocation(),
						"courtrooms." + courtRoom.getName() + ".accusedlocation");
			}

			for (Entry<Integer, CourtCase> entry : courtCases.entrySet()) {
				CourtCase courtCase = entry.getValue();
				config.set("courtcases." + courtCase.getCaseId() + ".judge", courtCase.getJudge().toString());
				config.set("courtcases." + courtCase.getCaseId() + ".accused", courtCase.getAccused().toString());
				ArrayList<String> staff = new ArrayList<String>();
				for (UUID uuid : courtCase.getStaff()) {
					staff.add(uuid.toString());
				}
				config.set("courtcases." + courtCase.getCaseId() + ".staff", staff);
				config.set("courtcases." + courtCase.getCaseId() + ".status", courtCase.getStatus().toString());
				if (courtCase.getOutcome() != null) {
					config.set("courtcases." + courtCase.getCaseId() + ".outcome", courtCase.getOutcome().toString());
				}
				for (Entry<String, Evidence> ev : courtCase.getStaffEvidence().entrySet()) {
					config.set("courtcases." + courtCase.getCaseId() + ".evidence." + ev.getValue().getName() + ".link",
							ev.getValue().getLink());
					config.set("courtcases." + courtCase.getCaseId() + ".evidence." + ev.getValue().getName() + ".type",
							ev.getValue().getType().toString());
				}
				for (Entry<String, Evidence> ev : courtCase.getAccusedEvidence().entrySet()) {
					config.set("courtcases." + courtCase.getCaseId() + ".evidence." + ev.getValue().getName() + ".link",
							ev.getValue().getLink());
					config.set("courtcases." + courtCase.getCaseId() + ".evidence." + ev.getValue().getName() + ".type",
							ev.getValue().getType());
				}
			}

			Utils.saveFile(file, config);
		} catch (Exception e) {
			BrutusCore.createBrutusError(e, "Save Courts");
		}
	}

	private void loadCourts() {
		try {
			for (String c : config.getConfigurationSection("courtrooms").getKeys(false)) {
				Location judgeLocation = Utils.getLocation(config, "courtrooms." + c + ".judgelocation");
				Location staffLocation = Utils.getLocation(config, "courtrooms." + c + ".stafflocation");
				Location accusedLocation = Utils.getLocation(config, "courtrooms." + c + ".accusedlocation");
				ProtectedCuboidRegion region = (ProtectedCuboidRegion) plugin.getWorldGuard()
						.getRegionManager(judgeLocation.getWorld()).getRegion(c);
				CourtRoom room = new CourtRoom(c, region);
				room.setJudgeLocation(judgeLocation);
				room.setStaffLocation(staffLocation);
				room.setAccusedLocation(accusedLocation);
				courtRooms.put(c, room);
			}

			for (String c : config.getConfigurationSection("courtcases").getKeys(false)) {
				Integer cc = Integer.parseInt(c);
				UUID judge = UUID.fromString(config.getString("courtcases." + cc + ".judge"));
				UUID accused = UUID.fromString(config.getString("courtcases." + cc + ".accused"));
				CourtCase courtCase = new CourtCase(judge, accused, cc);
				for (String u : config.getStringList("courtcases." + cc + ".staff")) {
					courtCase.addStaffMember(UUID.fromString(u));
				}

				for (String e : config.getConfigurationSection("courtcases." + cc + ".evidence").getKeys(false)) {
					String link = config.getString("courtcases." + cc + ".evidence." + e + ".link");
					Type type = Type
							.valueOf(config.getString("courtcases." + cc + ".evidence." + e + ".type"));
					Evidence evidence = new Evidence(e, type, link);
					if (type.equals(Type.ACCUSED)) {
						courtCase.addAccusedEvidence(evidence);
					} else if (type.equals(Type.STAFF)) {
						courtCase.addStaffEvidence(evidence);
					}

					if (config.contains("courtcases." + cc + ".status")) {
						courtCase.setStatus(CourtCase.Status.valueOf(config.getString("courtcases." + cc + ".status")));
					}

					if (config.contains("courtcases." + cc + ".outcome")) {
						courtCase.setOutcome(
								CourtCase.Outcome.valueOf(config.getString("courtcases." + cc + ".outcome")));
					}
				}

				courtCases.put(cc, courtCase);
			}
		} catch (Exception e) {
			BrutusCore.createBrutusError(e, "Load Courts");
		}
	}

	public boolean inTrial(UUID uuid) {
		return inTrial.contains(uuid);
	}

	public boolean inTrial(Player player) {
		return inTrial.contains(player.getUniqueId());
	}

	public HashMap<Integer, Trial> getTrials() {
		return new HashMap<Integer, Trial>(trials);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		try {
			if (!(sender instanceof Player)) {
				sender.sendMessage(plugin.settings().getPlayerOnlyCommandMessage());
				return true;
			}

			Player player = (Player) sender;

			if (args.length < 1) {
				sender.sendMessage("§cUsage: /courts (subcommand) (additional arguments)");
				return true;
			}

			if (args[0].equalsIgnoreCase("adminmode")) {
				if (player.hasPermission(Perms.COURTS_ADMIN)) {
					if (adminMode.contains(player.getUniqueId())) {
						adminMode.remove(player.getUniqueId());
						player.sendMessage("§cYou have disabled Courts Admin Mode.");
					} else {
						adminMode.add(player.getUniqueId());
						player.sendMessage("§aYou have enabled Courts Admin Mode.");
					}
				} else {
					player.sendMessage(plugin.settings().getNoPermissionMessage());
				}
			} else if (args[0].equalsIgnoreCase("create")) {
				if (args.length == 2) {
					if (!adminMode.contains(player.getUniqueId())) {
						player.sendMessage("§cYou must be in Courts Admin Mode.");
						return true;
					}

					Selection sel = plugin.getWorldEdit().getSelection(player);

					if (sel == null) {
						player.sendMessage("§cYou must make a WorldEdit Selection.");
						return true;
					}

					if (!(sel instanceof CuboidSelection)) {
						player.sendMessage("§cYou must make a Cuboid Selection.");
						return true;
					}

					CuboidSelection selection = (CuboidSelection) sel;

					String name = args[1];

					ProtectedCuboidRegion region = new ProtectedCuboidRegion(name,
							new BlockVector(selection.getNativeMaximumPoint()),
							new BlockVector(selection.getNativeMinimumPoint()));
					plugin.getWorldGuard().getRegionManager(player.getWorld()).addRegion(region);

					CourtRoom courtRoom = new CourtRoom(name, region);
					courtRooms.put(name, courtRoom);

					player.sendMessage("§aYou have successully created the CourtRoom §b" + name);
				} else {
					player.sendMessage("§cUsage: /courts (subcommand) (additional arguments)");
					return true;
				}
			} else if (args[0].equalsIgnoreCase("set")) {
				if (args.length == 3) {
					if (!adminMode.contains(player.getUniqueId())) {
						player.sendMessage("§cYou must be in Courts Admin Mode.");
						return true;
					}
					CourtRoom room = courtRooms.get(args[1]);
					if (room == null) {
						player.sendMessage("§cUnknown CourtRoom §b" + args[2]);
						return true;
					}

					if (!room.getRegion().contains(player.getLocation().getBlockX(), player.getLocation().getBlockY(),
							player.getLocation().getBlockZ())) {
						player.sendMessage("§cYou are not within the Court Room's boundaries.");
						return true;
					}

					if (args[2].equalsIgnoreCase("judgelocation") || args[2].equalsIgnoreCase("jl")) {
						room.setJudgeLocation(player.getLocation());
						courtRooms.put(room.getName(), room);
						player.sendMessage("§aSet the Judge Location of §b" + room.getName() + " §ato your location.");
						return true;
					} else if (args[2].equalsIgnoreCase("accusedlocation") || args[2].equalsIgnoreCase("al")) {
						room.setAccusedLocation(player.getLocation());
						courtRooms.put(room.getName(), room);
						player.sendMessage("§aSet the Accused Location of §b" + room.getName() + " §ato your location.");
						return true;
					} else if (args[2].equalsIgnoreCase("stafflocation") || args[2].equalsIgnoreCase("sl")) {
						room.setStaffLocation(player.getLocation());
						courtRooms.put(room.getName(), room);
						player.sendMessage("§aSet the Staff Location of §b" + room.getName() + " §ato your location.");
						return true;
					} else {
						player.sendMessage("§cUnkown Setting §b" + args[2]);
					}
				} else {
					player.sendMessage("§cUsage: /courts (subcommand) (additional arguments)");
					return true;
				}
			} else if (args[0].equalsIgnoreCase("remove")) {
				if (args.length == 2) {
					if (!adminMode.contains(player.getUniqueId())) {
						player.sendMessage("§cYou must be in Courts Admin Mode.");
						return true;
					}
					CourtRoom room = courtRooms.get(args[1]);
					if (room == null) {
						player.sendMessage("§cThat Court Room does not exist §b" + args[1]);
						return true;
					}

					courtRooms.remove(room.getName());
					player.sendMessage("§aSuccessfully removed Court Room §b" + args[1]);
					return true;
				} else {
					player.sendMessage("§cUsage: /courts (subcommand) (additional arguments)");
					return true;
				}
			} else if (args[0].equalsIgnoreCase("new")) {
				if (args.length > 1) {
					if (args[1].equalsIgnoreCase("case")) {
						if (args.length == 3) {
							OfflinePlayer judge = Bukkit.getOfflinePlayer(args[2]);
							OfflinePlayer accused = Bukkit
									.getOfflinePlayer(plugin.punishments().getNextJailedUser().getPlayer());
							if (judge == null) {
								player.sendMessage("§cThe provided name for judge is not valid.");
								return true;
							}

							if (!BrutusCore.getJudges().contains(judge.getUniqueId())) {
								player.sendMessage("§cThe player provided for the judge is not approved.");
								return true;
							}

							CourtCase courtCase = new CourtCase(judge.getUniqueId(), accused.getUniqueId());
							courtCase.setStatus(CourtCase.Status.GATHERING);
							courtCases.put(courtCase.getCaseId(), courtCase);
							player.sendMessage("");
							player.sendMessage("§dCreated a court case with the following information...");
							player.sendMessage("§2-> §6Judge: §b" + judge.getName());
							player.sendMessage("§2--> §6Accused: §b" + accused.getName());
							player.sendMessage("§2---> §6Case ID: §b" + courtCase.getCaseId());
							player.sendMessage("§2----> §aAdd at least one staff member to be a part of the case.");
							player.sendMessage("§2-----> §aGive the case id to the accused and staff of the case.");
							player.sendMessage("");
						} else if (args.length == 4) {
							player.sendMessage("§aStarting creation of the case...");
							player.sendMessage("§aRetrieving the Judge Info from Mojang...");
							OfflinePlayer judge = Bukkit.getOfflinePlayer(args[2]);
							player.sendMessage("§aJudge Info retrieved, getting Accused Info from Mojang...");
							OfflinePlayer accused = Bukkit.getOfflinePlayer(args[3]);
							player.sendMessage("§aAccused info retrieved, setting Default Case Information.");
							if (judge == null) {
								player.sendMessage("§cThe provided name for the judge is not valid.");
								return true;
							}

							if (!BrutusCore.getJudges().contains(judge.getUniqueId())) {
								player.sendMessage("§cThe player provided for the judge is not approved.");
								return true;
							}

							if (accused == null) {
								player.sendMessage("§cThe provided name for accused is not valid.");
								return true;
							}

							if (!plugin.punishments().isJailed(accused.getUniqueId())) {
								player.sendMessage("§cThe provided accused player is currently not in jail.");
								player.sendMessage(
										"§cConsider not providing a name to automatically get the next jailed player.");
								return true;
							}

							CourtCase courtCase = new CourtCase(judge.getUniqueId(), accused.getUniqueId());

							courtCase.setStatus(CourtCase.Status.GATHERING);
							courtCases.put(courtCase.getCaseId(), courtCase);
							player.sendMessage("");
							player.sendMessage("§dCreated a court case with the following information...");
							player.sendMessage("§2-> §6Judge: §b" + judge.getName());
							player.sendMessage("§2--> §6Accused: §b" + accused.getName());
							player.sendMessage("§2---> §6Case ID: §b" + courtCase.getCaseId());
							player.sendMessage("§2----> §aAdd at least one staff member to be a part of the case.");
							player.sendMessage("§2-----> §aGive the case id to the accused and staff of the case.");
							player.sendMessage("");
						} else {
							player.sendMessage("§cUsage: /courts (subcommand) (additional arguments)");
							return true;
						}
					}
				} else {
					player.sendMessage("§cUsage: /courts (subcommand) (additional arguments)");
					return true;
				}
			} else if (args[0].equalsIgnoreCase("case")) {
				if (args.length == 1) {
					if (args[1].equalsIgnoreCase("list")) {
						player.sendMessage("§cThis feature is a Work In Progress.");
						return true;
					}
				} else if (args.length > 1) {
					CourtCase courtCase = courtCases.get(Integer.parseInt(args[1]));
					if (courtCase == null) {
						player.sendMessage("§cThat is not a valid case id. Please contact the Judge or recheck the id.");
						return true;
					}

					if (args.length > 3) {
						if (courtCase.isStaff(player.getUniqueId())) {
							courtCase.addStaffEvidence(new Evidence(args[2], Type.STAFF, args[3]));
							player.sendMessage("§aAdded your evidence to the staff evidence list.");
							return true;
						} else if (courtCase.isAccused(player.getUniqueId())) {
							courtCase.addAccusedEvidence(new Evidence(args[2], Type.ACCUSED, args[3]));
							player.sendMessage("§aAdded your evidence to the accused evidence list.");
							return true;
						} else if (courtCase.isJudge(player.getUniqueId())) {
							OfflinePlayer staff = Bukkit.getOfflinePlayer(args[2]);
							if (staff == null) {
								player.sendMessage("§cThe provided name for a staff member is not valid.");
								return true;
							}
							courtCase.addStaffMember(staff.getUniqueId());
							player.sendMessage("§aSuccessfully added §b" + staff.getName() + " §ato the case §b"
									+ courtCase.getCaseId());
							return true;
						} else {
							player.sendMessage("§cYou are not a member of that case. You cannot add anything to it.");
							return true;
						}
					}
				}
			} else if (args[0].equalsIgnoreCase("start")) {
				if (args.length == 3) {
					CourtCase courtCase = courtCases.get(Integer.parseInt(args[1]));
					if (courtCase == null) {
						player.sendMessage("§cThat is not a valid case id.");
						return true;
					}

					CourtRoom room = courtRooms.get(args[2]);
					if (room == null) {
						player.sendMessage("§cThat is not a valid court room.");
						return true;
					}

					Player judge = Bukkit.getPlayer(courtCase.getJudge());
					if (judge == null) {
						player.sendMessage("§cThe judge is not online.");
						return true;
					}

					if (player.getUniqueId() != judge.getUniqueId()) {
						player.sendMessage("§cOnly the judge can start the trial.");
						return true;
					}

					Player accused = Bukkit.getPlayer(courtCase.getAccused());

					if (accused == null) {
						player.sendMessage("§cThe accused is not online.");
						return true;
					}

					ArrayList<Player> staff = new ArrayList<Player>();
					for (UUID u : courtCase.getStaff()) {
						Player s = Bukkit.getPlayer(u);
						if (s != null) {
							staff.add(s);
						}
					}

					judge.sendMessage("§9You have started the creation of the trial. Teleporting in 15 seconds.");
					accused.sendMessage(
							"§9The judge has started the creation of your trial. You will be teleported in 15 seconds.");
					for (Player s : staff) {
						s.sendMessage("§9The judge has started the creation of the trial §b" + courtCase.getCaseId());
						s.sendMessage("§9You will be automatically teleported in 15 seconds.");
					}

					player.sendMessage("§aCreated the trial. Setting it up...");
					Trial trial = new Trial(courtCase.getCaseId(), courtCase, room);
					trial.setStatus(Status.WAITING);
					trial.setJudge(judge);
					trial.setAccused(accused);
					for (Player p : staff) {
						trial.addStaff(p);
					}

					for (Player p : trial.getParticipants()) {
						inTrial.add(p.getUniqueId());
					}

					new BukkitRunnable() {
						public void run() {
							trial.setStatus(Status.SETTING_UP);
							player.sendMessage("§aTeleporting the Judge.");
							judge.teleport(room.getJudgeLocation());

							player.sendMessage("§aTeleporting the Accused.");
							accused.teleport(room.getAccusedLocation());

							player.sendMessage("§aTeleporting additional staff.");
							for (Player p : staff) {
								p.teleport(room.getStaffLocation());
							}
							trials.put(trial.getId(), trial);
						}
					}.runTaskLater(plugin, 20 * 15);
				}
			} else if (args[0].equalsIgnoreCase("trial")) {
				if (!inTrial(player)) {
					player.sendMessage("§cYou are not in a trial.");
					return true;
				}

				Trial trial = null;
				for (Trial t : trials.values()) {
					if (t.getParticipants().contains(player)) {
						trial = t;
						break;
					}
				}

				if (trial == null) {
					player.sendMessage("§cCould not detect your trial.");
					return true;
				}

				if (args[1].equalsIgnoreCase("evidence")) {
					if (player.getUniqueId().equals(trial.getAccused().getUniqueId())) {
						Evidence evidence = trial.getCourtCase().getAccusedEvidence().get(args[2]);
						if (evidence == null) {
							player.sendMessage("§cThat is not a valid evidence name.");
							return true;
						}

						for (Player part : trial.getParticipants()) {
							part.sendMessage("§e§l[§6§lTRIAL§e§l] §6Accused Evidence §b" + evidence.getName());
							part.sendMessage("§e§l[§6§lTRIAL§e§l] §6Link to evidence §b" + evidence.getLink());
						}
						return true;
					} else if (player.getUniqueId().equals(trial.getJudge().getUniqueId())) {
						player.sendMessage("§aThe judge cannot call upon any evidence.");
						return true;
					} else if (trial.getStaff().contains(player)) {
						Evidence evidence = trial.getCourtCase().getStaffEvidence().get(args[2]);
						if (evidence == null) {
							player.sendMessage("§cThat is not a valid evidence name.");
							return true;
						}

						for (Player part : trial.getParticipants()) {
							part.sendMessage("§e§l[§6§lTRIAL§e§l] §6Staff Evidence §b" + evidence.getName());
							part.sendMessage("§e§l[§6§lTRIAL§e§l] §6Link to evidence §b" + evidence.getLink());
						}
						return true;
					}
				} else if (args[1].equalsIgnoreCase("decision")) {
					if (player.getUniqueId().equals(trial.getJudge().getUniqueId())) {
						if (args[2].equalsIgnoreCase("innocent")) {
							trial.getCourtCase().setOutcome(CourtCase.Outcome.FAVOR_ACCUSED);
							for (Player part : trial.getParticipants()) {
								part.sendMessage("§e§l[§6§lTRIAL§e§l] §6The accused has been ruled §2innocent");
							}
							plugin.punishments().unjailPlayer(player, trial.getAccused().getUniqueId());
						} else if (args[2].equalsIgnoreCase("guilty")) {
							trial.getCourtCase().setOutcome(CourtCase.Outcome.FAVOR_STAFF);
							player.sendMessage("§aMake sure you issue the punishment.");
							for (Player part : trial.getParticipants()) {
								part.sendMessage("§e§l[§6§lTRIAL§e§l] §6The accused has been ruled §cguilty");
							}
						}
						trials.remove(trial.getId());
						for (Player part : trial.getParticipants()) {
							inTrial.remove(part.getUniqueId());
						}
						trial.getCourtCase().setStatus(CourtCase.Status.ARCHIVED);
						saveCourts();
					}
				} else {
					player.sendMessage("§cOnly the judge can make a decision.");
				}
			}
		} catch (Exception e) {
			BrutusCore.createBrutusError(sender, e, "CourtManager onCommand()");
		}
		return true;
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		try {
			Player player = e.getPlayer();
			for (Trial trial : trials.values()) {
				if (trial.getAccused().getUniqueId().equals(player.getUniqueId())) {
					if (e.getFrom().getX() != e.getTo().getX() || e.getFrom().getY() != e.getTo().getY()
							|| e.getFrom().getZ() != e.getTo().getZ()) {
						e.setCancelled(true);
						player.sendMessage("§cThe accused is not allowed to move during a trial.");
						return;
					}
				}
			}
		} catch (Exception ex) {
			BrutusCore.createBrutusError(ex, "CourtManager PlayerMoveEvent");
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		try {
			Player player = e.getPlayer();
			for (Trial trial : trials.values()) {
				for (Player part : trial.getParticipants()) {
					if (part.getUniqueId().equals(player.getUniqueId())) {
						e.setCancelled(true);
						return;
					}
				}
			}
		} catch (Exception ex) {
			BrutusCore.createBrutusError(ex, "CourtManager BlockPlaceEvent");
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		try {
			Player player = e.getPlayer();
			for (Trial trial : trials.values()) {
				for (Player part : trial.getParticipants()) {
					if (part.getUniqueId().equals(player.getUniqueId())) {
						e.setCancelled(true);
						return;
					}
				}
			}
		} catch (Exception ex) {
			BrutusCore.createBrutusError(ex, "CourtManager BlockBreakEvent");
		}
	}
}