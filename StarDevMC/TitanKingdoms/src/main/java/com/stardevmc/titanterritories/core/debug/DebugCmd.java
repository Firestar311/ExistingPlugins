package com.stardevmc.titanterritories.core.debug;

import com.firestar311.lib.pagination.IElement;
import com.stardevmc.titanterritories.core.cmds.DebugExecutor;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.ClickEvent.Action;

import java.util.Objects;

public class DebugCmd implements IElement, Comparable<DebugCmd> {
    
    private String baseCommand;
    private String name;
    private int order;
    private DebugStatus status = DebugStatus.UNTESTED;
    
    public DebugCmd(String name, String baseCommand, int order) {
        this.name = name;
        this.baseCommand = baseCommand;
        this.order = order;
    }
    
    public int compareTo(DebugCmd o) {
        return Integer.compare(order, o.order);
    }
    
    public int hashCode() {
        return Objects.hash(name);
    }
    
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        DebugCmd debugCmd = (DebugCmd) o;
        return name.equals(debugCmd.name);
    }
    
    public int getOrder() {
        return order;
    }
    
    public String getName() {
        return name;
    }
    
    public TextComponent formatLineAsTextComponent(String... args) {
        DebugExecutor.variables.forEach((k, v) -> {
            if (v != null) {
                baseCommand = baseCommand.replace(k, v);
            }
        });
        ComponentBuilder componentBuilder = new ComponentBuilder(baseCommand).color(status.getColor()).event(new ClickEvent(Action.SUGGEST_COMMAND, baseCommand));
        if (!status.equals(DebugStatus.VERIFIED)) {
            componentBuilder.append(" ").append(DebugStatus.VERIFIED.getComponent(this));
        }
        
        if (!status.equals(DebugStatus.BROKEN)) {
            componentBuilder.append(" ").append(DebugStatus.BROKEN.getComponent(this));
        }
        return new TextComponent(componentBuilder.create());
    }
    
    public DebugStatus getStatus() {
        return status;
    }
    
    public void setStatus(DebugStatus status) {
        this.status = status;
    }
    
    public String getBaseCommand() {
        return baseCommand;
    }
}