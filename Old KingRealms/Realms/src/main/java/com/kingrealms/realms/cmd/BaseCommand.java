package com.kingrealms.realms.cmd;

import com.kingrealms.realms.Realms;
import com.starmediadev.lib.util.Utils;
import org.bukkit.command.*;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseCommand implements TabExecutor {
    
    protected Realms plugin = Realms.getInstance();
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        return new ArrayList<>();
    }
    
    protected List<String> getResults(String arg, List<String> possibleResults) {
        return Utils.getResults(arg, possibleResults);
    }
    
    protected boolean checkSeason(CommandSender sender) {
        if (!plugin.getSeason().isActive()) {
            sender.sendMessage(Utils.color("&cThe season is not yet active. That command is not accessible"));
        }
        return plugin.getSeason().isActive();
    }
}