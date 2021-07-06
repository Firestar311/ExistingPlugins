package com.stardevmc.enforcer.module;

import com.stardevmc.enforcer.Enforcer;
import com.stardevmc.enforcer.manager.RuleManager;
import com.stardevmc.enforcer.modules.base.Module;
import com.stardevmc.enforcer.modules.rules.MrulesCommand;
import com.stardevmc.enforcer.modules.rules.RuleCommand;

public class RuleModule extends Module<RuleManager> {
    public RuleModule(Enforcer plugin, String... commands) {
        super(plugin, "rules", new RuleManager(plugin), commands);
    }
    
    public void setup() {
        if (enabled) {
            manager.loadData();
        }
        MrulesCommand mrulesCommand = new MrulesCommand(plugin);
        RuleCommand ruleCommand = new RuleCommand(plugin);
        registerCommand("moderatorrules", mrulesCommand);
        registerCommand("rules", ruleCommand);
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