package net.firecraftmc.maniacore.spigot.cmd;

import net.firecraftmc.maniacore.api.CenturionsCore;
import net.firecraftmc.maniacore.api.ranks.Rank;
import net.firecraftmc.maniacore.api.stats.Statistic;
import net.firecraftmc.maniacore.api.stats.Stats;
import net.firecraftmc.maniacore.api.user.User;
import net.firecraftmc.maniacore.api.util.CenturionsUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class SetstatCommand implements CommandExecutor {
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Rank rank;
        if (sender instanceof ConsoleCommandSender) {
            rank = Rank.CONSOLE;
        } else if (sender instanceof Player) {
            User user = CenturionsCore.getInstance().getUserManager().getUser(((Player) sender).getUniqueId());
            rank = user.getRank();
        } else {
            sender.sendMessage(CenturionsUtils.color("&cYou are not allowed to use that command."));
            return true;
        }
        
        if (rank.ordinal() > Rank.ADMIN.ordinal()) {
            sender.sendMessage(CenturionsUtils.color("&cYou do not have permission to use that command."));
            return true;
        }
        
        if (!(args.length > 2)) {
            sender.sendMessage(CenturionsUtils.color("&cUsage: /setstat <player> <statname> <value>"));
            return true;
        }
        
        User target = CenturionsCore.getInstance().getUserManager().getUser(args[0]);
        if (target == null) {
            sender.sendMessage(CenturionsUtils.color("&cThe name you provided does not match a player that has joined the server."));
            return true;
        }
        
        Stats stat;
        try {
            stat = Stats.valueOf(args[1].toUpperCase());
        } catch (IllegalArgumentException e) {
            sender.sendMessage(CenturionsUtils.color("&cInvalid stat name."));
            return true;
        }
        
        Statistic statistic = target.getStat(stat);
        if (!stat.isNumber()) {
            statistic.setValue(StringUtils.join(args, " ", 2, args.length));
            sender.sendMessage(CenturionsUtils.color("&aSet the stat " + stat.name().toLowerCase() + " to " + statistic.getValue()));
        } else {
            String a = args[2];
            if (a.startsWith("+")) {
                int v = Integer.parseInt(a.substring(1));
                statistic.setValue((statistic.getAsInt() + v) + "");
                sender.sendMessage(CenturionsUtils.color("&aIncreased the stat " + stat.name().toLowerCase() + " by " + v));
            } else if (a.startsWith("-")) {
                int v = Integer.parseInt(a.substring(1));
                statistic.setValue((statistic.getAsInt() - v) + "");
                sender.sendMessage(CenturionsUtils.color("&aDecreased the stat " + stat.name().toLowerCase() + " by " + v));
            } else {
                int v = Integer.parseInt(a);
                statistic.setValue(v + "");
                sender.sendMessage(CenturionsUtils.color("&aSet the stat " + stat.name().toLowerCase() + " to " + v));
            }
        }
        
        return true;
    }
}
