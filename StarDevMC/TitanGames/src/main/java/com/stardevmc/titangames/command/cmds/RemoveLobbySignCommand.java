package com.stardevmc.titangames.command.cmds;

import com.firestar311.lib.util.Utils;
import com.stardevmc.titangames.TitanGames;
import com.stardevmc.titangames.command.CommandInfo;
import com.stardevmc.titangames.command.GameCommand;
import com.stardevmc.titangames.util.Messages;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

@CommandInfo(description = "Removes a lobby sign", aliases = {"removelobbysign", "rls"}, usage = "", permission = "titangames.removelobbysign")
public class RemoveLobbySignCommand extends GameCommand {
    
    private TitanGames plugin;
    public RemoveLobbySignCommand(TitanGames plugin) {
        this.plugin = plugin;
    }
    
    public void onCommand(Player player, String[] args) {
        Sign sign = getSignFromPlayer(player);
        plugin.getSignManager().removeSign(sign);
        player.sendMessage(Utils.color(Messages.REMOVE_LOBBY_SIGN(sign.getLocation())));
    }
}