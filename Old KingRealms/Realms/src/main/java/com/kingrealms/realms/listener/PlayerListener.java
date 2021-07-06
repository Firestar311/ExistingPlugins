package com.kingrealms.realms.listener;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.api.events.RealmsAPI;
import com.kingrealms.realms.graves.Grave;
import com.kingrealms.realms.home.DeathHome;
import com.kingrealms.realms.home.Home;
import com.kingrealms.realms.profile.RealmProfile;
import com.kingrealms.realms.profile.board.PrimaryBoard;
import com.kingrealms.realms.spawners.CustomSpawner;
import com.kingrealms.realms.spawners.gui.SpawnerGui;
import com.kingrealms.realms.territory.base.Territory;
import com.kingrealms.realms.territory.base.member.Member;
import com.kingrealms.realms.territory.enums.Privacy;
import com.kingrealms.realms.util.RealmsUtils;
import com.starmediadev.lib.items.NBTWrapper;
import com.starmediadev.lib.util.Utils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.data.type.Bed;
import org.bukkit.entity.EntityType;
import org.bukkit.event.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerListener implements Listener {
    
    private final Realms plugin = Realms.getInstance();
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        RealmProfile profile = plugin.getProfileManager().getProfile(e.getPlayer().getUniqueId());
        if (profile == null) {
            plugin.getProfileManager().addProfile(new RealmProfile(plugin.getUserManager().getUser(e.getPlayer().getUniqueId())));
        }
        
        if (plugin.isMaintenance()) {
            if (!e.getPlayer().hasPermission("realms.maintenance.ignore")) {
                e.getPlayer().setGameMode(GameMode.SPECTATOR);
            }
            
            profile.sendMessage("&c&lKingRealms is in Maintenance Mode, it is view only.");
        } else {
            profile.setDisplayBoard(new PrimaryBoard(e.getPlayer()));
            Territory locTerritory = plugin.getTerritoryManager().getTerritory(e.getPlayer().getLocation());
            if (locTerritory != null) {
                if (locTerritory.getPrivacy() == Privacy.PRIVATE) {
                    if (!locTerritory.isMember(e.getPlayer())) {
                        if (!e.getPlayer().hasPermission("realms.staff.claim.override")) {
                            e.getPlayer().teleport(plugin.getSpawn().getSpawnpoint());
                            e.getPlayer().sendMessage(Utils.color("&eYou have been teleported to the &dSpawn &eas the plot you were in is claimed by &b" + locTerritory.getName()));
                        }
                    }
                }
            }
            
            if (!e.getPlayer().hasPlayedBefore()) {
                new BukkitRunnable() {
                    public void run() {
                        e.getPlayer().teleport(plugin.getSpawn().getSpawnpoint());
                    }
                }.runTaskLater(plugin, 1);
            }
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        if (plugin.isMaintenance()) {
            if (!e.getPlayer().hasPermission("realms.maintenance.ignore")) {
                e.getPlayer().setGameMode(GameMode.SURVIVAL);
            }
        }
    }
    
    
    @EventHandler
    public void onPlayerBucketEmptyEvent(PlayerBucketEmptyEvent e) {
        handleBucketEvent(e);
    }
    
    @EventHandler
    public void onPlayerBucketFillEvent(PlayerBucketFillEvent e) {
        handleBucketEvent(e);
    }
    
    private void handleBucketEvent(PlayerBucketEvent e) {
        if (plugin.getSpawn().contains(e.getPlayer().getLocation()) || plugin.getWarzone().contains(e.getPlayer().getLocation())) {
            if (!e.getPlayer().hasPermission("realms.serverclaims.override")) {
                e.setCancelled(true);
                return;
            }
        }
        if (!RealmsUtils.isMember(e.getPlayer())) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Block clickedBlock = e.getClickedBlock();
            RealmProfile profile = RealmsAPI.getProfile(e.getPlayer());
            if (clickedBlock != null) {
                Grave grave = plugin.getGraveManager().getGrave(clickedBlock.getLocation());
                if (grave != null) {
                    if (!grave.getPlayer().equals(e.getPlayer().getUniqueId())) {
                        e.getPlayer().sendMessage(Utils.color("&cThat grave is not yours."));
                        return;
                    }
                    
                    grave.claimGrave(e.getPlayer());
                    e.getPlayer().sendMessage(Utils.color("&eYou have claimed your grave."));
                    plugin.getGraveManager().removeGrave(clickedBlock.getLocation());
                    return;
                } else {
                    if (clickedBlock.getState() instanceof CreatureSpawner) {
                        CustomSpawner spawner = plugin.getSpawnerManager().getSpawner(clickedBlock.getLocation());
                        if (spawner == null) { return; }
                        
                        ItemStack heldItem = e.getPlayer().getInventory().getItemInMainHand();
                        if (heldItem == null || heldItem.getType().equals(Material.AIR)) {
                            new SpawnerGui(spawner).openGUI(e.getPlayer());
                            return;
                        }
                        
                        String spawnertype;
                        try {
                            spawnertype = NBTWrapper.getNBTString(heldItem, "spawnertype");
                        } catch (Exception exception) {
                            return;
                        }
                        
                        EntityType type;
                        try {
                            type = EntityType.valueOf(spawnertype);
                        } catch (Exception ex) {
                            return;
                        }
                        
                        if (spawner.getEntityType() != type) { return; }
                        
                        if (!e.getPlayer().isSneaking()) {
                            if (spawner.getAmount() + 1 > 100) {
                                e.getPlayer().sendMessage(Utils.color("&cThe spawner stack size is already at max size."));
                                return;
                            }
                            spawner.setAmount(spawner.getAmount() + 1);
                            ItemStack item = heldItem.clone();
                            item.setAmount(1);
                            e.getPlayer().getInventory().removeItem(item);
                        } else {
                            int amount;
                            if (spawner.getAmount() + heldItem.getAmount() > 100) {
                               amount = 100 - spawner.getAmount();
                            } else {
                                amount = heldItem.getAmount();
                            }
                            
                            spawner.setAmount(spawner.getAmount() + amount);
                            heldItem.setAmount(amount);
                            e.getPlayer().getInventory().removeItem(heldItem);
                        }
                        
                        profile.sendMessage("&gSpawner stack size is now &h" + spawner.getAmount());
                        e.setCancelled(true);
                        return;
                    }
                }
            }
            
            if (!RealmsUtils.isMember(e.getPlayer())) {
                e.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Location from = e.getFrom(), to = e.getTo();
        if (from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ()) {
            return;
        }
    
        if (!plugin.getSeason().isActive()) {
            if (!e.getPlayer().hasPermission("realms.staff")) {
                if (!plugin.getSpawn().contains(e.getTo())) {
                    e.getPlayer().teleport(e.getFrom());
                    e.getPlayer().sendMessage(Utils.color("&cYou cannot leave spawn until the season is activated."));
                }
            }
        }
    
        Territory toTerritory = plugin.getTerritoryManager().getTerritory(e.getTo());
        RealmProfile profile = plugin.getProfileManager().getProfile(e.getPlayer());
        if (toTerritory != null) {
            if (!toTerritory.canEnter(profile)) {
                if (!e.getPlayer().hasPermission("realms.staff.claim.override") || profile.getStaffMode().isActive()) {
                    if (!e.getPlayer().hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                        e.getPlayer().teleport(e.getFrom());
                    } else {
                        boolean containsStaff = false;
                        for (Member toMember : toTerritory.getMembers()) {
                            if (toMember.getRealmProfile().hasPermission("realms.staff")) {
                                containsStaff = true;
                                break;
                            }
                        }
    
                        if (containsStaff) {
                            e.getPlayer().teleport(e.getFrom());
                            e.getPlayer().sendMessage(Utils.color("&cYou cannot enter that claim with invisibility as it contains a staff member. This is to help combat targeters."));
                            e.getPlayer().sendMessage(Utils.color("&cThis system is temporary until a better one is implemented."));
                        }
                    }
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent e) {
        if (!plugin.getWarzone().contains(e.getEntity().getLocation())) {
            plugin.getGraveManager().createGrave(e.getEntity());
        }
        RealmProfile profile = plugin.getProfileManager().getProfile(e.getEntity());
        profile.setDeathHome(e.getEntity().getLocation());
        e.getDrops().clear();
        e.setDroppedExp(0);
        profile.sendMessage("&gYou have died, a home has been created called &h" + DeathHome.NAME);
        Home home = profile.getHome("bed");
        if (home == null) {
            home = profile.getHome("home");
        }
        
        Location respawn;
        if (home != null) respawn = home.getLocation();
        else {
            Territory territory = plugin.getTerritoryManager().getTerritory(profile);
            if (territory != null) {
                respawn = territory.getSpawnpoint();
            } else {
                respawn = plugin.getSpawn().getSpawnpoint();
            }
        }
        
        new BukkitRunnable() {
            public void run() {
                e.getEntity().teleport(respawn);
            }
        }.runTaskLater(plugin, 1L);
    }
    
    @EventHandler
    public void onPlayerBedInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = e.getClickedBlock();
        if (block.getBlockData() instanceof Bed) {
            RealmProfile profile = RealmsAPI.getProfile(e.getPlayer());
            Home bed = profile.getHome("bed");
            if (bed != null) {
                bed.setLocation(block.getLocation().add(0, 1, 0));
            } else {
                profile.addHome(new Home(profile.getUniqueId(), "bed", block.getLocation().add(0, 1, 0), System.currentTimeMillis()));
            }
        }
    }
    
    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent e) {
        if (e.getView().getTitle().contains("Tier")) {
            if (e.getRawSlot() == e.getSlot()) {
                e.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onCommandPreProcess(PlayerCommandPreprocessEvent e) {
        if (e.getMessage().startsWith("/give") || e.getMessage().startsWith("/minecraft:give")) {
            RealmProfile profile = RealmsAPI.getProfile(e.getPlayer());
            if (profile.isActiveQuestLine(Realms.getInstance().getQuestManager().getNetherQuestLine())) {
                profile.sendMessage("&4&lPortal Keeper &cYou cannot give yourself items while doing the nether quest. Progress Reset!");
                profile.resetProgress(Realms.getInstance().getQuestManager().getNetherQuestLine());
            }
        }
    }
}