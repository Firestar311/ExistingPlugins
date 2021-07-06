package com.stardevmc.titanterritories.core.controller;

import com.firestar311.lib.util.Utils;
import com.stardevmc.titanterritories.core.TitanTerritories;
import com.stardevmc.titanterritories.core.objects.enums.Permission;
import com.stardevmc.titanterritories.core.objects.holder.*;
import com.stardevmc.titanterritories.core.objects.interfaces.IHolder;
import com.stardevmc.titanterritories.core.objects.interfaces.IUser;
import com.stardevmc.titanterritories.core.objects.kingdom.ClaimResponse;
import com.stardevmc.titanterritories.core.objects.kingdom.Plot;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;

import java.util.*;

public class ClaimController<T extends IHolder> extends Controller<T> {
    
    private Set<Plot> plots = new HashSet<>();
    
    private TitanTerritories plugin = TitanTerritories.getInstance();
    
    public ClaimController(T holder) {
        super(holder);
    }
    
    public void handleCommand(Command cmd, IHolder holder, IUser user, String[] args) {
        if (Utils.checkCmdAliases(args, 0, "claim", "c")) {
            if (!user.hasPermission(Permission.CLAIM)) {
                user.sendMessage(Utils.color("&cYou are not allowed to claim territory for your Kingdom"));
                return;
            }
            
            Plot plot = plugin.getPlotManager().getPlot(user.getLocation());
            ClaimResponse response = holder.claim(user, plot);
            
            if (response.isSuccess()) {
                addPlot(plot);
                holder.sendMemberMessage(user.getName() + " has claimed the plot " + plot.toString());
            } else {
                user.sendMessage("&cThere was a problem with claiming that plot: " + response.getMessage());
            }
        } else if (Utils.checkCmdAliases(args, 0, "unclaim", "uc")) {
            if (!user.hasPermission(Permission.UNCLAIM)) {
                user.sendMessage(Utils.color("&cYou are not allowed to unclaim territory in your " + holder.getClass().getSimpleName()));
                return;
            }
    
            if (getPlot(user.getLocation()) == null) {
                user.sendMessage(Utils.color("&cYou are not in a plot claimed by your " + holder.getClass().getSimpleName()));
                return;
            }
    
            Plot plot = plugin.getPlotManager().getPlot(user.getLocation());
            if (plot.contains(holder.getSpawnpoint())) {
                user.sendMessage("&cYou cannot unclaim the plot that contains the spawnpoint");
                return;
            }
            removePlot(user.getLocation());
            holder.sendMemberMessage(user.getName() + " has unclaimed the plot " + plot.toString());
        }
    }
    
    public void addPlot(Plot plot) {
        this.plots.add(plot);
        if (this.getHolder() instanceof Kingdom) {
            plot.setKingdom(((Kingdom) this.getHolder()));
        } else if (this.getHolder() instanceof Town) {
            plot.setTown((Town) holder);
        } else if (this.getHolder() instanceof Colony) {
            plot.setColony((Colony) holder);
        }
        
        if (TitanTerritories.getInstance().getPlotManager() != null) {
            if (!TitanTerritories.getInstance().getPlotManager().getPlots().contains(plot)) {
                TitanTerritories.getInstance().getPlotManager().addPlot(plot);
            }
        }
    }
    
    public Plot getPlot(Location location) {
        for (Plot chunk : plots) {
            if (chunk.contains(location)) {
                return chunk;
            }
        }
        return null;
    }
    
    public void removePlot(Location location) {
        Plot chunk = getPlot(location);
        this.plots.remove(chunk);
        chunk.setKingdom(null);
    }
    
    public boolean canBuild(UUID uuid) {
        IUser user = holder.getUserController().get(uuid);
        if (user == null) { return false; }
        if (!user.hasPermission(Permission.BUILD)) {
            return false;
        }
        
        Plot plot = getPlot(user.getLocation());
        if (plot.hasTown()) {
            return plot.getTown().getUserController().get(uuid).hasPermission(Permission.BUILD);
        }
        
        if (plot.hasColony()) {
            return plot.getColony().getUserController().get(uuid).hasPermission(Permission.BUILD);
        }
        return true;
    }
    
    public boolean contains(Player player) {
        return contains(player.getLocation());
    }
    
    public boolean contains(Location location) {
        for (Plot chunk : plots) {
            if (chunk.contains(location)) {
                return true;
            }
        }
        
        return false;
    }
    
    public List<Plot> getPlots() {
        return new ArrayList<>(plots);
    }
    
    public Map<String, Object> serialize() {
        return new HashMap<>();
    }
    
    public static ClaimController deserialize(Map<String, Object> serialized) {
        return null;
    }
}