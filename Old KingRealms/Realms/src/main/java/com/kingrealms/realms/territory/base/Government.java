package com.kingrealms.realms.territory.base;

import com.kingrealms.realms.plot.Plot;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.Map;
import java.util.UUID;

/**
 * Defines a territory that is a base government that controls what is and what is not allowed.
 */
@SerializableAs("Government")
public abstract class Government extends Territory {
    public Government(String name) {
        super(name);
    }
    
    public Government(Map<String, Object> serialized) {
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