package com.stardevmc.enforcer.module;

import com.stardevmc.enforcer.Enforcer;
import com.stardevmc.enforcer.manager.ActorManager;
import com.stardevmc.enforcer.modules.base.Module;
import org.bukkit.configuration.ConfigurationSection;

public class ActorModule extends Module<ActorManager> {
    
    private boolean replaceActorName, anonymousActor;
    
    public ActorModule(Enforcer plugin, String... commands) {
        super(plugin, "actor", new ActorManager(plugin), commands);
        this.replaceActorName = true;
        this.anonymousActor = false;
    }
    
    public void setup() {
        loadSettings();
        if (enabled) {
            manager.loadData();
        }
    }
    
    public void desetup() {
        manager.saveData();
        saveSettings();
    }
    
    @Override
    protected void saveSettings() {
        ConfigurationSection settingsSecton = getSection().getConfigurationSection("settings");
        if (settingsSecton == null) {
            settingsSecton = getSection().createSection("settings");
        }
        settingsSecton.set("replaceActorName", this.replaceActorName);
        settingsSecton.set("anonymousActor", this.anonymousActor);
    }
    
    @Override
    protected void loadSettings() {
        ConfigurationSection settingsSecton = getSection().getConfigurationSection("settings");
        if (settingsSecton != null) {
            replaceActorName = settingsSecton.getBoolean("replaceActorName");
            anonymousActor = settingsSecton.getBoolean("anonymousActor");
        }
    }
    
    public boolean replaceActorName() {
        return replaceActorName;
    }
    
    public boolean anonymousActor() {
        return anonymousActor;
    }
    
    public void setReplaceActorName(boolean replaceActorName) {
        this.replaceActorName = replaceActorName;
    }
    
    public void setAnonymousActor(boolean anonymousActor) {
        this.anonymousActor = anonymousActor;
    }
}