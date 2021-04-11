package com.craftyun83.ironhavensb.guis;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.craftyun83.ironhavensb.Main;

public class UpgradeGUI {

    public UpgradeGUI(Player p, Main plugin) {
        Inventory createdGUI = Bukkit.getServer().createInventory(p, 9, "Island Upgrades");
        if (plugin.islandsConfig.getString(p.getName()).equals(p.getName())) {
            ItemStack borderUpgrade = new ItemStack(Material.GLASS);
            ItemMeta borderUpgradeMeta = borderUpgrade.getItemMeta();
            ArrayList<String> borderUpgradeMetaLore = new ArrayList<>();
            borderUpgradeMetaLore.add(ChatColor.GREEN + "Increase the world border of your island!");
            if (Bukkit.getWorld(p.getUniqueId().toString()).getWorldBorder().getSize() == 51) {
                borderUpgradeMetaLore.add(ChatColor.GREEN + "Current World Border: 50x50");
                borderUpgradeMetaLore.add(ChatColor.GREEN + "Next Upgrade: 100x100");
            } else if (Bukkit.getWorld(p.getUniqueId().toString()).getWorldBorder().getSize() == 101) {
                borderUpgradeMetaLore.add(ChatColor.GREEN + "Current World Border: 100x100");
                borderUpgradeMetaLore.add(ChatColor.GREEN + "Next Upgrade: 150x150");
            } else if (Bukkit.getWorld(p.getUniqueId().toString()).getWorldBorder().getSize() == 151) {
                borderUpgradeMetaLore.add(ChatColor.GREEN + "Current World Border: 150x150");
                borderUpgradeMetaLore.add(ChatColor.GREEN + "Next Upgrade: 200x200");
            } else if (Bukkit.getWorld(p.getUniqueId().toString()).getWorldBorder().getSize() == 201) {
                borderUpgradeMetaLore.add(ChatColor.GREEN + "Current World Border: 200x200");
                borderUpgradeMetaLore.add(ChatColor.GREEN + "This upgrade is maxed!!!");
            }
            borderUpgradeMeta.setLore(borderUpgradeMetaLore);
            borderUpgradeMeta.setDisplayName("§6§lIncrease world border");
            borderUpgrade.setItemMeta(borderUpgradeMeta);
            createdGUI.setItem(2, borderUpgrade);
            ItemStack coopUpgrade = new ItemStack(Material.PLAYER_HEAD);
            ItemMeta coopUpgradeMeta = coopUpgrade.getItemMeta();
            ArrayList<String> coopUpgradeLore = new ArrayList<>();
            if (plugin.islandsConfig.getInt(p.getUniqueId() + "_coop") == 1) {
                coopUpgradeLore.add(ChatColor.GREEN + "Current Coop Limit: 1");
                coopUpgradeLore.add(ChatColor.GREEN + "Next Upgrade: 2");
            } else if (plugin.islandsConfig.getInt(p.getUniqueId() + "_coop") == 2) {
                coopUpgradeLore.add(ChatColor.GREEN + "Current World Border: 2");
                coopUpgradeLore.add(ChatColor.GREEN + "Next Upgrade: 3");
            } else if (plugin.islandsConfig.getInt(p.getUniqueId() + "_coop") == 3) {
                coopUpgradeLore.add(ChatColor.GREEN + "Current World Border: 3");
                coopUpgradeLore.add(ChatColor.GREEN + "Next Upgrade: 4");
            } else if (plugin.islandsConfig.getInt(p.getUniqueId() + "_coop") == 4) {
                coopUpgradeLore.add(ChatColor.GREEN + "Current World Border: 4");
                coopUpgradeLore.add(ChatColor.GREEN + "This upgrade is maxed!!!");
            }
            coopUpgradeMeta.setLore(coopUpgradeLore);
            coopUpgradeMeta.setDisplayName("§6§lIncrease Coop-member limit");
            coopUpgrade.setItemMeta(coopUpgradeMeta);
            createdGUI.setItem(4, coopUpgrade);
        } else {
            ItemStack borderUpgrade = new ItemStack(Material.GLASS);
            ItemMeta borderUpgradeMeta = borderUpgrade.getItemMeta();
            ArrayList<String> borderUpgradeMetaLore = new ArrayList<>();
            borderUpgradeMetaLore.add(ChatColor.GREEN + "Increase the world border of your island!");
            if (Bukkit.getWorld(plugin.islandsConfig.getString(p.getName())).getWorldBorder().getSize() == 51) {
                borderUpgradeMetaLore.add(ChatColor.GREEN + "Current World Border: 50x50");
                borderUpgradeMetaLore.add(ChatColor.GREEN + "Next Upgrade: 100x100");
            } else if (Bukkit.getWorld(plugin.islandsConfig.getString(p.getName())).getWorldBorder().getSize() == 101) {
                borderUpgradeMetaLore.add(ChatColor.GREEN + "Current World Border: 100x100");
                borderUpgradeMetaLore.add(ChatColor.GREEN + "Next Upgrade: 150x150");
            } else if (Bukkit.getWorld(plugin.islandsConfig.getString(p.getName())).getWorldBorder().getSize() == 151) {
                borderUpgradeMetaLore.add(ChatColor.GREEN + "Current World Border: 150x150");
                borderUpgradeMetaLore.add(ChatColor.GREEN + "Next Upgrade: 200x200");
            } else if (Bukkit.getWorld(plugin.islandsConfig.getString(p.getName())).getWorldBorder().getSize() == 201) {
                borderUpgradeMetaLore.add(ChatColor.GREEN + "Current World Border: 200x200");
                borderUpgradeMetaLore.add(ChatColor.GREEN + "This upgrade is maxed!!!");
            }
            borderUpgradeMeta.setLore(borderUpgradeMetaLore);
            borderUpgradeMeta.setDisplayName("§6§lIncrease world border");
            borderUpgrade.setItemMeta(borderUpgradeMeta);
            createdGUI.setItem(2, borderUpgrade);
            ItemStack coopUpgrade = new ItemStack(Material.PLAYER_HEAD);
            ItemMeta coopUpgradeMeta = coopUpgrade.getItemMeta();
            ArrayList<String> coopUpgradeLore = new ArrayList<>();
            if (plugin.islandsConfig.getInt(plugin.islandsConfig.getString(p.getName()) + "_coop") == 1) {
                coopUpgradeLore.add(ChatColor.GREEN + "Current Coop Limit: 1");
                coopUpgradeLore.add(ChatColor.GREEN + "Next Upgrade: 2");
            } else if (plugin.islandsConfig.getInt(plugin.islandsConfig.getString(p.getName()) + "_coop") == 2) {
                coopUpgradeLore.add(ChatColor.GREEN + "Current World Border: 2");
                coopUpgradeLore.add(ChatColor.GREEN + "Next Upgrade: 3");
            } else if (plugin.islandsConfig.getInt(plugin.islandsConfig.getString(p.getName()) + "_coop") == 3) {
                coopUpgradeLore.add(ChatColor.GREEN + "Current World Border: 3");
                coopUpgradeLore.add(ChatColor.GREEN + "Next Upgrade: 4");
            } else if (plugin.islandsConfig.getInt(plugin.islandsConfig.getString(p.getName()) + "_coop") == 4) {
                coopUpgradeLore.add(ChatColor.GREEN + "Current World Border: 4");
                coopUpgradeLore.add(ChatColor.GREEN + "This upgrade is maxed!!!");
            }
            coopUpgradeMeta.setLore(coopUpgradeLore);
            coopUpgradeMeta.setDisplayName("§6§lIncrease Coop-member limit");
            coopUpgrade.setItemMeta(coopUpgradeMeta);
            createdGUI.setItem(4, coopUpgrade);
        }
        p.openInventory(createdGUI);
    }
}