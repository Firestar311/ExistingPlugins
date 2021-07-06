package com.stardevmc.enforcer.module;

import com.stardevmc.enforcer.Enforcer;
import com.stardevmc.enforcer.manager.PrisonManager;
import com.stardevmc.enforcer.modules.base.Module;
import com.stardevmc.enforcer.modules.prison.PlayerPrisonListener;
import com.stardevmc.enforcer.modules.prison.PrisonCommand;

public class PrisonModule extends Module<PrisonManager> {
    public PrisonModule(Enforcer plugin, String... commands) {
        super(plugin, "prisons", new PrisonManager(plugin), commands);
        this.addListenerClass(PlayerPrisonListener.class);
    }
    
    
    public void setup() {
        if (enabled) {
            manager.loadData();
        }
        PrisonCommand prisonCommand = new PrisonCommand(plugin);
        registerCommands(prisonCommand);
        registerListeners();
    }
    
    public void desetup() {
        manager.saveData();
        registerCommands(null);
        unregisterListeners();
    }
    
    @Override
    protected void saveSettings() {
    
    }
    
    @Override
    protected void loadSettings() {
    
    }
}