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

@CommandInfo(description = "Adds a lobby sign", aliases = {"addlobbysign", "als"}, usage = "<arena>", permission = "titangames.addlobbysign")
public class AddLobbySignCommand extends GameCommand {
    
    private TitanGames plugin;
    public AddLobbySignCommand(TitanGames plugin) {
        this.plugin = plugin;
    }
    
    public void onCommand(Player player, String[] args) {
        if (!(args.length > 0)) {
            player.sendMessage(Utils.color("&cYou need to provide the arena name."));
            return;
        }
    
        Arena arena = plugin.getArenaManager().getArena(args[0]);
        if (arena == null) {
            player.sendMessage(Utils.color(Messages.INVALID_ARENA_NAME()));
            return;
        }
        
        Sign sign = getSignFromPlayer(player);
        LobbySign lobbySign = new LobbySign(sign, arena);
        lobbySign.updateSign();
        this.plugin.getSignManager().addLobbySign(lobbySign);
        player.sendMessage(Utils.color(Messages.ADD_LOBBY_SIGN(arena)));
    }
}