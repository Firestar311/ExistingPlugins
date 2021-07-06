package com.stardevmc.titanterritories.core.debug;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.ClickEvent.Action;

public enum DebugStatus {
    UNTESTED(ChatColor.YELLOW, ""), VERIFIED(ChatColor.GREEN, "Verified"), BROKEN(ChatColor.RED, "Broken");
    
    private final ChatColor color;
    private final String componentText;
    
    DebugStatus(ChatColor color, String componentText) {
        this.color = color;
        this.componentText = componentText;
    }
    
    public ChatColor getColor() {
        return color;
    }
    
    public TextComponent getComponent(DebugCmd cmd) {
        BaseComponent[] components = new ComponentBuilder("[" + componentText + "]").color(color).event(new ClickEvent(Action.RUN_COMMAND, "/debug " + cmd.getName() + " setstatus " + this.name())).bold(true).create();
        return new TextComponent(components);
    }
}