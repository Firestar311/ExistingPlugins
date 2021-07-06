package com.stardevmc.titangames.command.cmds;

import com.firestar311.lib.pagination.Paginator;
import com.firestar311.lib.pagination.PaginatorFactory;
import com.firestar311.lib.util.Utils;
import com.stardevmc.titangames.TitanGames;
import com.stardevmc.titangames.arenas.Arena;
import com.stardevmc.titangames.command.CommandInfo;
import com.stardevmc.titangames.command.GameCommand;
import com.stardevmc.titangames.signs.LobbySign;
import org.bukkit.entity.Player;

import java.util.List;

@CommandInfo(description = "Lists all signs of an arena", aliases = {"listspawns", "ls"}, usage = "<arena>", permission = "titangames.listspawns")
public class ListSignsCommand extends GameCommand {
    
    private TitanGames plugin;
    public ListSignsCommand(TitanGames plugin) {
        this.plugin = plugin;
    }
    
    public void onCommand(Player player, String[] args) {
        if (!(args.length > 0)) {
            player.sendMessage(Utils.color("&cYou must provide the arena name."));
            return;
        }
    
        Arena arena = this.getArenaFromArgs(plugin, player, args);
    
        List<LobbySign> signs = plugin.getSignManager().getLobbySigns(arena);
        if (signs == null || signs.isEmpty()) {
            player.sendMessage(Utils.color("&cThere are no signs linked to that arena."));
            return;
        }
    
        PaginatorFactory<LobbySign> factory = new PaginatorFactory<>();
        factory.setHeader("&7List of Signs for arena &e" + arena.getName() + " &e({pagenumber}/{totalpages})").setFooter("&6Type /titangames listtsigns " + arena.getName() + " {nextpage} for more");
        factory.setMaxElements(7);
        signs.forEach(factory::addElement);
        Paginator<LobbySign> paginator = factory.build();
        this.displayPaginator(paginator, player, 1, args);
    }
}