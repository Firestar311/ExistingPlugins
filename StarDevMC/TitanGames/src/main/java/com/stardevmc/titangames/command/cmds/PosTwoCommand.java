package com.stardevmc.titangames.command.cmds;

import com.firestar311.lib.util.Utils;
import com.stardevmc.titangames.TitanGames;
import com.stardevmc.titangames.command.CommandInfo;
import com.stardevmc.titangames.command.GameCommand;
import org.bukkit.entity.Player;

@CommandInfo(description = "Set position 2", aliases = {"pos2"}, usage = "", permission = "titangames.selection")
public class PosTwoCommand extends GameCommand {
    
    private TitanGames plugin;
    public PosTwoCommand(TitanGames plugin) {
        this.plugin = plugin;
    }
    
    public void onCommand(Player player, String[] args) {
        plugin.getSelectionManager().setPointA(player, player.getLocation());
        player.sendMessage(Utils.color("&aSet pos2 to " + player.getLocation().getBlockX() + ", " + player.getLocation().getBlockY() + ", " + player.getLocation().getBlockZ()));
    }
}
