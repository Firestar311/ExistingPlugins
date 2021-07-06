package com.stardevmc.titanterritories.core.manager;

import com.firestar311.lib.config.ConfigManager;
import com.stardevmc.titanterritories.core.TitanTerritories;
import com.stardevmc.titanterritories.core.objects.holder.Kingdom;
import com.stardevmc.titanterritories.core.objects.kingdom.Plot;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class PlotManager {
    
    private Set<Plot> plots = new HashSet<>();
    
    private ConfigManager configManager;
    
    public PlotManager() {
        this.configManager = new ConfigManager(TitanTerritories.getInstance(), "plots");
        this.configManager.setup();
    }
    
    public void loadData() {
        FileConfiguration config = configManager.getConfig();
        if (config.contains("plots")) {
            for (String c : config.getConfigurationSection("plots").getKeys(false)) {
                Plot plot = (Plot) config.get("plots." + c);
                this.plots.add(plot);
                if (plot.hasKingdom()) {
                    plot.getKingdom().getClaimController().addPlot(plot);
                }
                if (plot.hasTown()) {
                    plot.getTown().getClaimController().addPlot(plot);
                }
                if (plot.hasColony()) {
                    plot.getColony().getClaimController().addPlot(plot);
                }
            }
        }
    }
    
    public void saveData() {
        FileConfiguration config = configManager.getConfig();
        List<Plot> chunks = new ArrayList<>(this.plots);
        if (!chunks.isEmpty()) {
            for (int i = 0; i < chunks.size(); i++) {
                config.set("plots.plot" + i, chunks.get(i));
            }
        }
        configManager.saveConfig();
    }
    
    public Plot getPlot(Location location) {
        for (Plot chunk : plots) {
            if (chunk.contains(location)) {
                return chunk;
            }
        }
        Plot plot = new Plot(location);
        this.plots.add(plot);
        return plot;
    }
    
    public List<Plot> getPlots(Kingdom kingdom) {
        List<Plot> plots = new ArrayList<>();
        for (Plot plot : this.plots) {
            if (plot.getKingdom() != null && plot.getKingdom().equals(kingdom)) {
                plots.add(plot);
            }
        }
        return plots;
    }
    
    public List<Plot> getPlots() {
        return new ArrayList<>(plots);
    }
    
    public void addPlot(Plot plot) {
        this.plots.add(plot);
    }
}