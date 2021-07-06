package com.stardevmc.titangames.command.cmds;

import com.firestar311.lib.pagination.Paginator;
import com.firestar311.lib.pagination.PaginatorFactory;
import com.firestar311.lib.util.Utils;
import com.stardevmc.titangames.TitanGames;
import com.stardevmc.titangames.arenas.Arena;
import com.stardevmc.titangames.arenas.Spawn;
import com.stardevmc.titangames.command.CommandInfo;
import com.stardevmc.titangames.command.GameCommand;
import org.bukkit.entity.Player;

@CommandInfo(description = "Lists all spawns of an arena", aliases = {"listspawns", "ls"}, usage = "<arena>", permission = "titangames.listspawns")
public class ListSpawnsCommand extends GameCommand {
    
    private TitanGames plugin;
    public ListSpawnsCommand(TitanGames plugin) {
        this.plugin = plugin;
    }
    
    public void onCommand(Player player, String[] args) {
        if (!(args.length > 0)) {
            player.sendMessage(Utils.color("&cYou must provide the arena name."));
            return;
        }
    
        Arena arena = plugin.getArenaManager().getArena(args[0]);
        if (arena == null) {
            player.sendMessage(Utils.color("&cThe name you provided does not match an existing arena."));
            return;
        }
    
        PaginatorFactory<Spawn> factory = new PaginatorFactory<>();
        factory.setHeader("&7List of Spawns for arena &e" + arena.getName() + " &e({pagenumber}/{totalpages})").setFooter("&6Type /titangames listchests " + arena.getName() + " {nextpage} for more");
        factory.setMaxElements(7);
        arena.getSpawnpoints().values().forEach(factory::addElement);
        Paginator<Spawn> paginator = factory.build();
        this.displayPaginator(paginator, player, 1, args);
    }
}