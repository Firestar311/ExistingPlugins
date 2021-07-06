package com.kingrealms.realms.listener;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.entities.CustomEntities;
import com.kingrealms.realms.entities.type.CustomWitherSkeleton;
import com.kingrealms.realms.entities.type.ICustomEntity;
import com.kingrealms.realms.profile.RealmProfile;
import com.kingrealms.realms.territory.base.Territory;
import com.kingrealms.realms.territory.enums.Privacy;
import com.kingrealms.realms.util.RealmsUtils;
import com.starmediadev.lib.util.Utils;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.projectiles.ProjectileSource;

import java.util.*;

public class EntityListener implements Listener {
    
    private final Realms plugin = Realms.getInstance();
    private final Map<UUID, Item> droppedItems = new HashMap<>();
    
    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        ICustomEntity customEntity = CustomEntities.getCustomEntity(e.getEntity());
        if (customEntity != null) {
            if (customEntity instanceof CustomWitherSkeleton) {
                CustomWitherSkeleton ws = (CustomWitherSkeleton) customEntity;
                if (ws.isPortalKeeper()) {
                    e.setCancelled(true);
                }
            }
        }
    }
    
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (plugin.getSpawn().contains(e.getEntity().getLocation())) {
            e.setCancelled(true);
            return;
        }
        
        Entity damager = e.getDamager();
        Entity target = e.getEntity();
        
        if (damager instanceof Player || damager instanceof Projectile) {
            Territory locationTerritory = plugin.getTerritoryManager().getTerritory(target.getLocation()); //This will be used for flags later anyways.
            if (locationTerritory != null) {
                if (target instanceof LivingEntity) {
                    if (target instanceof Ambient || target instanceof Animals || target instanceof Player) {
                        if (damager instanceof Projectile) {
                            Projectile projectile = (Projectile) damager;
                            ProjectileSource shooter = projectile.getShooter();
                            if (shooter instanceof Player) {
                                Player playerShooter = (Player) shooter;
                                if (!locationTerritory.isMember(playerShooter)) {
                                    if (!playerShooter.hasPermission("realms.claims.override")) {
                                        e.setCancelled(true);
                                        playerShooter.sendMessage(Utils.color("&cYou cannot hurt that entity."));
                                        projectile.remove();
                                    } else {
                                        playerShooter.sendMessage(Utils.color("&cYou hurt that entity because you are overriding claim protection."));
                                    }
                                }
                            } else {
                                e.setCancelled(true);
                            }
                        } else if (damager instanceof Player) {
                            Player playerDamager = (Player) damager;
                            if (!locationTerritory.isMember(playerDamager)) {
                                if (!playerDamager.hasPermission("realms.claims.override")) {
                                    e.setCancelled(true);
                                    playerDamager.sendMessage(Utils.color("&cYou cannot hurt that entity."));
                                } else {
                                    playerDamager.sendMessage(Utils.color("&cYou hurt that entity because you are overriding claim protection."));
                                }
                            }
                        } else if (damager instanceof Monster) {
                            if (target instanceof Player) {
                                e.setCancelled(true);
                            }
                        }
                    }
                }
            }
        }
    }
    
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        if (e.getCause() == TeleportCause.ENDER_PEARL) {
            Territory territory = plugin.getTerritoryManager().getTerritory(e.getTo());
            if (territory != null) {
                if (territory.getPrivacy() == Privacy.PRIVATE) {
                    if (!territory.isMember(e.getPlayer())) {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        if (plugin.getSpawn().contains(e.getEntity().getLocation()) || plugin.getWarzone().contains(e.getEntity().getLocation())) {
            e.setCancelled(true);
            return;
        }
        if (e.getEntityType().equals(EntityType.CREEPER)) {
            Territory territory = getTerritory(e);
            if (territory != null) {
                e.setCancelled(true);
            }
        }
    }
    
    private Territory getTerritory(EntityEvent e) {
        return plugin.getTerritoryManager().getTerritory(e.getEntity().getLocation());
    }
    
    @EventHandler
    public void onEntityDropItem(EntityDropItemEvent e) {
        if (e.getEntity() instanceof Player) {
            Player player = ((Player) e.getEntity());
            Territory territory = getTerritory(e);
            if (!territory.isMember(player)) {
                droppedItems.put(player.getUniqueId(), e.getItemDrop());
            }
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        droppedItems.remove(e.getPlayer().getUniqueId());
    }
    
    @EventHandler
    public void onItemDespawn(ItemDespawnEvent e) {
        this.droppedItems.entrySet().removeIf(entry -> entry.getValue().getEntityId() == e.getEntity().getEntityId());
    }
    
    @EventHandler
    public void onEntityItemPickup(EntityPickupItemEvent e) {
        if (e.getEntity() instanceof Player) {
            Player player = ((Player) e.getEntity());
            
            RealmProfile profile = plugin.getProfileManager().getProfile(player);
            if (profile.getStaffMode().isActive()) {
                if (!profile.getStaffMode().canPickupItems()) {
                    e.setCancelled(true);
                    return;
                }
            }
            
            Territory territory = getTerritory(e);
            if (territory != null) {
                if (!territory.isMember(player)) {
                    if (!player.hasPermission("realms.claims.override")) {
                        if (droppedItems.containsKey(player.getUniqueId())) {
                            Item dropped = droppedItems.get(player.getUniqueId());
                            if (dropped.getEntityId() != e.getItem().getEntityId()) {
                                e.setCancelled(true);
                            } else {
                                this.droppedItems.remove(player.getUniqueId());
                            }
                        }
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onEntityTame(EntityTameEvent e) {
        if (e.getOwner() instanceof Player) {
            if (!RealmsUtils.isMember(((Player) e.getOwner()))) {
                e.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onEntitySpawn(CreatureSpawnEvent e) {
        Location location = e.getEntity().getLocation();
        if (plugin.getSpawn() != null) {
            if (plugin.getSpawn().contains(location)) {
                if (e.getEntityType() != EntityType.WITHER_SKELETON) {
                    e.setCancelled(true);
                }
            }
        }
    }
}