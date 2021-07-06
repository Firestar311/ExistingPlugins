package com.stardevmc.titanvanish;


import com.firestar311.lib.player.User;
import com.firestar311.lib.util.ActionBar;
import com.firestar311.lib.util.Utils;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class VanishManager implements Listener {
    
    private TitanVanish plugin;
    
    private VanishInfo defaultSettings;
    
    private ActionBar actionBar = new ActionBar("&fYou are currently &9VANISHED");
    
    public VanishManager(TitanVanish plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        
        ConfigurationSection defaultSection = plugin.getConfig().getConfigurationSection("defaults.vanishsettings");
        if (defaultSection != null) {
            boolean itemPickup, itemDrop, entityInteract, damage, chat, collision, entityTarget, blockBreak, blockPlace, silent, fly, nightVision;
            itemPickup = defaultSection.getBoolean("itemPickup");
            itemDrop = defaultSection.getBoolean("itemDrop");
            entityInteract = defaultSection.getBoolean("entityInteract");
            damage = defaultSection.getBoolean("damage");
            chat = defaultSection.getBoolean("chat");
            collision = defaultSection.getBoolean("collision");
            entityTarget = defaultSection.getBoolean("entityTarget");
            blockBreak = defaultSection.getBoolean("blockBreak");
            blockPlace = defaultSection.getBoolean("blockPlace");
            silent = defaultSection.getBoolean("silent");
            fly = defaultSection.getBoolean("fly");
            nightVision = defaultSection.getBoolean("nightVision");
            
            defaultSettings = new VanishInfo(itemPickup, itemDrop, entityInteract, damage, chat, collision, entityTarget, blockBreak, blockPlace, silent, fly, nightVision);
        }
        
        new BukkitRunnable() {
            public void run() {
                if (!Bukkit.getOnlinePlayers().isEmpty()) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (isVanished(player)) {
                            actionBar.send(player);
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 20L, 40L);
    }
    
    private VanishInfo getVanishInfo(Player player) {
        User user = plugin.getPlayerManager().getUser(player.getUniqueId());
        if (user == null) return null;
        return null; //TODO
    }
    
    public boolean isVanished(Player player) {
        VanishInfo vanishInfo = getVanishInfo(player);
        if (vanishInfo == null) return false;
        return vanishInfo.isActive();
    }
    
    public boolean isVanished(VanishInfo vanishInfo) {
        if (vanishInfo == null) return false;
        return vanishInfo.isActive();
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        VanishInfo vanishInfo = getVanishInfo(player);
        if (!isVanished(vanishInfo)) { return; }
        if (!vanishInfo.canBlockPlace()) {
            e.setCancelled(true);
            player.sendMessage(Utils.color("&cYou cannot place blocks while vanished."));
        }
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        VanishInfo vanishInfo = getVanishInfo(player);
        if (!isVanished(vanishInfo)) { return; }
        if (!vanishInfo.canBlockBreak()) {
            e.setCancelled(true);
            player.sendMessage(Utils.color("&cYou cannot break blocks while vanished."));
        }
    }
    
    @EventHandler
    public void onEntityTarget(EntityTargetLivingEntityEvent e) {
        if (!(e.getTarget() instanceof Player)) return;
        Player player = (Player) e.getTarget();
        VanishInfo vanishInfo = getVanishInfo(player);
        if (!isVanished(vanishInfo)) { return; }
        if (!vanishInfo.canEntityTarget()) {
            e.setCancelled(true);
        }
        
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        VanishInfo vanishInfo = getVanishInfo(player);
        if (!isVanished(vanishInfo)) { return; }
        if (!vanishInfo.canChat()) {
            e.setCancelled(true);
            player.sendMessage(Utils.color("&cYou cannot chat while vanished."));
        }
    }
    
    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) { return; }
        VanishInfo vanishInfo = getVanishInfo(((Player) e.getDamager()));
        if (!isVanished(vanishInfo)) { return; }
        if (!vanishInfo.canDamage()) {
            e.setCancelled(true);
            e.getDamager().sendMessage(Utils.color("&cYou cannot damage entities while vanished."));
        }
    }
    
    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) { return; }
        VanishInfo vanishInfo = getVanishInfo(((Player) e.getEntity()));
        if (!isVanished(vanishInfo)) { return; }
        if (!vanishInfo.canDamage()) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onItemPickup(EntityPickupItemEvent e) {
        if (!(e.getEntity() instanceof Player)) { return; }
        VanishInfo vanishInfo = getVanishInfo(((Player) e.getEntity()));
        if (!isVanished(vanishInfo)) { return; }
        if (!vanishInfo.canItemPickup()) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onItemDrop(PlayerDropItemEvent e) {
        Player player = e.getPlayer();
        VanishInfo vanishInfo = getVanishInfo(e.getPlayer());
        if (!isVanished(vanishInfo)) { return; }
        if (!vanishInfo.canDropItems()) {
            e.setCancelled(true);
            player.sendMessage(Utils.color("&cYou are cannot drop items while vanished."));
        }
    }
    
    @EventHandler
    public void onEntityInteract(PlayerInteractAtEntityEvent e) {
        Player player = e.getPlayer();
        VanishInfo vanishInfo = getVanishInfo(player);
        if (!isVanished(vanishInfo)) { return; }
        if (!vanishInfo.canEntityInteract()) {
            e.setCancelled(true);
            player.sendMessage(Utils.color("&cYou cannot interact with entities while vanished."));
        }
    }
    
    public void setVanish(Player player) {
        User info = plugin.getPlayerManager().getUser(player.getUniqueId());
        //TODO
        
        player.sendMessage(Utils.color("&aYou have enabled vanish mode!"));
        actionBar.send(player);
    }
    
    public void removeVanish(Player player) {
        User info = plugin.getPlayerManager().getUser(player.getUniqueId());
        //TODO VanishInfo settings = ((VanishInfo) info.getCustomValue("vanish"));
        //settings.setActive(false);
        
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.showPlayer(plugin, player);
        }
        
        player.setCollidable(true);
        if (player.getGameMode().equals(GameMode.SURVIVAL) || player.getGameMode().equals(GameMode.ADVENTURE)) {
            player.setAllowFlight(false);
            player.setFlying(false);
        }
        
        player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        player.sendMessage(Utils.color("&aYou have disabled vanish mode!"));
    }
    
    private boolean canSee(Player vanished, Player online) {
        try {
            Permission perms = plugin.getVaultPermission();
            String groupName = perms.getPrimaryGroup(vanished.getWorld().getName(), vanished).toLowerCase();
            if (groupName != null && !groupName.equals("")) {
                return online.hasPermission("titanvanish.rank.*") || online.hasPermission("titanvanish.rank." + groupName);
            }
        } catch (Exception ignored) {}
        return true;
    }
    
    public VanishInfo getDefaultSettings() {
        return defaultSettings;
    }
}