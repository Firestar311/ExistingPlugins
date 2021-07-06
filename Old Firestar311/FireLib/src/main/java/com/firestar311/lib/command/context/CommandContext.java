package com.firestar311.lib.command.context;

import com.firestar311.lib.command.TitanCommand;
import org.bukkit.command.CommandSender;

public class CommandContext {
    private TitanCommand command;
    private String usedAlias;
    private CommandSender sender;
    private String[] rawArguments;
    private int currentIndex;
    
    public CommandContext(TitanCommand command, String usedAlias, CommandSender sender, String[] rawArguments, int currentIndex) {
        this.command = command;
        this.usedAlias = usedAlias;
        this.sender = sender;
        this.rawArguments = rawArguments;
        this.currentIndex = currentIndex;
    }
    
    public TitanCommand getCommand() {
        return command;
    }
    
    public String getUsedAlias() {
        return usedAlias;
    }
    
    public CommandSender getSender() {
        return sender;
    }
    
    public String[] getRawArguments() {
        return rawArguments;
    }
    
    public int getCurrentIndex() {
        return currentIndex;
    }
}