package com.craftyun83.ironhavensb.island;

import com.craftyun83.ironhavensb.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class JoinIsland {

    public JoinIsland(Player guest, Player invitor, Main plugin) {
        FileConfiguration islandsConfig = plugin.islandsConfig;
        String worldName = islandsConfig.getString(invitor.getName());
        if (!(worldName == null)) {
            if (islandsConfig.getString(guest.getName()) == null) {
                islandsConfig.createSection(guest.getName());
                islandsConfig.set(guest.getName(), worldName);
                List var = islandsConfig.getList(invitor.getUniqueId() + "_players");
                islandsConfig.set(invitor.getUniqueId() + "_players", var.add(guest.getName()));
                plugin.saveIslandYML(plugin.islandsConfig, plugin.islandsYML);
                guest.teleport(new Location(Bukkit.getWorld(islandsConfig.getString(guest.getName())), 0, 81, 0));
                guest.sendMessage(ChatColor.GREEN + "[Iron Haven] >> Joined " + invitor.getName() + "'s island!");
            } else {
                guest.sendMessage(ChatColor.RED + "[Iron Haven] You already own an island! Delete it first before joining someone elses!");
            }
        } else {
            guest.sendMessage(ChatColor.RED + "[Iron Haven] >> This player does not own an island!");
        }
    }
}