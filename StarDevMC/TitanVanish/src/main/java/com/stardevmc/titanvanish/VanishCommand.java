package com.stardevmc.titanvanish;

import com.firestar311.lib.util.Utils;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class VanishCommand implements CommandExecutor {
    
    private TitanVanish plugin;
    public VanishCommand(TitanVanish plugin) {
        this.plugin = plugin;
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.color("&cOnly players may use that command."));
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("titanvanish.command.vanish")) {
            player.sendMessage(Utils.color("&cYou lack permission to use that command."));
            return true;
        }
        
        boolean value = plugin.getVanishManager().isVanished(player);
        if (value) {
            plugin.getVanishManager().removeVanish(player);
        } else {
            plugin.getVanishManager().setVanish(player);
        }
        
        return true;
    }
}