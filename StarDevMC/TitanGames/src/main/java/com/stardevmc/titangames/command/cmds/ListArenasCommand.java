package com.stardevmc.titangames.command.cmds;

import com.firestar311.lib.pagination.Paginator;
import com.firestar311.lib.pagination.PaginatorFactory;
import com.firestar311.lib.util.Utils;
import com.stardevmc.titangames.TitanGames;
import com.stardevmc.titangames.arenas.Arena;
import com.stardevmc.titangames.command.CommandInfo;
import com.stardevmc.titangames.command.GameCommand;
import org.bukkit.entity.Player;

import java.util.List;

@CommandInfo(description = "Lists all arenas", aliases = {"listarenas", "la"}, usage = "", permission = "titangames.listarenas")
public class ListArenasCommand extends GameCommand {
    
    private TitanGames plugin;
    public ListArenasCommand(TitanGames plugin) {
        this.plugin = plugin;
    }
    
    public void onCommand(Player player, String[] args) {
        List<Arena> arenas = plugin.getArenaManager().getArenas();
        
        if (arenas == null || arenas.isEmpty()) {
            player.sendMessage(Utils.color("&cThere are no arenas created."));
            return;
        }
        
        PaginatorFactory<Arena> factory = new PaginatorFactory<>();
        factory.setHeader("&7List of arenas &e({pagenumber}/{totalpages})").setFooter("&6Type /titangames listarenas {nextpage} for more.").setMaxElements(5);
        arenas.forEach(factory::addElement);
        Paginator<Arena> paginator = factory.build();
        this.displayPaginator(paginator, player, 0, args);
    }
}