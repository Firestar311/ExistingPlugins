package me.alonedev.ihhub.commands;

import me.alonedev.ihhub.IHhub;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.alonedev.ihhub.Util;

import net.md_5.bungee.api.ChatColor;

//Plugin commands handler
public class AdminCommands implements CommandExecutor {
    private final IHhub instance;

    public AdminCommands(final IHhub instance) {
        this.instance = instance;
    }

    @Override
    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (command.getName().equals("IronHavens")) {
            if (sender instanceof Player) {
                final Player p = (Player)sender;
                if(p.hasPermission("IronHavensH.use")) {
                    if(args.length == 0 || args.length > 1) {
                        this.instance.help(p);
                    } else if(args.length == 1) {
                        if(args[0].equals("reload")) {
                            if (p.hasPermission("IronHavensH.reload")) {
                                this.instance.loadSettings();
                                p.sendMessage(ChatColor.YELLOW+IHhub.PLUGIN_NAME+ChatColor.GREEN+" has been reloaded!");
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
                        Util.consoleMsg(ChatColor.YELLOW+IHhub.PLUGIN_NAME+ChatColor.GREEN+" has been reloaded!");
                    } else this.instance.help(null);
                }
            }
            return true;
        }
        return false;
    }
}

