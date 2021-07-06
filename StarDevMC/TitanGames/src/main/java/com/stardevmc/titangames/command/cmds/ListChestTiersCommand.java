package com.stardevmc.titangames.command.cmds;

import com.firestar311.lib.pagination.Paginator;
import com.firestar311.lib.pagination.PaginatorFactory;
import com.stardevmc.titangames.TitanGames;
import com.stardevmc.titangames.arenas.chests.ChestTier;
import com.stardevmc.titangames.command.CommandInfo;
import com.stardevmc.titangames.command.GameCommand;
import org.bukkit.entity.Player;

import java.util.List;

@CommandInfo(description = "Lists all chest tiers", aliases = {"listchesttiers", "lct"}, usage = "", permission = "titangames.chesttiers")
public class ListChestTiersCommand extends GameCommand {
    
    private TitanGames plugin;
    public ListChestTiersCommand(TitanGames plugin) {
        this.plugin = plugin;
    }
    
    public void onCommand(Player player, String[] args) {
        List<ChestTier> chestTiers = plugin.getChestTierManger().getChestTiers();
        
        PaginatorFactory<ChestTier> factory = new PaginatorFactory<>();
        factory.setHeader("&7List of chest tiers &e({pagenumber}/{totalpages})").setFooter("&6Type /titangames listarenas {nextpage} for more.").setMaxElements(5);
        chestTiers.forEach(factory::addElement);
        Paginator<ChestTier> paginator = factory.build();
        this.displayPaginator(paginator, player, 0, args);
    }
}