package com.kingrealms.realms.plot.claimed;

import com.kingrealms.realms.plot.Plot;
import com.kingrealms.realms.territory.medievil.Hamlet;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.Map;
import java.util.UUID;

@SerializableAs("HamletPlot")
public class HamletPlot extends ClaimedPlot {
    public HamletPlot(Hamlet hamlet, Plot plot, UUID actor, long date) {
        super(hamlet, plot, actor, date);
    }
    
    public HamletPlot(Hamlet hamlet, String plot, UUID actor, long date) {
        super(hamlet, plot, actor, date);
    }
    
    public HamletPlot(Map<String, Object> serialized) {
        super(serialized);
    }
    
    @Override
    public Hamlet getOwner() {
        return (Hamlet) super.getOwner();
    }
}