package com.stardevmc.titanterritories.core;

import com.firestar311.lib.player.PlayerManager;
import com.stardevmc.titanterritories.core.chat.KingdomRoom;
import com.stardevmc.titanterritories.core.cmds.CommandManager;
import com.stardevmc.titanterritories.core.cmds.DebugExecutor;
import com.stardevmc.titanterritories.core.controller.*;
import com.stardevmc.titanterritories.core.leader.*;
import com.stardevmc.titanterritories.core.listeners.*;
import com.stardevmc.titanterritories.core.manager.*;
import com.stardevmc.titanterritories.core.objects.Lock;
import com.stardevmc.titanterritories.core.objects.changelog.ChangeLog;
import com.stardevmc.titanterritories.core.objects.holder.*;
import com.stardevmc.titanterritories.core.objects.kingdom.*;
import com.stardevmc.titanterritories.core.objects.kingdom.Mail.MailMessage;
import com.stardevmc.titanterritories.core.objects.lists.PermissionList;
import com.stardevmc.titanterritories.core.objects.member.*;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;

public final class TitanTerritories extends JavaPlugin {
    
    private static TitanTerritories instance;
    private static ChangeLog changeLog;
    private ColonyManager colonyManager;
    private KingdomManager kingdomManager;
    private MemberManager memberManager;
    private PlayerManager playerManager;
    private PlotManager plotManager;
    private RelationsManager relationsManager;
    private TownManager townManager;
    private Economy vaultEconomy;
    
    private Lock enabled = new Lock();
    
    public void onDisable() {
        if (kingdomManager != null) {
            this.kingdomManager.saveData();
        }
        
        if (plotManager != null) {
            this.plotManager.saveData();
        }
        
        if (memberManager != null) {
            this.memberManager.saveData();
        }
        
        if (relationsManager != null) {
            this.relationsManager.saveData();
        }
        
        changeLog.save();
    
        File lockFile = new File(getDataFolder() + File.separator + "lock.data");
        if (!lockFile.exists()) {
            try {
                lockFile.createNewFile();
            } catch (IOException e) {}
        }
    
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(lockFile));
            oos.writeObject(this.enabled);
            oos.flush();
            oos.close();
        } catch (Exception e) {}
    }
    
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();
        changeLog = new ChangeLog();
        
        RegisteredServiceProvider<PlayerManager> playerProvider = getServer().getServicesManager().getRegistration(PlayerManager.class);
        if (playerProvider != null) {
            this.playerManager = playerProvider.getProvider();
        } else {
            getLogger().severe("Could not find a provider for FireLib's Player Framework, disabling plugin");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(Economy.class);
        if (playerProvider != null) {
            this.vaultEconomy = economyProvider.getProvider();
        } else {
            getLogger().severe("Could not find a Vault Economy Provider, disabling plugin");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        new CommandManager(this);
        
        this.memberManager = new MemberManager();
        this.memberManager.loadData();
        
        this.kingdomManager = new KingdomManager();
        this.kingdomManager.loadData();
        
        this.townManager = new TownManager();
        this.townManager.loadData();
        
        this.colonyManager = new ColonyManager();
        this.colonyManager.loadData();
        
        this.plotManager = new PlotManager();
        this.plotManager.loadData();
        
        this.relationsManager = new RelationsManager();
        this.relationsManager.loadData();
        
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new BlockListener(), this);
        pm.registerEvents(new PlayerListener(), this);
        pm.registerEvents(new EntityListener(), this);
        pm.registerEvents(new ShopListener(), this);
        
        getCommand("debug").setExecutor(new DebugExecutor(this));
    
        File lockFile = new File(getDataFolder() + File.separator + "lock.data");
        if (!lockFile.exists()) {
            try {
                lockFile.createNewFile();
            } catch (IOException e) {}
        }
        
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(lockFile));
            this.enabled = (Lock) objectInputStream.readObject();
            objectInputStream.close();
        } catch (Exception e) {}
    }
    
    public boolean isKingdomsEnabled() {
        return enabled.getValue();
    }
    
    public PlayerManager getPlayerManager() {
        return playerManager;
    }
    
    public Economy getVaultEconomy() {
        return vaultEconomy;
    }
    
    public KingdomManager getKingdomManager() {
        return kingdomManager;
    }
    
    public PlotManager getPlotManager() {
        return plotManager;
    }
    
    public MemberManager getMemberManager() {
        return memberManager;
    }
    
    public static TitanTerritories getInstance() {
        return instance;
    }
    
    public RelationsManager getRelationsManager() {
        return relationsManager;
    }
    
    public TownManager getTownManager() {
        return this.townManager;
    }
    
    public ColonyManager getColonyManager() {
        return colonyManager;
    }
    
    public static ChangeLog getChangeLog() {
        return changeLog;
    }
    
    static {
        ConfigurationSerialization.registerClass(Member.class);
        ConfigurationSerialization.registerClass(Citizen.class);
        ConfigurationSerialization.registerClass(Colonist.class);
        ConfigurationSerialization.registerClass(Resident.class);
        ConfigurationSerialization.registerClass(PermissionList.class);
        ConfigurationSerialization.registerClass(Announcement.class);
        ConfigurationSerialization.registerClass(Election.class);
        ConfigurationSerialization.registerClass(ExperienceAction.class);
        ConfigurationSerialization.registerClass(Invite.class);
        ConfigurationSerialization.registerClass(Mail.class);
        ConfigurationSerialization.registerClass(MailMessage.class);
        ConfigurationSerialization.registerClass(Plot.class);
        ConfigurationSerialization.registerClass(Rank.class);
        ConfigurationSerialization.registerClass(Relationship.class);
        ConfigurationSerialization.registerClass(Transaction.class);
        ConfigurationSerialization.registerClass(Visit.class);
        ConfigurationSerialization.registerClass(Vote.class);
        ConfigurationSerialization.registerClass(Warp.class);
        ConfigurationSerialization.registerClass(Baron.class);
        ConfigurationSerialization.registerClass(Chief.class);
        ConfigurationSerialization.registerClass(Leader.class);
        ConfigurationSerialization.registerClass(Monarch.class);
        ConfigurationSerialization.registerClass(PlayerMonarch.class);
        ConfigurationSerialization.registerClass(ServerMonarch.class);
        ConfigurationSerialization.registerClass(AnnouncementController.class);
        ConfigurationSerialization.registerClass(EconomyController.class);
        ConfigurationSerialization.registerClass(ElectionController.class);
        ConfigurationSerialization.registerClass(EconomyController.class);
        ConfigurationSerialization.registerClass(ExperienceController.class);
        ConfigurationSerialization.registerClass(FlagController.class);
        ConfigurationSerialization.registerClass(InviteController.class);
        ConfigurationSerialization.registerClass(MailController.class);
        ConfigurationSerialization.registerClass(RankController.class);
        ConfigurationSerialization.registerClass(UserController.class);
        ConfigurationSerialization.registerClass(WarpController.class);
        ConfigurationSerialization.registerClass(Kingdom.class);
        ConfigurationSerialization.registerClass(Town.class);
        ConfigurationSerialization.registerClass(Colony.class);
        ConfigurationSerialization.registerClass(Shop.class);
        ConfigurationSerialization.registerClass(ShopController.class);
        ConfigurationSerialization.registerClass(KingdomRoom.class);
    }
    
    public void setKingdomsEnabled(boolean enabled) {
        this.enabled.setValue(enabled);
    }
}