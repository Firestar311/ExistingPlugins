package com.firestar311.lib.command;

import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.SortedMap;

public class TitanCommand {
    
    protected String name, description, permission;
    protected List<String> aliases;
    protected SortedMap<Integer, Argument> arguments;
    
    public TitanCommand(String name, String description, String permission, List<String> aliases) {
        this.name = name;
        this.description = description;
        this.permission = permission;
        this.aliases = aliases;
    }
    
    public void registerArgument(int index, Argument argument) {
        this.arguments.put(index, argument);
        argument.setIndex(index);
    }
    
    public void executeCommand(CommandSender sender, String[] args) {
    
    }
}