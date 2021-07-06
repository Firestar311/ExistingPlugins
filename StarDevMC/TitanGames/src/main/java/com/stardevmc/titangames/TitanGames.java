package com.stardevmc.titangames;

import com.firestar311.lib.region.SelectionManager;
import com.stardevmc.titangames.arenas.Arena;
import com.stardevmc.titangames.arenas.ArenaManager;
import com.stardevmc.titangames.arenas.chests.ChestTierManger;
import com.stardevmc.titangames.arenas.listeners.*;
import com.stardevmc.titangames.command.CommandManager;
import com.stardevmc.titangames.kits.KitManager;
import com.stardevmc.titangames.kits.KitSelectorListener;
import com.stardevmc.titangames.signs.SignListener;
import com.stardevmc.titangames.signs.SignManager;
import com.stardevmc.titangames.util.Messages;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class TitanGames extends JavaPlugin {
    
    private static TitanGames instance;
    
    private ArenaManager arenaManager;
    private ChestTierManger chestTierManger;
    private SelectionManager selectionManager;
    private SignManager signManager;
    private KitManager kitManager;
    
    public void onEnable() {
        TitanGames.instance = this;
        this.saveDefaultConfig();
        Messages.prepare();
        this.getCommand("titangames").setExecutor(new CommandManager(this));
        this.arenaManager = new ArenaManager(this);
        this.arenaManager.loadArenas();
        this.chestTierManger = new ChestTierManger(this);
        this.chestTierManger.loadChestTeirs();
        this.selectionManager = new SelectionManager();
        this.signManager = new SignManager(this);
        this.signManager.loadSigns();
        this.kitManager = new KitManager(this);
        this.kitManager.loadKits();
        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(new BlockListeners(this), this);
        pm.registerEvents(new DamageListeners(this), this);
        pm.registerEvents(new PlayerListeners(this), this);
        pm.registerEvents(new SignListener(this), this);
        pm.registerEvents(new KitSelectorListener(this), this);
        //pm.registerEvents(new RegionToolListener(selectionManager, new RegionWandToolHook(this, Material.DIAMOND_SWORD)), this);
        
        for (Arena arena : this.arenaManager.getArenas()) {
            arena.allowPlayers();
        }
    }
    
    public void onDisable() {
        this.arenaManager.saveArenas();
        this.chestTierManger.saveChestTeirs();
        this.signManager.saveSigns();
        this.kitManager.saveKits();
    }
    
    public static TitanGames getInstance() {
        return instance;
    }
    
    public ArenaManager getArenaManager() {
        return arenaManager;
    }
    
    public ChestTierManger getChestTierManger() {
        return chestTierManger;
    }
    
    public SelectionManager getSelectionManager() {
        return selectionManager;
    }
    
    public SignManager getSignManager() {
        return signManager;
    }
    
    public KitManager getKitManager() {
        return kitManager;
    }
}