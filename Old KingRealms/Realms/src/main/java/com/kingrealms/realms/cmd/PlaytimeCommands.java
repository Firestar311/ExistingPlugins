package com.kingrealms.realms.cmd;

import com.kingrealms.realms.util.Constants;
import com.starmediadev.lib.pagination.*;
import com.starmediadev.lib.user.User;
import com.starmediadev.lib.util.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class PlaytimeCommands extends BaseCommand {
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!checkSeason(sender)) return true;
        if (cmd.getName().equalsIgnoreCase("playtime")) {
            User target;
            if (args.length > 0) {
                target = plugin.getUserManager().getUser(args[0]);
                if (target == null) {
                    sender.sendMessage(Utils.color("&cYou provided an invalid name"));
                    return true;
                }
            } else {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(Utils.color("&cYou must provide a player name."));
                    return true;
                }
            
                Player player = (Player) sender;
                target = plugin.getUserManager().getUser(player.getUniqueId());
            }
            
            String b = Constants.PLAYER_BASE_COLOR;
            String v = Constants.PLAYER_VARIABLE_COLOR;
            if (args.length > 0) {
                sender.sendMessage(Utils.color(b + "Playtime for " + v + target.getLastName() + b + ": &f" + Utils.formatTime(target.getPlayTime())));
            } else {
                sender.sendMessage(Utils.color(b + "Playtime: &f" + Utils.formatTime(target.getPlayTime())));
            }
        } else if (cmd.getName().equalsIgnoreCase("playtimetop")) {
            Map<User, Long> playtimes = new HashMap<>();
            for (User user : plugin.getUserManager().getUsers().values()) {
                long playTime = user.getPlayTime();
                if (playTime > 0) {
                    playtimes.put(user, playTime);
                }
            }
        
            List<Map.Entry<User, Long>> sortedEntries = new ArrayList<>(playtimes.entrySet());
            sortedEntries.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));
        
            String[] lines = new String[sortedEntries.size()];
            for (int i = 0; i < sortedEntries.size(); i++) {
                lines[i] = Constants.PLAYER_BASE_COLOR + (i+1) + ". &f" + sortedEntries.get(i).getKey().getLastName() + " " + Utils.formatTime(sortedEntries.get(i).getValue());
            }
        
            Paginator<StringElement> linePaginator = PaginatorFactory.generateStringPaginator(7, Arrays.asList(lines), new HashMap<>(){{
                put(DefaultVariables.COMMAND, "playtimetop");
                put(DefaultVariables.TYPE, "Top Players by Time");
            }});
        
            if (args.length > 0) {
                linePaginator.display(sender, args[0]);
            } else {
                linePaginator.display(sender, 1);
            }
        }
        
        return true;
    }
}