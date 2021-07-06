package com.kingrealms.realms.plot;

import com.kingrealms.realms.storage.StorageManager;
import com.starmediadev.lib.config.ConfigManager;
import com.starmediadev.lib.util.Code;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class PlotManager {
    
    private ConfigManager configManager = StorageManager.plotsConfig;
    private Map<String, Plot> plots = new HashMap<>();
    
    public PlotManager() {}
    
    public void saveData() {
        FileConfiguration config = configManager.getConfig();
        config.set("plots", null);
        for (Entry<String, Plot> entry : this.plots.entrySet()) {
            config.set("plots." + entry.getKey(), entry.getValue());
        }
        this.configManager.saveConfig();
    }
    
    public void loadData() {
        this.configManager.setup();
        FileConfiguration config = configManager.getConfig();
        ConfigurationSection plotsSection = config.getConfigurationSection("plots");
        if (plotsSection != null) {
            for (String p : plotsSection.getKeys(false)) {
                Plot plot = (Plot) plotsSection.get(p);
                this.plots.put(plot.getUniqueId(), plot);
            }
        }
    }
    
    public Plot getPlot(Location location) {
        if (!plots.isEmpty()) {
            for (Plot plot : this.plots.values()) {
                if (plot.contains(location)) {
                    return plot;
                }
            }
        }
        
        Plot plot = new Plot(location);
        this.addPlot(plot);
        return plot;
    }
    
    public void addPlot(Plot plot) {
        if (StringUtils.isEmpty(plot.getUniqueId())) {
            String id;
            do {
                id = Code.generateNewCode(8);
            } while (this.plots.containsKey(id));
            plot.setUniqueId(id);
        }
        this.plots.put(plot.getUniqueId(), plot);
    }
    
    public Plot getPlot(String id) {
        return this.plots.get(id);
    }
}