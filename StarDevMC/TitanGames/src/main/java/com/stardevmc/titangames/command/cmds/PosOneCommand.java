package com.stardevmc.titangames.command.cmds;

import com.firestar311.lib.util.Utils;
import com.stardevmc.titangames.TitanGames;
import com.stardevmc.titangames.command.CommandInfo;
import com.stardevmc.titangames.command.GameCommand;
import org.bukkit.entity.Player;

@CommandInfo(description = "Set position 1", aliases = {"pos1"}, usage = "", permission = "titangames.selection")
public class PosOneCommand extends GameCommand {
    
    private TitanGames plugin;
    public PosOneCommand(TitanGames plugin) {
        this.plugin = plugin;
    }
    
    public void onCommand(Player player, String[] args) {
        plugin.getSelectionManager().setPointA(player, player.getLocation());
        player.sendMessage(Utils.color("&aSet pos1 to " + player.getLocation().getBlockX() + ", " + player.getLocation().getBlockY() + ", " + player.getLocation().getBlockZ()));
    }
}
