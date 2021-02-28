package me.alonedev.ironhhub.Commands;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.alonedev.ironhhub.IronHhub;
import me.alonedev.ironhhub.Utils.Util;

public class Commands implements CommandExecutor {
    private final IronHhub instance;

    public Commands(final IronHhub instance) {
        this.instance = instance;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (command.getName().equals("ih")) {
            if (sender instanceof Player) {
                final Player p = (Player)sender;
                if(p.hasPermission(IronHhub.BASE_PERMISSION+".use")) {
                    if(args.length == 0 || args.length > 1) {
                        this.instance.help(p);
                    } else if(args.length == 1) {
                        if(args[0].equals("reload")) {
                            if (p.hasPermission(IronHhub.BASE_PERMISSION+".reload")) {
                                this.instance.loadSettings();
                                p.sendMessage(ChatColor.YELLOW+IronHhub.PLUGIN_NAME+ChatColor.GREEN+" has been reloaded!");
                            } else p.sendMessage(ChatColor.RED+"No permission!");
                        } else this.instance.help(p);
                    }
                } else p.sendMessage(ChatColor.RED+"No permission!");
            } else {
                if(args.length == 0 || args.length > 1) {
                    this.instance.help(null);
                } else if(args.length == 1) {
                    if(args[0].equals("reload")) {
                        this.instance.loadSettings();
                        Util.consoleMsg(ChatColor.YELLOW+IronHhub.PLUGIN_NAME+ChatColor.GREEN+" has been reloaded!");
                    } else this.instance.help(null);
                }
            }
            return true;
        }
        return false;
    }

}
