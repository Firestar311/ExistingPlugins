package com.stardevmc.titangames.command.cmds;

import com.firestar311.lib.util.Utils;
import com.stardevmc.titangames.TitanGames;
import com.stardevmc.titangames.command.CommandInfo;
import com.stardevmc.titangames.command.GameCommand;
import com.stardevmc.titangames.kits.Kit;
import com.stardevmc.titangames.util.Messages;
import org.bukkit.entity.Player;

@CommandInfo(description = "Removes a kit", aliases = {"removekit", "rk"}, usage = "<kitName>", permission = "titangames.removekit")
public class RemoveKitCommand extends GameCommand {
    
    private TitanGames plugin;
    public RemoveKitCommand(TitanGames plugin) {
        this.plugin = plugin;
    }
    
    public void onCommand(Player player, String[] args) {
        if (!(args.length > 0)) {
            player.sendMessage(Utils.color("&cYou need to provide the kit name to remove"));
            return;
        }
        
        String name = args[0];
        Kit kit = plugin.getKitManager().getKit(name);
        this.plugin.getKitManager().removeKit(name);
        player.sendMessage(Utils.color(Messages.REMOVE_KIT(kit)));
    }
}