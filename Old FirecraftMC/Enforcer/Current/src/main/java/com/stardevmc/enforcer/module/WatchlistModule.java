package com.stardevmc.enforcer.module;

import com.stardevmc.enforcer.Enforcer;
import com.stardevmc.enforcer.manager.WatchlistManager;
import com.stardevmc.enforcer.modules.base.Module;
import com.stardevmc.enforcer.modules.watchlist.WatchlistCommand;
import com.stardevmc.enforcer.modules.watchlist.WatchlistListener;

public class WatchlistModule extends Module<WatchlistManager> {
    
    public WatchlistModule(Enforcer plugin, String... commands) {
        super(plugin, "Watchlist", new WatchlistManager(plugin), commands);
        this.addListenerClass(WatchlistListener.class);
    }
    
    public void setup() {
        if (enabled) {
            manager.loadData();
        }
        WatchlistCommand watchlistCommand = new WatchlistCommand();
        registerCommands(watchlistCommand);
        registerListeners();
    }
    
    public void desetup() {
        manager.saveData();
        registerCommands(null);
        registerListeners();
    }
    
    @Override
    protected void saveSettings() {
    
    }
    
    @Override
    protected void loadSettings() {
    
    }
}