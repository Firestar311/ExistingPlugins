package net.firecraftmc.maniacore.spigot.cmd;

import net.firecraftmc.maniacore.api.CenturionsCore;
import net.firecraftmc.maniacore.api.ranks.Rank;
import net.firecraftmc.maniacore.api.stats.Statistic;
import net.firecraftmc.maniacore.api.stats.Stats;
import net.firecraftmc.maniacore.api.user.User;
import net.firecraftmc.maniacore.api.util.CenturionsUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class TesterCmd implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Rank senderRank;
        if (sender instanceof ConsoleCommandSender) {
            senderRank = Rank.CONSOLE;
        } else if (sender instanceof Player) {
            senderRank = CenturionsCore.getInstance().getUserManager().getUser(((Player) sender).getUniqueId()).getRank();
        } else {
            senderRank = Rank.DEFAULT;
        }

        if (senderRank.ordinal() > Rank.ADMIN.ordinal()) {
            sender.sendMessage(CenturionsUtils.color("&cYou do not have enough permission to use that command."));
            return true;
        }

        if (!(args.length > 1)) {
            sender.sendMessage(CenturionsUtils.color("&cUsage: /tester <name> <true|false>"));
            return true;
        }

        User target = CenturionsCore.getInstance().getUserManager().getUser(args[0]);
        if (target == null) {
            sender.sendMessage(CenturionsUtils.color("&cInvalid target name."));
            return true;
        }

        Statistic statistic = target.getStat(Stats.TESTER);
        boolean current = statistic.getAsBoolean();
        boolean value;
        try {
            value = Boolean.parseBoolean(args[1]);
        } catch (Exception e) {
            sender.sendMessage(CenturionsUtils.color("&cYou provided an invalid value. Possible values: true or false"));
            return true;
        }

        if (value == current) {
            sender.sendMessage(CenturionsUtils.color("&cThe new value is the same as the old value."));
            return true;
        }

        statistic.setValue(value);
        sender.sendMessage(CenturionsUtils.color("&aYou set " + target.getName() + "'s tester status to " + value));
        return true;
    }
}
