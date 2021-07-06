package com.kingrealms.realms.tasks;

import com.starmediadev.lib.workload.Workload;
import org.bukkit.Location;
import org.bukkit.Material;

public class BlockUpdate implements Workload {
    
    private Location location;
    private Material type;
    
    public BlockUpdate(Location location, Material type) {
        this.location = location;
        this.type = type;
    }
    
    @Override
    public void compute() {
        location.getBlock().setType(type);
    }
}