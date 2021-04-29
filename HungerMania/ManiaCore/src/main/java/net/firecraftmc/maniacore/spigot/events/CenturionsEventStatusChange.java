package net.firecraftmc.maniacore.spigot.events;

import lombok.Getter;
import net.firecraftmc.maniacore.api.events.EventInfo;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CenturionsEventStatusChange extends Event {
    
    private static final HandlerList handlers = new HandlerList();
    @Getter private EventInfo eventInfo;
    
    public CenturionsEventStatusChange(EventInfo eventInfo) {
        super();
        this.eventInfo = eventInfo;
    }
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    public HandlerList getHandlers() {
        return handlers;
    }
}