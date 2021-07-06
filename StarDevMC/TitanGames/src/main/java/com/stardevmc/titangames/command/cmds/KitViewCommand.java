package com.stardevmc.titangames.command.cmds;

import com.firestar311.lib.util.Utils;
import com.stardevmc.titangames.TitanGames;
import com.stardevmc.titangames.command.CommandInfo;
import com.stardevmc.titangames.command.GameCommand;
import com.stardevmc.titangames.kits.Kit;
import com.stardevmc.titangames.kits.KitViewGUI;
import org.bukkit.entity.Player;

@CommandInfo(description = "View the contents of a kit", aliases = {"viewkit", "vk"}, usage = "<kitName>")
public class KitViewCommand extends GameCommand {
    
    private TitanGames plugin;
    public KitViewCommand(TitanGames plugin) {
        this.plugin = plugin;
    }
    
    public void onCommand(Player player, String[] args) {
        if (args.length == 0) {
            player.sendMessage(Utils.color("&cYou did not provide a kit name."));
            return;
        }
    
        Kit kit = plugin.getKitManager().getKit(args[0]);
        if (kit == null) {
            player.sendMessage(Utils.color("&cThe name you provided does not match a kit"));
            return;
        }
    
        new KitViewGUI(kit).openGUI(player);
    }
}
