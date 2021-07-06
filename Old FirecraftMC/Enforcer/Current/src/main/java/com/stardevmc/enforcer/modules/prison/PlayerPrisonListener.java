package com.stardevmc.enforcer.modules.prison;

import com.stardevmc.enforcer.modules.base.ModuleListener;
import com.stardevmc.enforcer.objects.punishment.Punishment;
import com.stardevmc.enforcer.objects.punishment.JailPunishment;
import com.stardevmc.enforcer.objects.prison.Prison;
import com.stardevmc.enforcer.util.Messages;
import com.stardevmc.enforcer.util.Perms;
import com.starmediadev.lib.items.InventoryStore;
import com.starmediadev.lib.util.Utils;
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
            e.getPlayer().sendMessage(Utils.color(Messages.prisonNoAction("break blocks")));
        }
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (plugin.getPunishmentModule().getManager().isJailed(e.getPlayer().getUniqueId())) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(Utils.color(Messages.prisonNoAction("place blocks")));
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
                    player.sendMessage(Utils.color(Messages.PRISON_OUTSIDE_BOUNDS));
                }
            }
        } else {
            for (Punishment IPunishment : plugin.getPunishmentModule().getManager().getJailPunishments(player.getUniqueId())) {
                JailPunishment jailPunishment = ((JailPunishment) IPunishment);
                
                new BukkitRunnable() {
                    public void run() {
                        player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
                        if (jailPunishment.wasUnjailedWhileOffline() && !jailPunishment.wasNotifiedOfOfflineUnjail()) {
                            player.sendMessage(Utils.color(Messages.JAILED_WHILE_OFFLINE));
                            try {
                                ItemStack[] items = InventoryStore.stringToItems(jailPunishment.getJailedInventory());
                                player.getInventory().setContents(items);
                                player.sendMessage(Utils.color(Messages.INVENTORY_RESTORED));
                                jailPunishment.setNotifiedOfOfflineUnjail(true);
                            } catch (Exception e) {
                                player.sendMessage(Utils.color(Messages.INVENTORY_RESTORE_PROBLEM));
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
            p.sendMessage(Utils.color(Messages.prisonNoAction("use commands")));
        }
    }
    
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) {
            Player p = ((Player) e.getDamager());
            if (plugin.getPunishmentModule().getManager().isJailed(p.getUniqueId())) {
                e.setCancelled(true);
                p.sendMessage(Utils.color(Messages.prisonNoAction("damage entities")));
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