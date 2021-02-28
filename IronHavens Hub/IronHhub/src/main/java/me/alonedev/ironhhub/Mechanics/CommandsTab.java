package me.alonedev.ironhhub.Mechanics;

import me.alonedev.ironhhub.IronHhub;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

    public class CommandsTab implements TabCompleter {
        @Override
        public List<String> onTabComplete(final CommandSender sender, final Command command, final String alias, final String[] args) {
            final List<String> cmdlist = new ArrayList<String>();
            if (args.length == 1) {
                if (sender.hasPermission(IronHhub.BASE_PERMISSION+".reload")) {
                    cmdlist.add("reload");
                }
            } else return null;
            return cmdlist;
        }
    }
