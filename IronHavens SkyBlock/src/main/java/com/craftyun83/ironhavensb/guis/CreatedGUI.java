package com.craftyun83.ironhavensb.guis;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CreatedGUI {

    public CreatedGUI(Player p) {
        Inventory createdGUI = Bukkit.getServer().createInventory(p, 9, "Island viewer");

        ItemStack teleportIsland = new ItemStack(Material.END_PORTAL_FRAME);
        ItemMeta teleportIslandMeta = teleportIsland.getItemMeta();
        ArrayList<String> teleportIslandLore = new ArrayList<>();
        teleportIslandLore.add(ChatColor.GREEN + "Teleport to your island!");
        teleportIslandMeta.setLore(teleportIslandLore);
        teleportIslandMeta.setDisplayName("§6§lTeleport to your island");
        teleportIsland.setItemMeta(teleportIslandMeta);
        createdGUI.setItem(4, teleportIsland);
        ItemStack upgradeIsland = new ItemStack(Material.DIAMOND);
        ItemMeta upgradeIslandMeta = upgradeIsland.getItemMeta();
        ArrayList<String> upgradeIslandLore = new ArrayList<>();
        upgradeIslandLore.add(ChatColor.GREEN + "Upgrade things on your island!");
        upgradeIslandMeta.setLore(upgradeIslandLore);
        upgradeIslandMeta.setDisplayName("§6§lUpgrade your island");
        upgradeIsland.setItemMeta(upgradeIslandMeta);
        createdGUI.setItem(2, upgradeIsland);
        ItemStack reloadIsland = new ItemStack(Material.REDSTONE);
        ItemMeta reloadIslandMeta = reloadIsland.getItemMeta();
        ArrayList<String> reloadIslandLore = new ArrayList<>();
        reloadIslandLore.add(ChatColor.RED + "Reload island and start over!");
        reloadIslandMeta.setLore(reloadIslandLore);
        reloadIslandMeta.setDisplayName("§c§lReload Island");
        reloadIsland.setItemMeta(reloadIslandMeta);
        createdGUI.setItem(0, reloadIsland);
        ItemStack deleteIsland = new ItemStack(Material.BARRIER);
        ItemMeta deleteIslandMeta = deleteIsland.getItemMeta();
        ArrayList<String> deleteIslandLore = new ArrayList<>();
        deleteIslandLore.add(ChatColor.RED + "Delete island and start over!");
        deleteIslandMeta.setLore(deleteIslandLore);
        deleteIslandMeta.setDisplayName("§c§lDelete Island");
        deleteIsland.setItemMeta(deleteIslandMeta);
        createdGUI.setItem(8, deleteIsland);
        ItemStack inviteIsland = new ItemStack(Material.PAPER);
        ItemMeta inviteIslandMeta = inviteIsland.getItemMeta();
        ArrayList<String> inviteIslandLore = new ArrayList<>();
        inviteIslandLore.add(ChatColor.RED + "Invite friends to your skyblock island!");
        inviteIslandMeta.setLore(inviteIslandLore);
        inviteIslandMeta.setDisplayName("§a§lInvite Friends");
        inviteIsland.setItemMeta(inviteIslandMeta);
        createdGUI.setItem(6, inviteIsland);
        p.openInventory(createdGUI);
    }
}