package com.stardevmc.titangames.command;

import com.firestar311.lib.util.Utils;
import com.stardevmc.titangames.TitanGames;
import com.stardevmc.titangames.command.cmds.*;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.*;

public class CommandManager implements CommandExecutor {
    
    private List<GameCommand> cmds = new LinkedList<>();
    
    public CommandManager(TitanGames plugin) {
        registerCmds(new CreateArenaCommand(plugin), new RemoveArenaCommand(plugin), new AddChestCommand(plugin), new RemoveChestCommand(plugin),
                new JoinCommand(plugin), new LeaveCommand(plugin), new PosOneCommand(plugin), new PosTwoCommand(plugin), new AddSpawnCommand(plugin),
                new RemoveSpawnCommand(plugin), new ForceStartCommand(plugin), new ForceStopCommand(plugin), new AddLobbySignCommand(plugin),
                new RemoveLobbySignCommand(plugin), new SetSignArenaCommand(plugin), new CreateKitCommand(plugin), new RemoveKitCommand(plugin),
                new AddChestTierCommand(plugin), new AddItemCommand(plugin), new SetupCommand(plugin), new ListArenasCommand(plugin),
                new ListChestsCommand(plugin), new ListChestTiersCommand(plugin), new ListKitsCommand(plugin), new ListSpawnsCommand(plugin),
                new ListSignsCommand(plugin), new ArenaViewCommand(plugin), new KitViewCommand(plugin), new ChestTierViewCommand(plugin));
    }
    
    private void registerCmds(GameCommand... gcmds) {
        cmds.addAll(Arrays.asList(gcmds));
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Utils.color("&cOnly players may use that command."));
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            for (GameCommand gcmd : this.cmds) {
                CommandInfo info = gcmd.getClass().getAnnotation(CommandInfo.class);
                
                if (!StringUtils.isEmpty(info.permission())) {
                    if (!player.hasPermission(info.permission())) {
                        continue;
                    }
                }
    
                player.sendMessage(Utils.color("&6/" + label + " (" + StringUtils.join(info.aliases(), " ").trim() + ") " + info.usage() + " - " + info.description()));
            }
            return true;
        }
        
        GameCommand wanted = null;
        for (GameCommand gcmd : this.cmds) {
            CommandInfo info = gcmd.getClass().getAnnotation(CommandInfo.class);
            for (String alias : info.aliases()) {
                if (alias.equalsIgnoreCase(args[0])) {
                    wanted = gcmd;
                    break;
                }
            }
        }
        
        if (wanted == null) {
            player.sendMessage(Utils.color("&cInvalid subcommand: " + args[0]));
            return true;
        }
        
        List<String> oldArgs = new ArrayList<>(Arrays.asList(args));
        oldArgs.remove(args[0]);
        String[] newArgs = oldArgs.toArray(new String[0]);
        wanted.onCommand(player, newArgs);
        
        return true;
    }
}