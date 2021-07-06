package com.stardevmc.enforcer.module;

import com.stardevmc.enforcer.Enforcer;
import com.stardevmc.enforcer.manager.TargetManager;
import com.stardevmc.enforcer.modules.base.Module;

public class TargetModule extends Module<TargetManager> {
    public TargetModule(Enforcer plugin, String... commands) {
        super(plugin, "target", new TargetManager(plugin), commands);
    }
    
    public void setup() {
        if (enabled) {
            manager.loadData();
        }
    }
    
    public void desetup() {
        manager.saveData();
    }
    
    @Override
    protected void saveSettings() {
    
    }
    
    @Override
    protected void loadSettings() {
    
    }
}