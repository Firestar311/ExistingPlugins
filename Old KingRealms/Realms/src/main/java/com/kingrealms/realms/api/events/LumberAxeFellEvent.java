package com.kingrealms.realms.api.events;

import com.kingrealms.realms.profile.RealmProfile;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import java.util.List;

public class LumberAxeFellEvent extends ProfileEvent implements Cancellable {
    
    private static final HandlerList handlers = new HandlerList();
    private Location location;
    private List<Block> blocks;
    
    public LumberAxeFellEvent(RealmProfile profile, Location location, List<Block> blocks) {
        super(profile);
        this.location = location;
        this.blocks = blocks;
    }
    
    public Location getLocation() {
        return location;
    }
    
    public List<Block> getBlocks() {
        return blocks;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    @Override
    public boolean isCancelled() {
        return false;
    }
    
    @Override
    public void setCancelled(boolean b) {
        
    }
}