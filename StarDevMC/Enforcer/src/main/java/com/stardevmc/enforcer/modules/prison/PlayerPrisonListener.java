package com.stardevmc.enforcer.modules.prison;

import com.firestar311.lib.items.InventoryStore;
import com.firestar311.lib.util.Utils;
import com.stardevmc.enforcer.modules.base.ModuleListener;
import com.stardevmc.enforcer.modules.punishments.type.abstraction.Punishment;
import com.stardevmc.enforcer.modules.punishments.type.impl.JailPunishment;
import com.stardevmc.enforcer.util.Perms;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerPrisonListener extends ModuleListener {
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (plugin.getPunishmentModule().getManager().isJailed(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(Utils.color("&cYou cannot break blocks while jailed."));
        }
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (plugin.getPunishmentModule().getManager().isJailed(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(Utils.color("&cYou cannot place blocks while jailed."));
        }
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if (plugin.getPunishmentModule().getManager().isJailed(e.getPlayer().getUniqueId())) {
            Prison prison = plugin.getPrisonModule().getManager().getPrison(player.getUniqueId());
            if (prison != null) {
                if (!prison.contains(player)) {
                    player.teleport(prison.getLocation());
                    if (player.getInventory().getSize() > 0) {
                        player.getInventory().clear();
                    }
                    player.sendMessage(Utils.color("&cYou were outside of the prison bounds, teleporting you to the spawn location."));
                }
            }
        } else {
            for (Punishment punishment : plugin.getPunishmentModule().getManager().getJailPunishments(player.getUniqueId())) {
                JailPunishment jailPunishment = ((JailPunishment) punishment);
                
                new BukkitRunnable() {
                    public void run() {
                        player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
                        if (jailPunishment.wasUnjailedWhileOffline() && !jailPunishment.wasNotifiedOfOfflineUnjail()) {
                            player.sendMessage(Utils.color("&aYou have been unjailed while you were offline"));
                            try {
                                ItemStack[] items = InventoryStore.stringToItems(jailPunishment.getJailedInventory());
                                player.getInventory().setContents(items);
                                player.sendMessage(Utils.color("&7&oYour inventory items have been restored."));
                                jailPunishment.setNotifiedOfOfflineUnjail(true);
                            } catch (Exception e) {
                                player.sendMessage(Utils.color("&cThere was a problem restoring your inventory. Please contact the plugin developer"));
                            }
                        }
                    }
                }.runTaskLater(plugin, 5L);
            }
        }
        
        if (plugin.getPrisonModule().getManager().getPrisons().isEmpty()) {
            if (player.hasPermission(Perms.ENFORCER_ADMIN)) {
                player.sendMessage(Utils.color("&c&lThere are currently no prisons set and the jail punishment type is enabled. Jails will not work."));
            }
        }
    }
    
    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        if (plugin.getPunishmentModule().getManager().isJailed(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
            p.sendMessage(Utils.color("&cYou cannot use commands while jailed."));
        }
    }
    
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) {
            Player p = ((Player) e.getDamager());
            if (plugin.getPunishmentModule().getManager().isJailed(p.getUniqueId())) {
                e.setCancelled(true);
                p.sendMessage(Utils.color("&cYou cannot damage entites while in jail."));
            }
        }
    }
    
    @EventHandler
    public void foodChangeEvent(FoodLevelChangeEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = ((Player) e.getEntity());
            if (plugin.getPunishmentModule().getManager().isJailed(p.getUniqueId())) {
                p.setFoodLevel(20);
                p.setSaturation(20L);
            }
        }
    }
}