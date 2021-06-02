package me.libraryaddict.disguise.events;

import me.libraryaddict.disguise.disguisetypes.Disguise;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class UndisguiseEvent extends Event implements Cancellable {
    private static final HandlerList handlerList = new HandlerList();

    private final Disguise disguise;
    private final Entity disguised;
    private final boolean isBeingReplaced;
    private final CommandSender commandSender;
    private boolean isCancelled;

    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public Disguise getDisguise() {
        return disguise;
    }

    public Entity getDisguised() {
        return disguised;
    }

    public boolean isBeingReplaced() {
        return isBeingReplaced;
    }

    public CommandSender getCommandSender() {
        return commandSender;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public UndisguiseEvent(Entity entity, Disguise disguise, boolean beingReplaced) {
        this(null, entity, disguise, beingReplaced);
    }

    public UndisguiseEvent(CommandSender sender, Entity entity, Disguise disguise, boolean beingReplaced) {
        this.commandSender = sender;
        this.disguised = entity;
        this.disguise = disguise;
        this.isBeingReplaced = beingReplaced;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
