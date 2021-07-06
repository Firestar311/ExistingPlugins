package com.firestar311.lib.region;

import com.firestar311.lib.util.Utils;
import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class PositionCommand implements CommandExecutor {
    
    private SelectionManager selectionManager;
    
    public PositionCommand(SelectionManager selectionManager) {
        this.selectionManager = selectionManager;
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.color("&cOnly players may use that command."));
            return true;
        }
        
        Player player = ((Player) sender);
    
        if (!(args.length > 0)) {
            player.sendMessage("&cYou must provide a position number to set.");
            return true;
        }
        
        if (Utils.checkCmdAliases(args, 0, "pos1", "posa")) {
            Location location = player.getLocation();
            selectionManager.setPointA(player, location);
            player.sendMessage(Utils.color("&aSet pos1 to " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ()));
        } else if (Utils.checkCmdAliases(args, 0, "pos2", "posb")) {
            Location location = player.getLocation();
            selectionManager.setPointB(player, location);
            player.sendMessage(Utils.color("&aSet pos2 to " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ()));
        }
        
        return true;
    }
}
