package net.firecraftmc.maniacore.spigot.cmd;

import net.firecraftmc.maniacore.api.ManiaCore;
import net.firecraftmc.maniacore.api.ranks.Rank;
import net.firecraftmc.maniacore.api.util.ManiaUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class BroadcastCmd implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Rank rank = null;
        if (sender instanceof Player) {
            rank = ManiaCore.getInstance().getUserManager().getUser(((Player) sender).getUniqueId()).getRank();
        } else if (sender instanceof ConsoleCommandSender) {
            rank = Rank.CONSOLE;
        }
        if (rank.ordinal() <= Rank.ADMIN.ordinal()) {
            String message = ManiaUtils.color(StringUtils.join(args, " "));
            Bukkit.getConsoleSender().sendMessage(message);
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(message);
            }
        }
        return true;
    }
}
