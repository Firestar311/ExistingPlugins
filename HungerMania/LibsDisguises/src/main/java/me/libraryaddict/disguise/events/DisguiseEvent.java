package me.libraryaddict.disguise.events;

import me.libraryaddict.disguise.disguisetypes.Disguise;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DisguiseEvent extends Event implements Cancellable {
    private static final HandlerList handlerList = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public CommandSender getCommandSender() {
        return commandSender;
    }

    public Disguise getDisguise() {
        return disguise;
    }

    public Entity getEntity() {
        return entity;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    private final CommandSender commandSender;
    private final Disguise disguise;
    private final Entity entity;
    private boolean cancelled;

    public DisguiseEvent(CommandSender sender, Entity entity, Disguise disguise) {
        commandSender = sender;
        this.entity = entity;
        this.disguise = disguise;
    }

    public DisguiseEvent(Entity entity, Disguise disguise) {
        this(null, entity, disguise);
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
