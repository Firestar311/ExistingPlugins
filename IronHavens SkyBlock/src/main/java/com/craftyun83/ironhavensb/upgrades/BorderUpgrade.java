package com.craftyun83.ironhavensb.upgrades;

import com.craftyun83.ironhavensb.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

import net.milkbowl.vault.economy.Economy;

public class BorderUpgrade {
    public BorderUpgrade(Player p, Main plugin) {
        Economy econ = Main.econ;
        World world = Bukkit.getWorld(plugin.islandsConfig.getString(p.getName()));
        double borderSize = world.getWorldBorder().getSize();

        if (borderSize == 51) {
            if (econ.getBalance(p) >= 5000) {
                econ.withdrawPlayer(p, 5000);
                p.sendMessage(ChatColor.GREEN + "[Iron Haven] >> Succesfully bought a island size upgrade for $5000!");
                world.getWorldBorder().setSize(101);
            } else {
                p.sendMessage(ChatColor.RED + "[Iron Haven] >> You do not have enough money to buy this upgrade!");
            }
            p.closeInventory();
        } else if (borderSize == 101) {
            if (econ.getBalance(p) >= 7500) {
                econ.withdrawPlayer(p, 7500);
                p.sendMessage(ChatColor.GREEN + "[Iron Haven] >> Succesfully bought a island size upgrade for $7500!");
                world.getWorldBorder().setSize(151);
            } else {
                p.sendMessage(ChatColor.RED + "[Iron Haven] >> You do not have enough money to buy this upgrade!");
            }
            p.closeInventory();
        } else if (borderSize == 151) {
            if (econ.getBalance(p) >= 10000) {
                econ.withdrawPlayer(p, 10000);
                p.sendMessage(ChatColor.GREEN + "[Iron Haven] >> Succesfully bought a island size upgrade for $10000!");
                world.getWorldBorder().setSize(201);
            } else {
                p.sendMessage(ChatColor.RED + "[Iron Haven] >> You do not have enough money to buy this upgrade!");
            }
            p.closeInventory();
        } else {
            p.sendMessage(ChatColor.RED + "[Iron Haven] >> You have already maxed out this upgrade!");
            p.closeInventory();
        }
    }
}