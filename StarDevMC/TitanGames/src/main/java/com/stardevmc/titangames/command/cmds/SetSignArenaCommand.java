package com.stardevmc.titangames.command.cmds;

import com.firestar311.lib.util.Utils;
import com.stardevmc.titangames.TitanGames;
import com.stardevmc.titangames.arenas.Arena;
import com.stardevmc.titangames.command.CommandInfo;
import com.stardevmc.titangames.command.GameCommand;
import com.stardevmc.titangames.signs.LobbySign;
import com.stardevmc.titangames.util.Messages;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

@CommandInfo(description = "Changes a lobby sign's arena", aliases = {"changesignarena", "csa"}, usage = "<arena>", permission = "titangames.changesignarena")
public class SetSignArenaCommand extends GameCommand {
    
    private TitanGames plugin;
    public SetSignArenaCommand(TitanGames plugin) {
        this.plugin = plugin;
    }
    
    public void onCommand(Player player, String[] args) {
        if (!(args.length > 0)) {
            player.sendMessage(Utils.color("&cYou need to provide the arena name."));
            return;
        }
        
        Arena arena = plugin.getArenaManager().getArena(args[0]);
        if (arena == null) {
            player.sendMessage(Utils.color("&cThe arena name does not match any valid arenas"));
            return;
        }
        
        Sign sign = getSignFromPlayer(player);
        LobbySign lobbySign = plugin.getSignManager().getLobbySign(sign);
        if (lobbySign == null) {
            player.sendMessage(Utils.color("&cThe sign you are looking at is not a valid lobby sign"));
            return;
        }
        lobbySign.setArena(arena);
        lobbySign.updateSign();
        player.sendMessage(Utils.color(Messages.CHANGE_SIGN_ARENA(arena)));
    }
}
