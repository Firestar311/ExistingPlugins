package com.stardevmc.titangames.command.cmds;

import com.firestar311.lib.pagination.Paginator;
import com.firestar311.lib.pagination.PaginatorFactory;
import com.stardevmc.titangames.TitanGames;
import com.stardevmc.titangames.command.CommandInfo;
import com.stardevmc.titangames.command.GameCommand;
import com.stardevmc.titangames.kits.Kit;
import org.bukkit.entity.Player;

import java.util.List;

@CommandInfo(description = "Lists all kits", aliases = {"listkits", "lk"}, usage = "", permission = "titangames.listkits")
public class ListKitsCommand extends GameCommand {
    
    private TitanGames plugin;
    public ListKitsCommand(TitanGames plugin) {
        this.plugin = plugin;
    }
    
    public void onCommand(Player player, String[] args) {
        List<Kit> kits = plugin.getKitManager().getKits();
        
        PaginatorFactory<Kit> factory = new PaginatorFactory<>();
        factory.setHeader("&7List of kits &e({pagenumber}/{totalpages})").setFooter("&6Type /titangames listarenas {nextpage} for more.").setMaxElements(5);
        kits.forEach(factory::addElement);
        Paginator<Kit> paginator = factory.build();
        this.displayPaginator(paginator, player, 0, args);
    }
}