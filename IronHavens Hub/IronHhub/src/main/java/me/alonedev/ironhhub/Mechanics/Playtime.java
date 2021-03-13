package me.alonedev.ironhhub.Mechanics;

import me.alonedev.ironhhub.Utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class Playtime implements CommandExecutor {

    private Object Playtime;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player p = (Player) sender;
            Util.sendMsg(ChatColor.translateAlternateColorCodes('&',"&bYou have played for:"), (Player) sender);
            Util.sendMsg(ChatColor.translateAlternateColorCodes('&',toNormalTime((long) p.getStatistic(Statistic.PLAY_ONE_MINUTE))), (Player) sender);
            Util.sendMsg(ChatColor.translateAlternateColorCodes('&',"&b--------------------------------"), (Player) sender);
        }
        return true;
    }

    private static String toNormalTime(Long minutes){
        return String.format("&f%02d hours %02d minutes %02d seconds", TimeUnit.MILLISECONDS.toHours(minutes),
                TimeUnit.MILLISECONDS.toMinutes(minutes) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(minutes)),
                TimeUnit.MILLISECONDS.toSeconds(minutes) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(minutes)));

    }

}
