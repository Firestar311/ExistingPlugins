package me.alonedev.ironhavensb.commands;

import me.alonedev.ironhavensb.Main;
import me.alonedev.ironhavensb.island.InviteIsland;
import me.alonedev.ironhavensb.guis.CreateGUI;
import me.alonedev.ironhavensb.guis.CreatedGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class IslandCommands implements CommandExecutor {

    private Main plugin;

    public IslandCommands(Main plugin) {
        this.plugin = plugin;

        Bukkit.getPluginCommand("is").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String str, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "[Iron Haven] >> This command is only available to players!");
        } else {
            Player p = (Player) sender;
            try {
                if (args[0].equals("testanvil")) {
                    new InviteIsland(p, plugin);
                }
            } catch (ArrayIndexOutOfBoundsException exc) {
                if (Bukkit.getWorlds().contains(Bukkit.getWorld(p.getUniqueId().toString()))) {
                    new CreatedGUI(p, plugin);
                } else {
                    new CreateGUI(p, plugin);
                }
            }
        }

        return false;
    }

}
