package com.kingrealms.realms.territory.base;

import com.kingrealms.realms.plot.Plot;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.Map;
import java.util.UUID;

/**
 * Defines a territory that is a part of a Government but is not within the main claim of the government
 */
@SerializableAs("Outpost")
public abstract class Outpost extends Territory{
    public Outpost(String name) {
        super(name);
    }
    
    public Outpost(Map<String, Object> serialized) {
        super(serialized);
    }
    
    @Override
    public void createChannel() {
    
    }
    
    //TODO
    @Override
    public void addPlot(Plot plot, UUID actor, long time) {
        
    }
}