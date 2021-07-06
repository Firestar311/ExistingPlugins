package com.stardevmc.enforcer.manager;

import com.stardevmc.enforcer.Enforcer;
import com.stardevmc.enforcer.modules.base.Manager;
import com.stardevmc.enforcer.objects.actor.*;
import com.stardevmc.enforcer.objects.wave.Wave;
import com.stardevmc.enforcer.objects.wave.Wave.Type;
import com.stardevmc.enforcer.util.Code;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class WaveManager extends Manager {
    
    private Map<String, Wave> waves = new HashMap<>();
    
    private Wave currentWave;
    
    public WaveManager(Enforcer plugin) {
        super(plugin, "waves");
        this.configManager.setup();
    }
    
    protected Wave createWave(Actor creator, Type type) {
        String id;
        do {
            id = Code.generateNewCode(6, false);
        } while (waves.containsKey(id));
        
        Wave wave = new Wave(id, creator, type);
        this.waves.put(id, wave);
    
        if (plugin.getWaveModule().mustHaveActiveWave()) {
            if (currentWave == null) {
                currentWave = wave;
            }
        }
        
        return wave;
    }
    
    public Wave createWave(Actor actor) {
        if (actor instanceof PlayerActor) {
            return createWave((PlayerActor) actor);
        } else if (actor instanceof ConsoleActor) {
            return createWave((ConsoleActor) actor);
        }
        return null;
    }
    
    public Wave createWave(PlayerActor creator) {
        return createWave(creator, Type.MANUAL);
    }
    
    public Wave createWave(ConsoleActor creator) {
        return createWave(creator, Type.AUTOMATIC);
    }
    
    public Wave getWave(String id) {
        if (currentWave != null) {
            if (id.equalsIgnoreCase("active") || id.equalsIgnoreCase("current") || this.currentWave.getId().equalsIgnoreCase(id)) {
                return currentWave;
            }
        }
        return this.waves.get(id);
    }
    
    public Map<String, Wave> getWaves() {
        return waves;
    }
    
    @Override
    public void saveData() {
        FileConfiguration config = this.configManager.getConfig();
        for (Entry<String, Wave> entry : waves.entrySet()) {
            config.set("waves." + entry.getKey(), entry.getValue());
        }
        
        if (currentWave != null) {
            config.set("current", this.currentWave);
        }
        
        this.configManager.saveConfig();
    }
    
    @Override
    public void loadData() {
        FileConfiguration config = this.configManager.getConfig();
        ConfigurationSection waveSection = config.getConfigurationSection("waves");
        if (waveSection == null) return;
        for (String w : waveSection.getKeys(false)) {
            Wave wave = (Wave) waveSection.get(w);
            this.waves.put(w, wave);
        }
        
        if (config.contains("current")) {
            this.currentWave = (Wave) config.get("current");
        }
    
        if (plugin.getWaveModule().mustHaveActiveWave()) {
            if (this.currentWave == null) {
                this.currentWave = createWave(plugin.getActorModule().getManager().getActor("Console"));
            }
        }
    }
    
    public Wave getCurrentWave() {
        return currentWave;
    }
    
    public void setCurrentWave(Wave currentWave) {
        this.currentWave = currentWave;
    }
}