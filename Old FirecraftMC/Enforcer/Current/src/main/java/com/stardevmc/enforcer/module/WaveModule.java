package com.stardevmc.enforcer.module;

import com.stardevmc.enforcer.Enforcer;
import com.stardevmc.enforcer.manager.WaveManager;
import com.stardevmc.enforcer.modules.base.Module;
import com.stardevmc.enforcer.modules.wave.WaveCommand;
import org.bukkit.configuration.ConfigurationSection;

public class WaveModule extends Module<WaveManager> {
    
    private boolean mustHaveActiveWave, punishmentsUseWaves;
    
    public WaveModule(Enforcer plugin, String name, String... commands) {
        super(plugin, name, new WaveManager(plugin), commands);
        this.mustHaveActiveWave = false;
        this.punishmentsUseWaves = false;
    }
    
    @Override
    public void setup() {
        loadSettings();
        if (enabled) {
            manager.loadData();
        }
        WaveCommand waveCommand = new WaveCommand(plugin);
        registerCommands(waveCommand);
    }
    
    @Override
    public void desetup() {
        manager.saveData();
        registerCommands(null);
        saveSettings();
    }
    
    @Override
    protected void saveSettings() {
        ConfigurationSection settingsSecton = getSection().getConfigurationSection("settings");
        if (settingsSecton == null) {
            settingsSecton = getSection().createSection("settings");
        }
        settingsSecton.set("mustHaveActiveWave", this.mustHaveActiveWave);
        settingsSecton.set("punishmentsUseWaves", this.punishmentsUseWaves);
    }
    
    @Override
    protected void loadSettings() {
        ConfigurationSection settingsSecton = getSection().getConfigurationSection("settings");
        if (settingsSecton != null) {
            mustHaveActiveWave = settingsSecton.getBoolean("mustHaveActiveWave");
            punishmentsUseWaves = settingsSecton.getBoolean("punishmentsUseWaves");
        }
    }
    
    public boolean mustHaveActiveWave() {
        return mustHaveActiveWave;
    }
    
    public void setMustHaveActiveWave(boolean mustHaveActiveWave) {
        this.mustHaveActiveWave = mustHaveActiveWave;
    }
    
    public boolean punishmentsUseWaves() {
        return punishmentsUseWaves;
    }
    
    public void setPunishmentsUseWaves(boolean punishmentsUseWaves) {
        this.punishmentsUseWaves = punishmentsUseWaves;
    }
}