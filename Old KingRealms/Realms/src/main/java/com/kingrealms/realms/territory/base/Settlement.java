package com.kingrealms.realms.territory.base;

import com.kingrealms.realms.plot.Plot;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.Map;
import java.util.UUID;

/**
 * Defines a territory that is based on a Government but has some freedom and is separate from others
 */
@SerializableAs("Settlement")
public abstract class Settlement extends Territory {
    public Settlement(String name) {
        super(name);
    }
    
    public Settlement(Map<String, Object> serialized) {
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
