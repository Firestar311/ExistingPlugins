package com.stardevmc.titangames.command.cmds;

import com.firestar311.lib.pagination.Paginator;
import com.firestar311.lib.pagination.PaginatorFactory;
import com.firestar311.lib.util.Utils;
import com.stardevmc.titangames.TitanGames;
import com.stardevmc.titangames.arenas.Arena;
import com.stardevmc.titangames.arenas.chests.ArenaChest;
import com.stardevmc.titangames.command.CommandInfo;
import com.stardevmc.titangames.command.GameCommand;
import org.bukkit.entity.Player;

import java.util.List;

@CommandInfo(description = "Lists all chests in an arena", aliases = {"listchests", "lc"}, usage = "<arena>", permission = "titangames.listarenas")
public class ListChestsCommand extends GameCommand {
    
    private TitanGames plugin;
    public ListChestsCommand(TitanGames plugin) {
        this.plugin = plugin;
    }
    
    public void onCommand(Player player, String[] args) {
        if (!(args.length > 0)) {
            player.sendMessage(Utils.color("&cYou must provide the arena."));
            return;
        }
    
        Arena arena = plugin.getArenaManager().getArena(args[0]);
        if (arena == null) {
            player.sendMessage(Utils.color("&cThe name that you provided does not match an existing arena."));
            return;
        }
    
        List<ArenaChest> chests = arena.getChests();
        PaginatorFactory<ArenaChest> factory = new PaginatorFactory<>();
        factory.setHeader("&7List of chests for arena &e" + arena.getName() + " &e({pagenumber}/{totalpages})").setFooter("&6Type /titangames listchests " + arena.getName() + " {nextpage} for more");
        factory.setMaxElements(7);
        chests.forEach(factory::addElement);
    
        Paginator<ArenaChest> paginator = factory.build();
        this.displayPaginator(paginator, player, 1, args);
    }
}