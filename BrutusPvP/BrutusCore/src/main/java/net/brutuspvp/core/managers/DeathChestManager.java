package net.brutuspvp.core.managers;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import net.brutuspvp.core.BrutusCore;
import net.brutuspvp.core.Perms;

public class DeathChestManager implements Listener, CommandExecutor {

	private HashMap<UUID, DeathChest> chests = new HashMap<UUID, DeathChest>();
	private BrutusCore plugin;
	private static boolean enabled = true;

	public DeathChestManager(BrutusCore plugin) {
		plugin.registerListener(this);
		this.plugin = plugin;
	}

	class DeathChest {
		private Location chest1;
		private Location chest2;
		private UUID playerUniqueId;

		public DeathChest(UUID puid, Location c1, Location c2) {
			chest1 = c1;
			chest2 = c2;
			playerUniqueId = puid;
		}

		public Location getChest1() {
			return chest1;
		}

		public Location getChest2() {
			return chest2;
		}

		public UUID getPlayerUniqueId() {
			return playerUniqueId;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((playerUniqueId == null) ? 0 : playerUniqueId.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			DeathChest other = (DeathChest) obj;
			if (playerUniqueId == null) {
				if (other.playerUniqueId != null)
					return false;
			} else if (!playerUniqueId.equals(other.playerUniqueId))
				return false;
			return true;
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!sender.hasPermission(Perms.TOGGLE_DEATHCHEST)) {
			sender.sendMessage(plugin.settings().getNoPermissionMessage());
			return true;
		}
		
		enabled = !enabled;
		if(enabled) {
			sender.sendMessage("§aYou have enabled DeathChests.");
		} else {
			sender.sendMessage("§aYou have disabled DeathChests.");
		}
		return true;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDeath(PlayerDeathEvent e) {
		if (enabled) {
			List<ItemStack> drops = e.getDrops();
			Player player = e.getEntity();
			Location chest1loc = player.getLocation();
			World world = player.getWorld();
			int x = player.getLocation().getBlockX() + 1;
			int y = player.getLocation().getBlockY();
			int z = player.getLocation().getBlockZ();
			Location chest2loc = new Location(world, x, y, z);
			Block chest1block = chest1loc.getBlock();
			Block chest2block = chest2loc.getBlock();
			chest1block.setType(Material.CHEST);
			chest2block.setType(Material.CHEST);
			Chest chest1 = (Chest) chest1block.getState();
			Chest chest2 = (Chest) chest2block.getState();
			Inventory chest1inv = chest1.getInventory();
			Inventory chest2inv = chest2.getInventory();

			for (ItemStack item : drops) {
				if (item != null && !item.getType().equals(Material.AIR)) {
					if (chest1inv.getContents().length <= 27) {
						chest1inv.addItem(item);
					} else if (chest1inv.getContents().length > 27) {
						chest2inv.addItem(item);
					}
				}
			}
			chests.put(player.getUniqueId(),
					new DeathChest(player.getUniqueId(), chest1.getLocation(), chest2.getLocation()));
			drops.clear();
			new BukkitRunnable() {
				public void run() {
					player.sendMessage("§cA death chest has been spawned where you died.");
				}
			}.runTaskLater(plugin, 3L);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent e) {
		if (enabled) {
			if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
				if (e.getClickedBlock() == null)
					return;
				Block block = e.getClickedBlock();
				if (!(block.getState() instanceof Chest))
					return;
				if (!(this.chests.containsKey(e.getPlayer().getUniqueId())))
					return;
				DeathChest deathChest = chests.get(e.getPlayer().getUniqueId());
				deathChest.getChest1().getBlock().setType(Material.AIR);
				deathChest.getChest2().getBlock().setType(Material.AIR);
				chests.remove(e.getPlayer().getUniqueId());
				e.getPlayer().sendMessage("§aYou have reclaimed your items!");
			}
		}
	}

	@EventHandler
	public void onExplode(EntityExplodeEvent e) {
		for (Entry<UUID, DeathChest> entry : chests.entrySet()) {
			for (Block block : e.blockList()) {
				if (block.getLocation().equals(entry.getValue().getChest1())
						|| block.getLocation().equals(entry.getValue().getChest2())) {
					final BlockState state = block.getState();
					block.setType(Material.AIR);

					new BukkitRunnable() {
						public void run() {
							state.update();
						}
					}.runTaskLater(plugin, 5L);
				}
			}
		}
	}
}