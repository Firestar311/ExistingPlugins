package com.firestar311.lib;

import com.firestar311.lib.chat.menu.ChatMenuAPI;
import com.firestar311.lib.customitems.CustomItems;
import com.firestar311.lib.customitems.api.IItemManager;
import com.firestar311.lib.player.*;
import com.firestar311.lib.region.*;
import com.firestar311.lib.sql.DBType;
import com.firestar311.lib.sql.SQLManager;
import com.firestar311.lib.sql.test.TestObject;
import com.firestar311.lib.sql.test.TestRecord;
import com.firestar311.lib.superadmins.SuperAdminCommand;
import com.firestar311.lib.superadmins.SuperAdminManager;
import com.firestar311.lib.util.Utils;
import com.firestar311.lib.util.VaultIntegration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public final class FireLib extends JavaPlugin implements Listener {
    
    static {
        Utils.registerConfigClasses(ServerUUIDHistory.class, User.class, PlaySession.class, DeathSnapshot.class);
    }

    private VaultIntegration vault;
    private PlayerManager playerManager;
    private CustomItems customItems;
    private SuperAdminManager superAdminManager;
    private SelectionManager selectionManager;
    
    private boolean reload = false;
    
    public void onDisable() {
        if (!reload) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                User user = playerManager.getUser(p.getUniqueId());
                user.getCurrentSession().setLogoutInfo(System.currentTimeMillis(), p);
            }
        }
        this.playerManager.savePlayerData();
        superAdminManager.saveData();
        ChatMenuAPI.disable();
        reload = false;
    }
    
    @EventHandler
    public void onServerCommand(ServerCommandEvent e) {
        if (e.getCommand().contains("/reload")) {
            this.reload = true;
        }
    }
    
    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e) {
        if (e.getMessage().contains("/reload")) {
            this.reload = true;
        }
    }
    
    public void onEnable() {
        this.saveDefaultConfig();
        //getServer().getPluginManager().registerEvents(this, this);
        try {
            this.vault = new VaultIntegration(this);
        } catch (Exception e) {
            getLogger().severe("Vault was not found, vault based utilities will not work.");
        }
    
        ServicesManager servicesManager = getServer().getServicesManager();
        this.playerManager = new PlayerManager(this);
        this.playerManager.loadPlayerData();
        getServer().getPluginManager().registerEvents(playerManager, this);
        servicesManager.register(PlayerManager.class, playerManager, this, ServicePriority.High);
        
        superAdminManager = new SuperAdminManager(this);
        if (getConfig().getBoolean("superadmins.enabled")) {
            superAdminManager.setEnabled(true);
            superAdminManager.loadData();
        }
        if (getConfig().getBoolean("superadmins.global")) {
            getCommand("superadmins").setExecutor(new SuperAdminCommand(superAdminManager));
            superAdminManager.setGlobal(true);
        }
        
        customItems = new CustomItems(this);
        servicesManager.register(IItemManager.class, customItems.getItemManager(), this, ServicePriority.High);
        
        this.selectionManager = new SelectionManager();
        getServer().getServicesManager().register(SelectionManager.class, selectionManager, this, ServicePriority.High);
        getServer().getPluginManager().registerEvents(new RegionToolListener(selectionManager, new RegionWandToolHook(this, Material.COBBLESTONE_WALL)), this);
    
        ChatMenuAPI.init(this);
        
        if (playerManager.getServerUser() == null) {
            new Thread(() -> {
                UUID generated;
                do {
                    generated = UUID.randomUUID();
                } while (playerManager.getUser(generated) != null);
                playerManager.setServerUser(new ServerUser(generated));
            }).start();
        } else {
            User testInfo = playerManager.getUser(playerManager.getServerUser().getUniqueId());
            if (testInfo instanceof ServerUser) return;
            if (playerManager.getUser(playerManager.getServerUser().getUniqueId()) != null) {
                getLogger().info("Server Info matches a player, generating a new UUID to use.");
                new Thread(() -> {
                    UUID generated;
                    do {
                        generated = UUID.randomUUID();
                    } while (playerManager.getUser(generated) != null);
                    playerManager.getServerUser().setUniqueId(generated);
                    getLogger().info("New UUID for Server is " + generated);
                }).start();
            }
        }
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("testsql")) {
            SQLManager sqlManager = new SQLManager(this, DBType.MYSQL, getConfig().getConfigurationSection("database"));
            sqlManager.registerTableRecord(TestRecord.class);
            sqlManager.createTables(success -> {
                sqlManager.addRecordToQueue(new TestRecord(new TestObject("test1", 0, System.currentTimeMillis(), UUID.randomUUID(), true)));
                sqlManager.pushQueueChanges();
            });
        }
        
        return true;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        setPrefix(e.getPlayer());
    }
    
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        setPrefix(e.getPlayer());
        e.setFormat(Utils.color(e.getPlayer().getDisplayName() + "&8: &f" + e.getMessage()));
    }
    
    private void setPrefix(Player player) {
        String prefix = vault.getChat()
                             .getGroupPrefix(player.getWorld(), vault.getPermission().getPrimaryGroup(player));
        String displayName = Utils.color(prefix + player.getName());
        player.setDisplayName(displayName);
    }
    
    public VaultIntegration getVault() {
        return vault;
    }
    
    public PlayerManager getPlayerManager() {
        return playerManager;
    }
    
    public IItemManager getItemManager() {
        return customItems.getItemManager();
    }
    
    public SelectionManager getSelectionManager() {
        return selectionManager;
    }
}