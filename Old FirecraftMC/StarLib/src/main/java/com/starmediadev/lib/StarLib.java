package com.starmediadev.lib;

import com.starmediadev.lib.cooldown.Cooldown;
import com.starmediadev.lib.cooldown.RandomizedCooldown;
import com.starmediadev.lib.region.Cuboid;
import com.starmediadev.lib.region.SelectionManager;
import com.starmediadev.lib.user.*;
import com.starmediadev.lib.user.damage.*;
import com.starmediadev.lib.util.Utils;
import com.starmediadev.lib.util.VaultIntegration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public final class StarLib extends JavaPlugin {
    
    static {
        Utils.registerConfigClasses(RandomizedCooldown.class, Cooldown.class, Cuboid.class, EntityDamagePlayerInfo.class, EntityDamageInfo.class, BlockDamageInfo.class, DamageInfo.class, ServerUUIDHistory.class, User.class, PlaySession.class, DeathSnapshot.class, ServerUser.class, ChatMessage.class, CmdMessage.class);
    }
    
    private SelectionManager selectionManager;
    private UserManager userManager;
    private VaultIntegration vault;
    
    private static StarLib INSTANCE;
    
    public void onDisable() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            User user = userManager.getUser(p.getUniqueId());
            PlaySession session = user.getCurrentSession();
            if (session == null) {
                getLogger().severe("Could not find a current play session for " + p.getName());
                return;
            }
            session.setLogoutInfo(System.currentTimeMillis(), p);
        }
        this.userManager.savePlayerData();
    }
    
    public void onEnable() {
        this.saveDefaultConfig();
        INSTANCE = this;
        
        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            try {
                this.vault = new VaultIntegration();
            } catch (Error e) {
                getLogger().severe("Vault was not found, vault based utilities will not work.");
            }
        }
        
        ServicesManager servicesManager = getServer().getServicesManager();
        this.userManager = new UserManager(this);
        this.userManager.loadPlayerData();
        getServer().getPluginManager().registerEvents(userManager, this);
        servicesManager.register(UserManager.class, userManager, this, ServicePriority.High);
        for (Player player : Bukkit.getOnlinePlayers()) {
            User user = this.userManager.getUser(player.getUniqueId());
            user.addPlaySession(new PlaySession(player.getUniqueId(), System.currentTimeMillis(), player.getLocation()));
        }
    
        new BukkitRunnable() {
            public void run() {
                userManager.savePlayerData();
            }
        }.runTaskTimerAsynchronously(this, 20L, 12000L);
        
        this.getCommand("user").setExecutor(new UserCommand(this));
        
        this.selectionManager = new SelectionManager();
        getServer().getServicesManager().register(SelectionManager.class, selectionManager, this, ServicePriority.High);
        
        if (userManager.getServerUser() == null) {
            new Thread(() -> {
                UUID generated;
                do {
                    generated = UUID.randomUUID();
                } while (userManager.getUser(generated) != null);
                userManager.setServerUser(new ServerUser(generated));
            }).start();
        } else {
            User testInfo = userManager.getUser(userManager.getServerUser().getUniqueId());
            if (testInfo instanceof ServerUser) { return; }
            if (userManager.getUser(userManager.getServerUser().getUniqueId()) != null) {
                getLogger().info("Server Info matches a player, generating a new UUID to use.");
                new Thread(() -> {
                    UUID generated;
                    do {
                        generated = UUID.randomUUID();
                    } while (userManager.getUser(generated) != null);
                    userManager.getServerUser().setUniqueId(generated);
                    getLogger().info("New UUID for Server is " + generated);
                }).start();
            }
        }
    }
    
    public UserManager getUserManager() {
        return userManager;
    }
    
    public VaultIntegration getVault() {
        return vault;
    }
    
    public SelectionManager getSelectionManager() {
        return selectionManager;
    }
    
    public static StarLib getInstance() {
        return INSTANCE;
    }
}