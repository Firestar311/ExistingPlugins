package com.firestar311.lib.command;

import com.firestar311.lib.command.context.CommandContext;

import java.util.ArrayList;
import java.util.List;

public abstract class Argument {
    
    protected String name, description, permission;
    protected List<String> aliases;
    protected int index;
    
    protected Argument() {}
    
    public void setIndex(int index) {
        this.index = index;
    }
    
    public void performAction(CommandContext context) {
    
    }
    
    public List<String> tabCompletionList() {
        return new ArrayList<>();
    }
}