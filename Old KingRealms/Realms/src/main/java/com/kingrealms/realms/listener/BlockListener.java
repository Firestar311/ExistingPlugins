package com.kingrealms.realms.listener;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.graves.Grave;
import com.kingrealms.realms.items.type.CropItem;
import com.kingrealms.realms.items.type.MysticalResource;
import com.kingrealms.realms.profile.RealmProfile;
import com.kingrealms.realms.skills.farming.CropBlock;
import com.kingrealms.realms.skills.mining.MysticalBlock;
import com.kingrealms.realms.territory.base.Territory;
import com.kingrealms.realms.util.RealmsUtils;
import com.starmediadev.lib.util.Utils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;

public class BlockListener implements Listener {
    
    private final Realms plugin = Realms.getInstance();
    
    @EventHandler
    public void onDragonEggTeleport(BlockFromToEvent e) {
        if (plugin.getSpawn().contains(e.getBlock().getLocation()) || plugin.getWarzone().contains(e.getBlock().getLocation())) {
            if (e.getBlock().getType().equals(Material.DRAGON_EGG)) {
                e.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (plugin.getSpawn().contains(e.getBlock().getLocation()) || plugin.getWarzone().contains(e.getBlock().getLocation())) {
            if (plugin.getSeason().isActive()) {
                MysticalBlock mysticalBlock = plugin.getMiningManager().getMysticalBlock(e.getBlock().getLocation());
                if (mysticalBlock != null) return;
                CropBlock cropBlock = plugin.getFarmingManager().getCropBlock(e.getBlock().getLocation());
                if (cropBlock != null) {
                    if (cropBlock.contains(e.getBlock().getLocation())) return;
                }
            }
    
            if (!e.getPlayer().hasPermission("realms.serverclaims.override")) {
                e.setCancelled(true);
                e.getPlayer().sendMessage(Utils.color("&cCannot break blocks here."));
                return;
            }
        }
        
        if (getTerritory(e) == null) {
            RealmProfile profile = plugin.getProfileManager().getProfile(e.getPlayer().getUniqueId());
            profile.addMinedBlock(e.getBlock().getType());
            return;
        }
        
        if (!RealmsUtils.isMember(e.getPlayer())) {
            if (!e.getPlayer().hasPermission("realms.claims.override")) {
                e.setCancelled(true);
                return;
            }
        }
    
        Grave grave = plugin.getGraveManager().getGrave(e.getBlock().getLocation());
        if (grave != null) {
            e.setCancelled(true);
            e.getPlayer().sendMessage("&cYou cannot break grave blocks.");
            return;
        }
        
        if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
            RealmProfile profile = plugin.getProfileManager().getProfile(e.getPlayer().getUniqueId());
            profile.addMinedBlock(e.getBlock().getType());
        }
    }
    
    private Territory getTerritory(BlockEvent e) {
        return plugin.getTerritoryManager().getTerritory(e.getBlock().getLocation());
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if (plugin.getSpawn().contains(e.getBlock().getLocation()) || plugin.getWarzone().contains(e.getBlock().getLocation())) {
            if (!e.getPlayer().hasPermission("realms.serverclaims.override")) {
                if (CropItem.isCropItem(e.getItemInHand()) || MysticalResource.isMysticalResource(e.getItemInHand())) {
                    return;    
                }
                
                e.setCancelled(true);
                e.getPlayer().sendMessage(Utils.color("&cCannot place blocks here."));
                return;
            }
        }
        
        if (getTerritory(e) == null) {
            RealmProfile profile = plugin.getProfileManager().getProfile(e.getPlayer().getUniqueId());
            profile.addPlacedBlock(e.getBlock().getType());
            return;
        }
    
        if (!RealmsUtils.isMember(e.getPlayer())) {
            if (!e.getPlayer().hasPermission("realms.claims.override")) {
                e.setCancelled(true);
                return;
            }
        }
        
        if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
            RealmProfile profile = plugin.getProfileManager().getProfile(e.getPlayer().getUniqueId());
            profile.addPlacedBlock(e.getBlock().getType());
        }
    }
    
    @EventHandler
    public void onBlockIgnite(BlockIgniteEvent e) {
        if (plugin.getSpawn().contains(e.getBlock().getLocation()) || plugin.getWarzone().contains(e.getBlock().getLocation())) {
            if (e.getPlayer() != null) {
                if (!e.getPlayer().hasPermission("realms.serverclaims.override")) {
                    e.setCancelled(true);
                    e.getPlayer().sendMessage(Utils.color("&cCannot ignite blocks here."));
                    return;
                }
            }
        }
        if (getTerritory(e) == null) {
            return;
        }
        
        if (!RealmsUtils.isMember(e.getPlayer())) {
            if (!e.getPlayer().hasPermission("realms.claims.override")) {
                e.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onBlockExplode(BlockExplodeEvent e) {
        if (plugin.getSpawn().contains(e.getBlock().getLocation()) || plugin.getWarzone().contains(e.getBlock().getLocation())) {
            e.setCancelled(true);
            return;
        }
        
        if (getTerritory(e) == null) {
            return;
        }
        if (getTerritory(e) != null) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onBlockFertalize(BlockFertilizeEvent e) {
        if (plugin.getSpawn().contains(e.getBlock().getLocation()) || plugin.getWarzone().contains(e.getBlock().getLocation())) {
            if (!e.getPlayer().hasPermission("realms.serverclaims.override")) {
                e.setCancelled(true);
                e.getPlayer().sendMessage(Utils.color("&cCannot fertilize blocks here."));
                return;
            }
        }
        if (getTerritory(e) == null) {
            return;
        }
        
        if (!RealmsUtils.isMember(e.getPlayer())) {
            if (!e.getPlayer().hasPermission("realms.claims.override")) {
                e.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onEntityBlockForm(EntityBlockFormEvent e) {
        if (plugin.getSpawn().contains(e.getBlock().getLocation()) || plugin.getWarzone().contains(e.getBlock().getLocation())) {
            e.setCancelled(true);
            return;
        }
        if (getTerritory(e) == null) {
            return;
        }
        
        if (e.getEntity() instanceof Player) {
            if (!RealmsUtils.isMember(((Player) e.getEntity()))) {
                if (!e.getEntity().hasPermission("realms.claims.override")) {
                    e.setCancelled(true);
                }
            }
        }
    }
}