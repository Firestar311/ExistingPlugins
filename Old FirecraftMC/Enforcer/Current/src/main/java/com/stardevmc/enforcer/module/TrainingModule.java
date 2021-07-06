package com.stardevmc.enforcer.module;

import com.stardevmc.enforcer.Enforcer;
import com.stardevmc.enforcer.manager.TrainingManager;
import com.stardevmc.enforcer.modules.base.Module;
import com.stardevmc.enforcer.modules.training.TrainingCommand;

public class TrainingModule extends Module<TrainingManager> {
    public TrainingModule(Enforcer plugin, String... commands) {
        super(plugin, "training", new TrainingManager(plugin), commands);
    }
    
    public void setup() {
        if (enabled) {
            manager.loadData();
        }
        
        TrainingCommand trainingCommand = new TrainingCommand(plugin);
        registerCommands(trainingCommand);
    }
    
    public void desetup() {
        manager.saveData();
        registerCommands(null);
    }
    
    @Override
    protected void saveSettings() {
    
    }
    
    @Override
    protected void loadSettings() {
    
    }
}