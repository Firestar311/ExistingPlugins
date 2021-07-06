package net.brutuspvp.core.managers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.EnderChest;
import org.bukkit.block.Furnace;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.firestar311.fireutils.classes.Utils;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import net.brutuspvp.core.BrutusCore;
import net.brutuspvp.core.Perms;
import net.brutuspvp.core.enums.Toggle;
import net.brutuspvp.core.enums.Type;
import net.brutuspvp.core.model.Apartment;
import net.brutuspvp.core.model.BrutusUser;
import net.brutuspvp.core.model.House;
import net.brutuspvp.core.model.Mansion;
import net.brutuspvp.core.model.abstraction.LivingSpace;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.EconomyResponse;

@SuppressWarnings("unused")
public class LivingSpaceManager implements Listener, CommandExecutor {

	private BrutusCore plugin;
	private File file;
	private FileConfiguration config;

	private HashMap<String, LivingSpace> livingSpaces = new HashMap<String, LivingSpace>();

	public LivingSpaceManager(BrutusCore plugin) {
		this.plugin = plugin;
		plugin.registerListener(this);
		file = Utils.createYamlFile(plugin, "livingspaces");
		config = Utils.createYamlConfig(plugin, file, "livingspaces");
		this.loadLivingSpaces();
		new BukkitRunnable() {
			public void run() {
				for (Entry<String, LivingSpace> entry : livingSpaces.entrySet()) {
					if ((System.currentTimeMillis()) >= entry.getValue().getExpires()) {
						LivingSpace living = entry.getValue();
						Player player = Bukkit.getPlayer(living.getOwner());
						living.setOwner(null);
						living.setAvailable(true);
						living.setExpires(0);
						living.updateSigns();
						if (player != null) {
							player.sendMessage("§cYour LivingSpace has expired!");
						}
						livingSpaces.put(living.getName(), living);
					}
				}
			}
		}.runTaskTimer(plugin, 0L, 20 * 60);
	}

	public void saveLivingSpaces() {
		try {
			for (Entry<String, LivingSpace> entry : livingSpaces.entrySet()) {
				LivingSpace space = entry.getValue();
				Type type = space.getType();
				String typeString = type.toString().toLowerCase();
				String name = space.getName().replace("", "");
				String path = "livingspaces." + type.toString().toLowerCase() + "." + space.getName() + ".";
				config.set(path + "world", space.getWorld().getName());
				if (space.getOwner() == null) {
					config.set(path + "owner", "");
				} else {
					config.set(path + "owner", space.getOwner().toString());
				}
				config.set(path + "members", space.getMembers());
				config.set(path + "price", space.getPrice());
				config.set(path + "available", space.isAvailable());
				config.set(path + "expires", space.getExpires());
				for (int s = 0; s < space.getSigns().size(); s++) {
					Sign sign = space.getSigns().get(s);
					Utils.saveLocation(file, config, sign.getLocation(), path + "signs." + s);
				}

				if (space instanceof Apartment) {
					// TODO More things will be added to the apartment class in
					// the future. Need this as a placeholder for now.
				} else if (space instanceof House) {
					House house = (House) space;
					if (house.getCraftingTable() != null) {
						Utils.saveLocation(file, config, house.getCraftingTable(), path + "craftingtable");
					}
					if (house.getFurnace() != null) {
						Utils.saveLocation(file, config, house.getFurnace().getLocation(), path + "furnace");
					}
					for (int c = 0; c < house.getChests().size(); c++) {
						Chest chest = house.getChests().get(c);
						Utils.saveLocation(file, config, chest.getLocation(), path + "chests." + c);
					}
				} else if (space instanceof Mansion) {
					Mansion mansion = (Mansion) space;
					if (mansion.getCraftingTable() != null) {
						Utils.saveLocation(file, config, mansion.getCraftingTable(), path + "craftingtable");
					}
					if (mansion.getFurnace() != null) {
						Utils.saveLocation(file, config, mansion.getFurnace().getLocation(), path + "furnace");
					}
					for (int c = 0; c < mansion.getChests().size(); c++) {
						Chest chest = mansion.getChests().get(c);
						Utils.saveLocation(file, config, chest.getLocation(), path + "chests." + c);
					}
					if (mansion.getEnderChest() != null) {
						Utils.saveLocation(file, config, mansion.getEnderChest().getLocation(), path + "enderchest");
					}
				}
				Utils.saveFile(file, config);
			}
		} catch (Exception e) {
			BrutusCore.createBrutusError(e, "Save Living Spaces");
		}
	}

	private void loadLivingSpaces() {
		try {
			if (!this.livingSpaces.isEmpty()) {
				this.livingSpaces.clear();
			}
			if (!config.contains("livingspaces")) {
				config.createSection("livingspaces");
			}
			for (String lt : config.getConfigurationSection("livingspaces").getKeys(false)) {
				Type type = Type.valueOf(lt.toUpperCase());
				for (String name : config.getConfigurationSection("livingspaces." + lt).getKeys(false)) {
					String path = "livingspaces." + lt + "." + name + ".";
					World world = Bukkit.getWorld(config.getString(path + "world"));
					ProtectedCuboidRegion region = (ProtectedCuboidRegion) plugin.getWorldGuard()
							.getRegionManager(world).getRegion(name);
					String owner = config.getString(path + "owner");
					ArrayList<String> members = (ArrayList<String>) config.getStringList(path + "members");
					int price = config.getInt(path + "price");
					boolean available = config.getBoolean(path + "available");
					long expires = config.getLong(path + "expires");
					ArrayList<Sign> signs = new ArrayList<Sign>();
					for (String s : config.getConfigurationSection(path + "signs").getKeys(false)) {
						Location loc = Utils.getLocation(config, path + "signs." + s);
						if (loc.getBlock().getState() instanceof Sign) {
							Sign sign = (Sign) loc.getBlock().getState();
							signs.add(sign);
						} else {
							loc.getBlock().setType(Material.WALL_SIGN);
							;
							loc.getBlock().getState().update();
							Sign sign = (Sign) loc.getBlock().getState();
							signs.add(sign);
						}
					}
					if (type == Type.APARTMENT) {
						Apartment apartment = new Apartment(name, world, region, price);
						apartment.setAvailable(available);
						apartment.setExpires(expires);
						if (owner != null && owner != "") {
							try {
								apartment.setOwner(UUID.fromString(owner));
							} catch (IllegalArgumentException ie) {
								apartment.setOwner(null);
							}
						}
						members.forEach(member -> apartment.addMember(member));
						signs.forEach(sign -> apartment.addSign(sign));
						apartment.updateSigns();
						this.livingSpaces.put(name, apartment);
					} else if (type == Type.HOUSE) {
						Location craftingTable = Utils.getLocation(config, path + "craftingtable");
						Location bed = Utils.getLocation(config, path + "bed");
						Location furnaceLocation = Utils.getLocation(config, path + "furnace");
						Furnace furnace = null;
						if (furnaceLocation.getBlock().getState() instanceof Furnace) {
							furnace = (Furnace) furnaceLocation.getBlock().getState();
						} else {
							furnaceLocation.getBlock().setType(Material.FURNACE);
							furnace = (Furnace) furnaceLocation.getBlock().getState();
						}

						ArrayList<Chest> chests = new ArrayList<Chest>();
						if (config.getConfigurationSection(path + "chests") != null) {
							for (String c : config.getConfigurationSection(path + "chests").getKeys(false)) {
								Location cLoc = Utils.getLocation(config, path + "chests." + c);
								Chest chest = null;
								if (cLoc.getBlock().getState() instanceof Chest) {
									chest = (Chest) cLoc.getBlock().getState();
								} else {
									cLoc.getBlock().setType(Material.CHEST);
									chest = (Chest) cLoc.getBlock().getState();
								}
								chests.add(chest);
							}
						}

						House house = new House(region, world, name, price);
						house.setAvailable(available);
						house.setExpires(expires);
						if (owner != null && owner != "") {
							try {
								house.setOwner(UUID.fromString(owner));
							} catch (IllegalArgumentException ie) {
								house.setOwner(null);
							}
						}
						house.setCraftingTable(craftingTable);
						house.setFurnace(furnace);
						members.forEach(member -> house.addMember(member));
						signs.forEach(sign -> house.addSign(sign));
						chests.forEach(chest -> house.addChest(chest));
						house.updateSigns();
						this.livingSpaces.put(name, house);
					} else if (type == Type.MANSION) {
						Location craftingTable = Utils.getLocation(config, path + "craftingtable");
						Location bed = Utils.getLocation(config, path + "bed");
						Location furnaceLocation = Utils.getLocation(config, path + "furnace");
						Furnace furnace = null;
						if (furnaceLocation.getBlock().getState() instanceof Furnace) {
							furnace = (Furnace) furnaceLocation.getBlock().getState();
						} else {
							furnaceLocation.getBlock().setType(Material.FURNACE);
							furnace = (Furnace) furnaceLocation.getBlock().getState();
						}
						Location echestLoc = Utils.getLocation(config, path + "enderchest");
						EnderChest echest = null;
						if (echestLoc.getBlock().getState() instanceof Furnace) {
							echest = (EnderChest) echestLoc.getBlock().getState();
						} else {
							echestLoc.getBlock().setType(Material.ENDER_CHEST);
							echest = (EnderChest) echestLoc.getBlock().getState();
						}

						ArrayList<Chest> chests = new ArrayList<Chest>();
						if (config.getConfigurationSection(path + "chests") != null) {
							for (String c : config.getConfigurationSection(path + "chests").getKeys(false)) {
								Location cLoc = Utils.getLocation(config, path + "chests." + c);
								Chest chest = null;
								if (cLoc.getBlock().getState() instanceof Chest) {
									chest = (Chest) cLoc.getBlock().getState();
								} else {
									cLoc.getBlock().setType(Material.CHEST);
									chest = (Chest) cLoc.getBlock().getState();
								}
								chests.add(chest);
							}
						}

						Mansion mansion = new Mansion(region, world, name, price);
						mansion.setAvailable(available);
						mansion.setExpires(expires);
						if (owner != null && owner != "") {
							try {
								mansion.setOwner(UUID.fromString(owner));
							} catch (IllegalArgumentException ie) {
								mansion.setOwner(null);
							}
						}
						mansion.setCraftingTable(craftingTable);
						mansion.setFurnace(furnace);
						members.forEach(member -> mansion.addMember(member));
						signs.forEach(sign -> mansion.addSign(sign));
						chests.forEach(chest -> mansion.addChest(chest));
						mansion.updateSigns();
						this.livingSpaces.put(name, mansion);
					}
				}
			}
		} catch (Exception e) {
			BrutusCore.createBrutusError(e, "Load Living Spaces");
		}
	}

	private CuboidSelection checkSelection(Player player) {
		Selection sel = plugin.getWorldEdit().getSelection(player);
		if (sel == null) {
			player.sendMessage("§cYou must make a WorldEdit Selection.");
			return null;
		}

		if (!(sel instanceof CuboidSelection)) {
			player.sendMessage("§cThe WorldEdit selection must be cuboid.");
			return null;
		}

		return (CuboidSelection) sel;
	}

	private ProtectedCuboidRegion createRegion(CuboidSelection selection, String name) {
		BlockVector max = new BlockVector(selection.getNativeMaximumPoint());
		BlockVector min = new BlockVector(selection.getNativeMinimumPoint());
		DefaultDomain owners = new DefaultDomain();
		ProtectedCuboidRegion region = new ProtectedCuboidRegion(name, max, min);
		plugin.getWorldGuard().getRegionManager(selection.getWorld()).addRegion(region);
		region.setOwners(owners);
		return region;
	}

	private Integer parsePrice(Player player, String toParse) {
		Integer integer = null;
		try {
			integer = Integer.parseInt(toParse);
		} catch (NumberFormatException e) {
			player.sendMessage("§cThat is not a valid number.");
		}
		return integer;
	}

	public boolean createApartment(Player player, Command cmd, String[] args) {
		if (args.length > 1) {
			CuboidSelection selection = this.checkSelection(player);

			if (!(args.length > 2)) {
				player.sendMessage(
						"§cUsage: /<command> create apartment <options...>".replace("<command>", cmd.getName()));
				return true;
			}
			player.sendMessage("§aStarting creation of apartment §b" + args[2]);
			String name = args[2];
			player.sendMessage("§aRegistering WorldGuard region for §b" + name);
			ProtectedCuboidRegion region = this.createRegion(selection, name);

			Integer price = null;
			if (args.length > 3) {
				price = this.parsePrice(player, args[3]);
			}
			if (price == null) {
				price = 0;
				return true;
			}

			Apartment apartment = new Apartment(name, player.getWorld(), region, price);

			player.sendMessage("§aSearching for signs for the apartment §b" + name);
			int counter = 0;
			int signs = 0;
			Location minPoint = selection.getMinimumPoint();
			Location maxPoint = selection.getMaximumPoint();
			for (Block block : Utils.getBlocks(player.getWorld(), minPoint, maxPoint)) {
				if (block.getState() instanceof Sign) {
					apartment.addSign((Sign) block.getState());
					signs++;
				}
				counter++;
			}
			long expires = System.currentTimeMillis() + (plugin.settings().getRentDays() * 24 * 60 * 60 * 1000);
			apartment.setExpires(expires);
			player.sendMessage(
					"§aSearch for signs completed. Found §e" + signs + " §asign(s) in §d" + counter + " §ablocks.");
			apartment.updateSigns();
			this.livingSpaces.put(name, apartment);
			player.sendMessage("§aSuccessfully created apartment §b" + name);
			return true;
		}
		return true;
	}

	public boolean createHouse(Player player, Command cmd, String[] args) {
		if (args.length > 1) {
			CuboidSelection selection = this.checkSelection(player);

			if (!(args.length > 2)) {
				player.sendMessage("§cUsage: /<command> create house <options...>".replace("<command>", cmd.getName()));
				return true;
			}

			player.sendMessage("§aStarting creation of house §b" + args[2]);
			String name = args[2];
			ProtectedCuboidRegion region = this.createRegion(selection, name);
			player.sendMessage("§aRegistering WorldGuard region for §b" + name);

			Integer price = null;
			if (args.length > 3) {
				price = this.parsePrice(player, args[3]);
			}
			if (price == null) {
				price = 0;
				return true;
			}

			House house = new House(region, player.getWorld(), name, price);

			player.sendMessage("§aSearching for signs, chests, crafting tables and furnaces for the house §b" + name);
			int counter = 0;
			int signs = 0;
			int chests = 0;
			Location minPoint = selection.getMinimumPoint();
			Location maxPoint = selection.getMaximumPoint();
			for (Block block : Utils.getBlocks(player.getWorld(), minPoint, maxPoint)) {
				if (block.getState() instanceof Sign) {
					house.addSign((Sign) block.getState());
					signs++;
				}
				if (block.getType().equals(Material.WORKBENCH)) {
					house.setCraftingTable(block.getLocation());
				}
				if (block.getType().equals(Material.CHEST)) {
					house.addChest((Chest) block.getState());
					chests++;
				}
				if (block.getType().equals(Material.FURNACE)) {
					house.setFurnace((Furnace) block.getState());
				}
				counter++;
			}
			player.sendMessage("§aThe search for house components completed. §d" + counter + " §ablocks searched.");
			player.sendMessage("§e" + signs + " sign(s) found.");
			player.sendMessage("§6" + chests + " chest(s) found.");
			if (house.getCraftingTable() != null) {
				player.sendMessage("§9A crafting table was found.");
			}

			if (house.getFurnace() != null) {
				player.sendMessage("§7A Furnace was found.");
			}

			long expires = System.currentTimeMillis() + (plugin.settings().getRentDays() * 24 * 60 * 60 * 1000);
			house.setExpires(expires);

			house.updateSigns();

			this.livingSpaces.put(name, house);
			player.sendMessage("§aSuccessfully created house §b" + name);

			return true;
		}
		return true;
	}

	public boolean createMansion(Player player, Command cmd, String[] args) {
		if (args.length > 1) {

			CuboidSelection selection = this.checkSelection(player);

			if (!(args.length > 2)) {
				player.sendMessage(
						"§cUsage: /<command> create mansion <options...>".replace("<command>", cmd.getName()));
				return true;
			}

			player.sendMessage("§aStarting creation of mansion §b" + args[2]);
			String name = args[2];
			ProtectedCuboidRegion region = this.createRegion(selection, name);
			player.sendMessage("§aRegistering WorldGuard region for §b" + name);

			Integer price = null;
			if (args.length > 3) {
				price = this.parsePrice(player, args[3]);
			}
			if (price == null) {
				price = 0;
				return true;
			}

			Mansion mansion = new Mansion(region, player.getWorld(), name, price);

			player.sendMessage(
					"§aSearching for signs, chests, a crafting table, a furnace and an ender chest for the mansion §b"
							+ name);
			int counter = 0;
			int signs = 0;
			int chests = 0;
			Location minPoint = selection.getMinimumPoint();
			Location maxPoint = selection.getMaximumPoint();
			for (Block block : Utils.getBlocks(player.getWorld(), minPoint, maxPoint)) {
				if (block.getState() instanceof Sign) {
					mansion.addSign((Sign) block.getState());
					signs++;
				}
				if (block.getType().equals(Material.WORKBENCH)) {
					mansion.setCraftingTable(block.getLocation());
				}
				if (block.getType().equals(Material.CHEST)) {
					mansion.addChest((Chest) block.getState());
					chests++;
				}
				if (block.getType().equals(Material.FURNACE)) {
					mansion.setFurnace((Furnace) block.getState());
				}
				if (block.getType().equals(Material.ENDER_CHEST)) {
					mansion.setEnderChest((EnderChest) block.getState());
				}
				counter++;
			}

			player.sendMessage("§aThe search for mansion components completed. §d" + counter + " §ablocks searched.");
			player.sendMessage("§e" + signs + " sign(s) found.");
			player.sendMessage("§6" + chests + " chest(s) found.");
			if (mansion.getCraftingTable() != null) {
				player.sendMessage("§9A crafting table was found.");
			}

			if (mansion.getFurnace() != null) {
				player.sendMessage("§7A Furnace was found.");
			}

			if (mansion.getEnderChest() != null) {
				player.sendMessage("§2An EnderChest was found.");
			}

			long expires = System.currentTimeMillis() + (plugin.settings().getRentDays() * 24 * 60 * 60 * 1000);
			mansion.setExpires(expires);

			mansion.updateSigns();

			this.livingSpaces.put(name, mansion);
			player.sendMessage("§aSuccessfully created mansion §b" + name);

			return true;
		}
		return true;
	}

	public void removeLivingSpace(LivingSpace space) {
		this.livingSpaces.remove(space.getName());
	}

	public boolean removeApartmentCommand(Player player, Command cmd, String[] args) {
		if (args.length > 1) {
			String name = args[2];
			ProtectedRegion region = plugin.getWorldGuard().getRegionManager(player.getWorld()).getRegion(name);
			LivingSpace space = this.livingSpaces.get(name);
			if (space instanceof Apartment) {
				this.removeLivingSpace(space);
			} else {
				player.sendMessage("§cThat Living Space is not an apartment.");
				return true;
			}
			if (!(region == null)) {
				plugin.getWorldGuard().getRegionManager(player.getWorld()).removeRegion(name);
			}

			player.sendMessage("§aSuccessfully removed apartment §b" + name);
		} else {
			player.sendMessage("§cUsage: /<command> <arg0> <arg1> <name>".replace("<command>", cmd.getName())
					.replace("<arg0>", args[0]).replace("<arg1>", args[1]));
			return true;
		}
		return true;
	}

	public boolean removeHouseCommand(Player player, Command cmd, String[] args) {
		if (args.length > 1) {
			String name = args[2];
			ProtectedRegion region = plugin.getWorldGuard().getRegionManager(player.getWorld()).getRegion(name);
			LivingSpace space = this.livingSpaces.get(name);
			if (space instanceof House) {
				this.removeLivingSpace(space);
			} else {
				player.sendMessage("§cThat Living Space is not an house.");
				return true;
			}
			if (!(region == null)) {
				plugin.getWorldGuard().getRegionManager(player.getWorld()).removeRegion(name);
			}

			player.sendMessage("§aSuccessfully removed house §b" + name);
		} else {
			player.sendMessage("§cUsage: /<command> <arg0> <arg1> <name>".replace("<command>", cmd.getName())
					.replace("<arg0>", args[0]).replace("<arg1>", args[1]));
			return true;
		}
		return true;
	}

	public boolean removeMansionCommand(Player player, Command cmd, String[] args) {
		if (args.length > 1) {
			String name = args[2];
			ProtectedRegion region = plugin.getWorldGuard().getRegionManager(player.getWorld()).getRegion(name);
			LivingSpace space = this.livingSpaces.get(name);
			if (space instanceof Mansion) {
				this.removeLivingSpace(space);
			} else {
				player.sendMessage("§cThat Living Space is not an mansion.");
				return true;
			}
			if (!(region == null)) {
				plugin.getWorldGuard().getRegionManager(player.getWorld()).removeRegion(name);
			}

			player.sendMessage("§aSuccessfully removed mansion §b" + name);
		} else {
			player.sendMessage("§cUsage: /<command> <arg0> <arg1> <name>".replace("<command>", cmd.getName())
					.replace("<arg0>", args[0]).replace("<arg1>", args[1]));
			return true;
		}
		return true;
	}

	public LivingSpace getLivingSpace(Sign sign) {
		for (LivingSpace space : livingSpaces.values()) {
			for (Sign spaceSign : space.getSigns()) {
				if (spaceSign.getLocation().equals(sign.getLocation())) {
					return space;
				}
			}
		}
		return null;
	}

	public LivingSpace getLivingSpace(Player player) {
		for (LivingSpace livingSpace : livingSpaces.values()) {
			if (player.getUniqueId().equals(livingSpace.getOwner())) {
				return livingSpace;
			}
		}
		return null;
	}

	public LivingSpace getLivingSpace(Location location) {
		if (livingSpaces.isEmpty())
			return null;
		for (LivingSpace space : livingSpaces.values()) {
			if (space.getRegion() != null) {
				Location max = new Location(space.getWorld(), space.getRegion().getMaximumPoint().getBlockX(),
						space.getRegion().getMaximumPoint().getBlockY(),
						space.getRegion().getMaximumPoint().getBlockZ());
				Location min = new Location(space.getWorld(), space.getRegion().getMinimumPoint().getBlockX(),
						space.getRegion().getMinimumPoint().getBlockY(),
						space.getRegion().getMinimumPoint().getBlockZ());

				for (Block block : Utils.getBlocks(space.getWorld(), min, max)) {
					if (block.getLocation().equals(location)) {
						return space;
					}
				}
			}
		}

		return null;

	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Block block = e.getBlock();
		BrutusUser user = plugin.players().getBrutusUser(e.getPlayer().getUniqueId());

		LivingSpace space = getLivingSpace(block.getLocation());
		if (user.getToggle(Toggle.ADMINMODE))
			return;
		if (space == null)
			return;
		if ((space.getOwner().equals(user.getUniqueId())) || space.getMembers().contains(user.getName()))
			return;
		e.setCancelled(true);
		user.sendMessage("§cYou are not allowed to break blocks here.");
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		Block block = e.getBlock();
		BrutusUser user = plugin.players().getBrutusUser(e.getPlayer().getUniqueId());

		LivingSpace space = getLivingSpace(block.getLocation());
		if (user.getToggle(Toggle.ADMINMODE))
			return;
		if (space == null)
			return;
		if ((space.getOwner().equals(user.getUniqueId())) || space.getMembers().contains(user.getName()))
			return;
		e.setCancelled(true);
		user.sendMessage("§cYou are not allowed to place blocks here.");
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		Block block = e.getClickedBlock();
		if (block == null) return;
		
		BrutusUser user = plugin.players().getBrutusUser(e.getPlayer().getUniqueId());
		
		LivingSpace space = getLivingSpace(block.getLocation());
		if (user.getToggle(Toggle.ADMINMODE))
			return;
		if (space == null)
			return;
		if ((space.getOwner().equals(user.getUniqueId())) || space.getMembers().contains(user.getName()))
			return;
		for (Sign sign : space.getSigns()) {
			if(sign.getLocation().equals(block.getLocation())) {
				return;
			}
		}
		e.setCancelled(true);
		user.sendMessage("§cYou are not allowed to interact here.");
	}

	@EventHandler
	public void onBlockIgnite(BlockIgniteEvent e) {
		Block block = e.getBlock();
		LivingSpace space = getLivingSpace(block.getLocation());
		if (space == null)
			return;
		e.setCancelled(true);
	}

	@EventHandler
	public void onBlockExplode(BlockExplodeEvent e) {
		Block block = e.getBlock();
		LivingSpace space = getLivingSpace(block.getLocation());
		if (space != null) {
			e.setCancelled(true);
		}
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(args.length > 0)) {
			sender.sendMessage("§cNot enough arguments.");
			return true;
		}

		if (!(sender instanceof Player)) {
			sender.sendMessage(plugin.settings().getPlayerOnlyCommandMessage());
			return true;
		}

		Player player = (Player) sender;

		BrutusUser user = plugin.players().getBrutusUser(player.getUniqueId());

		if (Utils.checkArguments(args, 0, "adminmode", "admin", "a")) {
			if (!sender.hasPermission(Perms.LIVINGSPACE_ADMIN)) {
				sender.sendMessage(plugin.settings().getNoPermissionMessage());
				return true;
			}
			if (user.getToggle(Toggle.ADMINMODE)) {
				user.setToggle(Toggle.ADMINMODE, false);
				user.sendMessage("§cYou have disabled Housing Admin Mode.");
				return true;
			} else {
				user.setToggle(Toggle.ADMINMODE, true);
				user.sendMessage("§aYou have enabled Housing Admin Mode.");
				return true;
			}
		} else if (Utils.checkArguments(args, 0, "create", "c")) {
			if (!sender.hasPermission(Perms.LIVINGSPACE_ADMIN)) {
				sender.sendMessage(plugin.settings().getNoPermissionMessage());
				return true;
			}
			if (!user.getToggle(Toggle.ADMINMODE)) {
				user.sendMessage("§cYou must be in admin mode to do that.");
				return true;
			}

			if (Utils.checkArguments(args, 1, "apartment", "apart", "a")) {
				return this.createApartment(player, cmd, args);
			} else if (Utils.checkArguments(args, 1, "house", "h")) {
				return this.createHouse(player, cmd, args);
			} else if (Utils.checkArguments(args, 1, "mansion", "m")) {
				return this.createMansion(player, cmd, args);
			} else {
				player.sendMessage("§cUsage: /<command> create <apartment|house|mansion> <options...>"
						.replace("<command>", cmd.getName()));
				return true;
			}
		} else if (Utils.checkArguments(args, 0, "remove", "r", "delete", "d")) {
			if (!sender.hasPermission(Perms.LIVINGSPACE_ADMIN)) {
				sender.sendMessage(plugin.settings().getNoPermissionMessage());
				return true;
			}
			if (!user.getToggle(Toggle.ADMINMODE)) {
				player.sendMessage("§cYou must be in admin mode to do that.");
				return true;
			}

			if (Utils.checkArguments(args, 1, "apartment", "apart", "a")) {
				return this.removeApartmentCommand(player, cmd, args);
			} else if (Utils.checkArguments(args, 1, "house", "h")) {
				return this.removeHouseCommand(player, cmd, args);
			} else if (Utils.checkArguments(args, 1, "mansion", "m")) {
				return this.removeMansionCommand(player, cmd, args);
			} else {
				player.sendMessage("§cUsage: /<command> delete <apartment|house|mansion> <options...>"
						.replace("<command>", cmd.getName()));
				return true;
			}
		} else if (args[0].equalsIgnoreCase("confirm")) {
			if (!user.isBuying()) {
				player.sendMessage("§cYou are currently not buying a living space.");
				return true;
			}

			LivingSpace space = user.getBuying();

			EconomyResponse response = plugin.getVaultEconomy().withdrawPlayer(player, space.getPrice());
			if (response.transactionSuccess()) {
				space.setAvailable(false);
				space.setOwner(player.getUniqueId());
				DefaultDomain owners = new DefaultDomain();
				owners.addPlayer(player.getUniqueId());
				space.getRegion().setOwners(owners);
				space.updateSigns();
				this.livingSpaces.remove(space.getName());
				this.livingSpaces.put(space.getName(), space);
				player.sendMessage(
						"§aYou have bought §e" + space.getName() + " §afor §6" + space.getPrice() + " denari");
			} else {
				player.sendMessage("§cThere was an issue processing your transaction.");
				player.sendMessage("§cThe error is §b" + response.errorMessage);
			}

		} else if (args[0].equalsIgnoreCase("cancel")) {
			if (!user.isBuying()) {
				player.sendMessage("§cYou are currently not buying a living space.");
				return true;
			}
			user.removeBuying();
			player.sendMessage("§cYou have canceled your purchase of a living space.");
		} else if (Utils.checkArguments(args, 0, "edit", "e")) {
			if (!sender.hasPermission(Perms.LIVINGSPACE_ADMIN)) {
				sender.sendMessage(plugin.settings().getNoPermissionMessage());
				return true;
			}

			if (args.length == 1) {
				if (user.getToggle(Toggle.EDITMODE)) {
					user.setToggle(Toggle.EDITMODE, false);
					player.sendMessage("§cYou have disabled Housing Edit Mode.");
					return true;
				} else {
					user.setToggle(Toggle.EDITMODE, true);
					player.sendMessage("§aYou have enabled Housing Edit Mode.");
					return true;
				}
			} else if (args.length > 1) {
				if (Utils.checkArguments(args, 1, "apartment", "apart", "a")) {
					if (!user.getToggle(Toggle.EDITMODE)) {
						player.sendMessage("§cYou must be in edit mode to do that.");
						return true;
					}
					if (args.length > 2) {
						if (Utils.checkArguments(args, 2, "addsign", "as")) {
							if (args.length > 3) {
								LivingSpace space = livingSpaces.get(args[3]);
								if (space == null) {
									player.sendMessage("§cThat is not a valid apartment name.");
									return true;
								}
								if (space instanceof Apartment) {
									Apartment apartment = (Apartment) space;
									Set<Material> materials = null;
									Block block = player.getTargetBlock(materials, 50);
									if (block.getState() instanceof Sign) {
										Sign sign = (Sign) block.getState();
										apartment.addSign(sign);
										apartment.updateSigns();
										player.sendMessage("§aSuccessfully added a sign to the apartment §b"
												+ apartment.getName());
										livingSpaces.put(apartment.getName(), apartment);
										return true;
									} else {
										player.sendMessage("§cYou must look at a sign to add it to an apartment.");
										return true;
									}
								} else {
									player.sendMessage("§cThe name does not match an apartment.");
									return true;
								}
							} else {
								player.sendMessage("§cYou must provide an apartment name.");
							}
						} else if (Utils.checkArguments(args, 2, "removesign", "rs")) {
							if (args.length > 3) {
								LivingSpace space = livingSpaces.get(args[3]);
								if (space == null) {
									player.sendMessage("§cThat is not a valid apartment name.");
									return true;
								}
								if (space instanceof Apartment) {
									Apartment apartment = (Apartment) space;
									Set<Material> materials = null;
									Block block = player.getTargetBlock(materials, 50);
									if (block.getState() instanceof Sign) {
										Sign sign = (Sign) block.getState();
										apartment.removeSign(sign);
										player.sendMessage("§aSuccessfully removed a sign to the apartment §b"
												+ apartment.getName());
										livingSpaces.put(apartment.getName(), apartment);
										return true;
									} else {
										player.sendMessage("§cYou must look at a sign to add it to an apartment.");
										return true;
									}
								} else {
									player.sendMessage("§cThe name does not match an apartment.");
									return true;
								}
							} else {
								player.sendMessage("§cYou must provide an apartment name.");
							}
						} else {
							player.sendMessage("§cUnknown action §b" + args[2]);
							return true;
						}
					}
				} else if (Utils.checkArguments(args, 1, "house", "h")) {
					if (!user.getToggle(Toggle.EDITMODE)) {
						player.sendMessage("§cYou must be in edit mode to do that.");
						return true;
					}
					if (args.length > 2) {
						if (Utils.checkArguments(args, 2, "addsign", "as")) {
							if (args.length > 3) {
								LivingSpace space = livingSpaces.get(args[3]);
								if (space == null) {
									player.sendMessage("§cThat is not a valid house name.");
									return true;
								}
								if (space instanceof House) {
									House house = (House) space;
									Set<Material> materials = null;
									Block block = player.getTargetBlock(materials, 50);
									if (block.getState() instanceof Sign) {
										Sign sign = (Sign) block.getState();
										house.addSign(sign);
										house.updateSigns();
										player.sendMessage(
												"§aSuccessfully added a sign to the house §b" + house.getName());
										livingSpaces.put(house.getName(), house);
										return true;
									} else {
										player.sendMessage("§cYou must look at a sign to add it to an house.");
										return true;
									}
								} else {
									player.sendMessage("§cThe name does not match a house.");
									return true;
								}
							} else {
								player.sendMessage("§cYou must provide an house name.");
							}
						} else if (Utils.checkArguments(args, 1, "removesign", "rs")) {
							if (args.length > 3) {
								LivingSpace space = livingSpaces.get(args[3]);
								if (space == null) {
									player.sendMessage("§cThat is not a valid house name.");
									return true;
								}
								if (space instanceof House) {
									House house = (House) space;
									Set<Material> materials = null;
									Block block = player.getTargetBlock(materials, 50);
									if (block.getState() instanceof Sign) {
										Sign sign = (Sign) block.getState();
										house.removeSign(sign);
										player.sendMessage(
												"§aSuccessfully removed a sign to the house §b" + house.getName());
										livingSpaces.put(house.getName(), house);
										return true;
									} else {
										player.sendMessage("§cYou must look at a sign to add it to a house.");
										return true;
									}
								} else {
									player.sendMessage("§cThe name does not match a house.");
									return true;
								}
							} else {
								player.sendMessage("§cYou must provide an house name.");
							}
						} else if (Utils.checkArguments(args, 2, "setworkbench", "wb", "craftingtable", "ct")) {
							if (args.length > 3) {
								LivingSpace space = livingSpaces.get(args[3]);
								if (space == null) {
									player.sendMessage("§cThat is not a valid house name.");
									return true;
								}
								if (space instanceof House) {
									House house = (House) space;
									Set<Material> materials = null;
									Block block = player.getTargetBlock(materials, 50);
									if (block.getType().equals(Material.WORKBENCH)) {
										house.setCraftingTable(block.getLocation());
										player.sendMessage(
												"§aSuccessfully set the workbench to the house §b" + house.getName());
										livingSpaces.put(house.getName(), house);
										return true;
									} else {
										player.sendMessage("§cYou must look at a workbench to set it to a house.");
										return true;
									}
								} else {
									player.sendMessage("§cThe name does not match a house.");
									return true;
								}
							} else {
								player.sendMessage("§cThe name does not match a house.");
								return true;
							}
						} else if (Utils.checkArguments(args, 2, "setfurnace", "sf")) {
							if (args.length > 3) {
								LivingSpace space = livingSpaces.get(args[3]);
								if (space == null) {
									player.sendMessage("§cThat is not a valid house name.");
									return true;
								}
								if (space instanceof House) {
									House house = (House) space;
									Set<Material> materials = null;
									Block block = player.getTargetBlock(materials, 50);
									if (block.getState() instanceof Furnace) {
										Furnace furnace = (Furnace) block.getState();
										house.setFurnace(furnace);
										house.updateSigns();
										player.sendMessage(
												"§aSuccessfully set the furnace of the house §b" + house.getName());
										livingSpaces.put(house.getName(), house);
										return true;
									} else {
										player.sendMessage("§cYou must look at a furnace to set it to a house.");
										return true;
									}
								} else {
									player.sendMessage("§cThe name does not match a house.");
									return true;
								}
							} else {
								player.sendMessage("§cYou must provide a house name.");
							}
						} else if (Utils.checkArguments(args, 2, "addchest", "ac")) {
							if (args.length > 3) {
								LivingSpace space = livingSpaces.get(args[3]);
								if (space == null) {
									player.sendMessage("§cThat is not a valid house name.");
									return true;
								}
								if (space instanceof House) {
									House house = (House) space;
									Set<Material> materials = null;
									Block block = player.getTargetBlock(materials, 50);
									if (block.getState() instanceof Chest) {
										Chest chest = (Chest) block.getState();
										house.addChest(chest);
										house.updateSigns();
										player.sendMessage(
												"§aSuccessfully added a chest to the house §b" + house.getName());
										livingSpaces.put(house.getName(), house);
										return true;
									} else {
										player.sendMessage("§cYou must look at a chest to add it to a house.");
										return true;
									}
								} else {
									player.sendMessage("§cThe name does not match a house.");
									return true;
								}
							} else {
								player.sendMessage("§cYou must provide a house name.");
							}
						} else if (Utils.checkArguments(args, 2, "removechest", "rc")) {
							if (args.length > 3) {
								LivingSpace space = livingSpaces.get(args[3]);
								if (space == null) {
									player.sendMessage("§cThat is not a valid house name.");
									return true;
								}
								if (space instanceof House) {
									House house = (House) space;
									Set<Material> materials = null;
									Block block = player.getTargetBlock(materials, 50);
									if (block.getState() instanceof Chest) {
										Chest chest = (Chest) block.getState();
										for (Chest c : house.getChests()) {
											if (c.getLocation().equals(chest.getLocation())) {
												house.removeChest(c);
												player.sendMessage("§aSuccessfully removed a chest from the house §b"
														+ house.getName());
												livingSpaces.put(house.getName(), house);
												return true;
											}
										}
									} else {
										player.sendMessage("§cYou must look at a chest to remove it to a house.");
										return true;
									}
								} else {
									player.sendMessage("§cThe name does not match a house.");
									return true;
								}
							} else {
								player.sendMessage("§cYou must provide a house name.");
							}
						} else {
							player.sendMessage("§cUnknown action §b" + args[2]);
							return true;
						}
					}
				} else if (Utils.checkArguments(args, 1, "mansion", "m")) {
					if (!user.getToggle(Toggle.EDITMODE)) {
						player.sendMessage("§cYou must be in edit mode to do that.");
						return true;
					}
					if (args.length > 2) {
						if (Utils.checkArguments(args, 2, "addsign", "as")) {
							if (args.length > 3) {
								LivingSpace space = livingSpaces.get(args[3]);
								if (space == null) {
									player.sendMessage("§cThat is not a valid mansion name.");
									return true;
								}
								if (space instanceof Mansion) {
									Mansion mansion = (Mansion) space;
									Set<Material> materials = null;
									Block block = player.getTargetBlock(materials, 50);
									if (block.getState() instanceof Sign) {
										Sign sign = (Sign) block.getState();
										mansion.addSign(sign);
										mansion.updateSigns();
										player.sendMessage(
												"§aSuccessfully added a sign to the mansion §b" + mansion.getName());
										livingSpaces.put(mansion.getName(), mansion);
										return true;
									} else {
										player.sendMessage("§cYou must look at a sign to add it to a mansion.");
										return true;
									}
								} else {
									player.sendMessage("§cThe name does not match a mansion.");
									return true;
								}
							} else {
								player.sendMessage("§cYou must provide an mansion name.");
							}
						} else if (Utils.checkArguments(args, 2, "removesign", "rs")) {
							if (args.length > 3) {
								LivingSpace space = livingSpaces.get(args[3]);
								if (space == null) {
									player.sendMessage("§cThat is not a valid mansion name.");
									return true;
								}
								if (space instanceof Mansion) {
									Mansion mansion = (Mansion) space;
									Set<Material> materials = null;
									Block block = player.getTargetBlock(materials, 50);
									if (block.getState() instanceof Sign) {
										Sign sign = (Sign) block.getState();
										mansion.removeSign(sign);
										player.sendMessage(
												"§aSuccessfully removed a sign to the mansion §b" + mansion.getName());
										livingSpaces.put(mansion.getName(), mansion);
										return true;
									} else {
										player.sendMessage("§cYou must look at a sign to add it to a mansion.");
										return true;
									}
								} else {
									player.sendMessage("§cThe name does not match a mansion.");
									return true;
								}
							} else {
								player.sendMessage("§cYou must provide an mansion name.");
							}
						} else if (Utils.checkArguments(args, 2, "setworkbench", "wb", "craftingtable", "ct")) {
							if (args.length > 3) {
								LivingSpace space = livingSpaces.get(args[3]);
								if (space == null) {
									player.sendMessage("§cThat is not a valid mansion name.");
									return true;
								}
								if (space instanceof Mansion) {
									Mansion mansion = (Mansion) space;
									Set<Material> materials = null;
									Block block = player.getTargetBlock(materials, 50);
									if (block.getType().equals(Material.WORKBENCH)) {
										mansion.setCraftingTable(block.getLocation());
										player.sendMessage("§aSuccessfully set the workbench to the mansion §b"
												+ mansion.getName());
										livingSpaces.put(mansion.getName(), mansion);
										return true;
									} else {
										player.sendMessage("§cYou must look at a workbench to set it to a mansion.");
										return true;
									}
								} else {
									player.sendMessage("§cThe name does not match a mansion.");
									return true;
								}
							} else {
								player.sendMessage("§cThe name does not match a mansion.");
								return true;
							}
						} else if (Utils.checkArguments(args, 2, "setfurnace", "sf")) {
							if (args.length > 3) {
								LivingSpace space = livingSpaces.get(args[3]);
								if (space == null) {
									player.sendMessage("§cThat is not a valid mansion name.");
									return true;
								}
								if (space instanceof Mansion) {
									Mansion mansion = (Mansion) space;
									Set<Material> materials = null;
									Block block = player.getTargetBlock(materials, 50);
									if (block.getState() instanceof Furnace) {
										Furnace furnace = (Furnace) block.getState();
										mansion.setFurnace(furnace);
										player.sendMessage(
												"§aSuccessfully set the furnace of the mansion §b" + mansion.getName());
										livingSpaces.put(mansion.getName(), mansion);
										return true;
									} else {
										player.sendMessage("§cYou must look at a furnace to set it to a mansion.");
										return true;
									}
								} else {
									player.sendMessage("§cThe name does not match a mansion.");
									return true;
								}
							} else {
								player.sendMessage("§cYou must provide a mansion name.");
							}
						} else if (Utils.checkArguments(args, 2, "addchest", "ac")) {
							if (args.length > 3) {
								LivingSpace space = livingSpaces.get(args[3]);
								if (space == null) {
									player.sendMessage("§cThat is not a valid mansion name.");
									return true;
								}
								if (space instanceof Mansion) {
									Mansion mansion = (Mansion) space;
									Set<Material> materials = null;
									Block block = player.getTargetBlock(materials, 50);
									if (block.getState() instanceof Chest) {
										Chest chest = (Chest) block.getState();
										mansion.addChest(chest);
										mansion.updateSigns();
										player.sendMessage(
												"§aSuccessfully added a chest to the mansion §b" + mansion.getName());
										livingSpaces.put(mansion.getName(), mansion);
										return true;
									} else {
										player.sendMessage("§cYou must look at a chest to add it to a mansion.");
										return true;
									}
								} else {
									player.sendMessage("§cThe name does not match a mansion.");
									return true;
								}
							} else {
								player.sendMessage("§cYou must provide a mansion name.");
							}
						} else if (Utils.checkArguments(args, 2, "removechest", "rc")) {
							if (args.length > 3) {
								LivingSpace space = livingSpaces.get(args[3]);
								if (space == null) {
									player.sendMessage("§cThat is not a valid mansion name.");
									return true;
								}
								if (space instanceof Mansion) {
									Mansion mansion = (Mansion) space;
									Set<Material> materials = null;
									Block block = player.getTargetBlock(materials, 50);
									if (block.getState() instanceof Chest) {
										Chest chest = (Chest) block.getState();
										for (Chest c : mansion.getChests()) {
											if (c.getLocation().equals(chest.getLocation())) {
												mansion.removeChest(c);
												player.sendMessage("§aSuccessfully removed a chest from the mansion §b"
														+ mansion.getName());
												livingSpaces.put(mansion.getName(), mansion);
												return true;
											}
										}
									} else {
										player.sendMessage("§cYou must look at a chest to mansion it to a house.");
										return true;
									}
								} else {
									player.sendMessage("§cThe name does not mansion a house.");
									return true;
								}
							} else {
								player.sendMessage("§cYou must provide an mansion name.");
							}
						} else if (Utils.checkArguments(args, 2, "setenderchest", "setechest", "setec", "sec")) {
							if (args.length > 3) {
								LivingSpace space = livingSpaces.get(args[3]);
								if (space == null) {
									player.sendMessage("§cThat is not a valid mansion name.");
									return true;
								}
								if (space instanceof Mansion) {
									Mansion mansion = (Mansion) space;
									Set<Material> materials = null;
									Block block = player.getTargetBlock(materials, 50);
									if (block.getState() instanceof EnderChest) {
										EnderChest furnace = (EnderChest) block.getState();
										mansion.setEnderChest(furnace);
										mansion.updateSigns();
										player.sendMessage(
												"§aSuccessfully set the furnace of the mansion §b" + mansion.getName());
										livingSpaces.put(mansion.getName(), mansion);
										return true;
									} else {
										player.sendMessage("§cYou must look at a furnace to set it to a mansion.");
										return true;
									}
								} else {
									player.sendMessage("§cThe name does not match a mansion.");
									return true;
								}
							} else {
								player.sendMessage("§cYou must provide a mansion name.");
							}
						} else {
							player.sendMessage("§cUnknown action §b" + args[2]);
							return true;
						}
					}
				} else {
					player.sendMessage("§cUsage: /<command> edit <apartment|house|mansion> <options...>"
							.replace("<command>", cmd.getName()));
				}
			} else {
				sender.sendMessage("§cUnknown Command.");
				return true;
			}
		}
		return true;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onSignClick(PlayerInteractEvent e) {
		if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if (e.getClickedBlock().getState() instanceof Sign) {
				Player player = e.getPlayer();
				BrutusUser user = plugin.players().getBrutusUser(player);
				Sign sign = (Sign) e.getClickedBlock().getState();
				LivingSpace livingSpace = getLivingSpace(sign);
				if (livingSpace != null) {
					if ((livingSpace.getOwner() != null) && !livingSpace.isAvailable()) {
						player.sendMessage("§cThat space is occupied, Try another one.");
						return;
					}
				}

				LivingSpace owned = getLivingSpace(player);
				if (owned != null) {
					if ((owned instanceof Apartment) && (livingSpace instanceof Apartment)) {
						player.sendMessage("§cYou already have an apartment. You cannot have another.");
					} else if ((owned instanceof House) && (livingSpace instanceof House)) {
						player.sendMessage("§cYou already have an house. You cannot have another.");
					} else if ((owned instanceof Mansion) && (livingSpace instanceof Mansion)) {
						player.sendMessage("§cYou already have an mansion. You cannot have another.");
					}
				}

				String type = "";
				switch (livingSpace.getType()) {
				case APARTMENT:
					type = "apartment";
					break;
				case HOUSE:
					type = "house";
					break;
				case MANSION:
					type = "apartment";
					break;
				default: break;
				}

				user.setBuying(livingSpace);
				player.sendMessage("");
				player.sendMessage("§2Are you sure you want to buy this <type>?".replace("<type>", type));
				TextComponent confirm = new TextComponent("    /livingspaces confirm to confirm your purchase.");
				confirm.setColor(ChatColor.GREEN);
				confirm.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.SUGGEST_COMMAND,
						"/livingspaces confirm"));
				player.spigot().sendMessage(confirm);
				TextComponent cancel = new TextComponent("    /livingspaces cancel to cancel your purchase.");
				cancel.setColor(ChatColor.RED);
				cancel.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.SUGGEST_COMMAND,
						"/livingspaces deny"));
				player.spigot().sendMessage(cancel);
				player.sendMessage("");
			}
		}
	}
}