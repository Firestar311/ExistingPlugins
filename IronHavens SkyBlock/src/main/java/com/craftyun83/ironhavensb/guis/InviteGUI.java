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

public class InviteGUI {
	
	public Player invitor;
	
	public InviteGUI(Player target, Player invitor, Main plugin) {
		this.invitor = invitor;
		Inventory coopGUI = Bukkit.getServer().createInventory(invitor, 9, "Accept Island Invite");
		ItemStack info = new ItemStack(Material.WITHER_SKELETON_SKULL);
		ItemMeta infoMeta = info.getItemMeta();
		ArrayList<String> infoLore = new ArrayList<>();
		infoLore.add(ChatColor.GREEN+invitor.getName()+" has invited you to join their coop!");
		infoMeta.setLore(infoLore);
		infoMeta.setDisplayName("§6§lInfo");
		info.setItemMeta(infoMeta);
		coopGUI.setItem(4, info);
		ItemStack acceptItem = new ItemStack(Material.GREEN_CONCRETE);
		ItemMeta acceptItemMeta = acceptItem.getItemMeta();
		ArrayList<String> acceptItemLore = new ArrayList<>();
		acceptItemLore.add(ChatColor.GREEN+"Accept coop invitation!");
		acceptItemMeta.setLore(acceptItemLore);
		acceptItemMeta.setDisplayName("§a§lAccept");
		acceptItem.setItemMeta(acceptItemMeta);
		coopGUI.setItem(2, acceptItem);
		ItemStack declineItem = new ItemStack(Material.RED_CONCRETE);
		ItemMeta declineItemMeta = declineItem.getItemMeta();
		ArrayList<String> declineItemLore = new ArrayList<>();
		declineItemLore.add(ChatColor.RED+"Decline coop invitation!");
		declineItemMeta.setLore(declineItemLore);
		declineItemMeta.setDisplayName("§c§lDecline");
		declineItem.setItemMeta(declineItemMeta);
		coopGUI.setItem(6, declineItem);
		target.openInventory(coopGUI);
	}
}