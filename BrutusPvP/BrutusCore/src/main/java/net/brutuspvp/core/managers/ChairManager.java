package net.brutuspvp.core.managers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import net.brutuspvp.core.BrutusCore;

public class ChairManager implements Listener {

	private BrutusCore plugin;

	public ChairManager(BrutusCore plugin) {
		plugin.registerListener(this);
		this.plugin = plugin;
	}

	public Map<Player, Location> playerLocation = new HashMap<Player, Location>();
	public Map<Player, Entity> chairList = new HashMap<Player, Entity>();
	public Map<Player, Location> chairLocation = new HashMap<Player, Location>();

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		try {
			Player player = event.getPlayer();
			if (event.getClickedBlock() != null) {
				Block block = event.getClickedBlock();
				if ((event.getAction() == Action.RIGHT_CLICK_BLOCK) && (!player.isInsideVehicle())) {
					Material blockMaterial = block.getType();
					List<Material> chairs = new ArrayList<Material>(Arrays.asList(Material.ACACIA_STAIRS, Material.BIRCH_WOOD_STAIRS, 
							Material.BRICK_STAIRS, Material.COBBLESTONE_STAIRS, Material.DARK_OAK_STAIRS, Material.JUNGLE_WOOD_STAIRS, 
							Material.NETHER_BRICK_STAIRS, Material.PURPUR_STAIRS, Material.QUARTZ_STAIRS, Material.RED_SANDSTONE_STAIRS, 
							Material.SANDSTONE_STAIRS, Material.SMOOTH_STAIRS, Material.SPRUCE_WOOD_STAIRS, Material.WOOD_STAIRS));
					for (Material chairBlock : chairs) {
						if (blockMaterial == chairBlock) {
							World world = player.getWorld();
							this.playerLocation.put(player, player.getLocation());
							Entity chair = world.spawnEntity(player.getLocation(), EntityType.ARROW);
							this.chairList.put(player, chair);
							chair.teleport(block.getLocation().add(0.5D, 0.2D, 0.5D));
							this.chairLocation.put(player, chair.getLocation());
							chair.addPassenger(player);
							event.setCancelled(true);
							return;
						}
					}
				}
			}
		} catch (Exception e) {
			BrutusCore.createBrutusError(e, "ChairManager PlayerInteractEvent");
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		try {
			if (this.playerLocation.containsKey(event.getPlayer())) {
				if (event.getPlayer().isSneaking()) {
					Player player = event.getPlayer();
					final Player sit_player = player;
					final Location stand_location = (Location) this.playerLocation.get(player);
					Entity sit_chair = (Entity) this.chairList.get(player);
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
						public void run() {
							sit_player.teleport(stand_location);
							sit_player.setSneaking(false);
						}
					}, 1L);
					event.setCancelled(true);
					this.playerLocation.remove(player);
					sit_chair.remove();
					this.chairList.remove(player);
					event.setCancelled(true);
					return;
				}
				event.setCancelled(true);
			}
		} catch (Exception e) {
			BrutusCore.createBrutusError(e, "ChairManager PlayerTeleportEvent");
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		try {
			if (this.playerLocation.containsKey(event.getPlayer())) {
				Player player = event.getPlayer();
				if ((player.getLocation() != this.playerLocation.get(player)) && (!player.isInsideVehicle())) {
					World world = player.getWorld();
					if (this.chairLocation.containsKey(player)) {
						Entity chair = world.spawnEntity((Location) this.chairLocation.get(player), EntityType.ARROW);
						chair.addPassenger(player);
					}
				}
			}
		} catch (Exception e) {
			BrutusCore.createBrutusError(e, "ChairManager PlayerMoveEvent");
		}
	}
}