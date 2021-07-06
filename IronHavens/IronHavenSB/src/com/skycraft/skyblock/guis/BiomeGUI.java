package com.skycraft.skyblock.guis;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BiomeGUI {
	
	public BiomeGUI(Player p) {
		
		Inventory biomeGUI = Bukkit.getServer().createInventory(p, 18, "Biome Chooser");
		
		// Jungle Biome Item
		
		ItemStack JungleWood = new ItemStack(Material.SPRUCE_WOOD);
		ItemMeta JungleWoodMeta = JungleWood.getItemMeta();
		ArrayList<String> JungleWoodLore = new ArrayList<String>();
		
		JungleWoodLore.add(ChatColor.GREEN+"Generate a jungle biome skyblock island!");
		
		JungleWoodMeta.setLore(JungleWoodLore);
		JungleWoodMeta.setDisplayName("¡±6¡±lJungle Wood Biome");
		
		
		JungleWood.setItemMeta(JungleWoodMeta);
		biomeGUI.setItem(1, JungleWood);
		
		// Spruce Biome Item
		
		ItemStack SpruceWood = new ItemStack(Material.SPRUCE_WOOD);
		ItemMeta SpruceWoodMeta = SpruceWood.getItemMeta();
		ArrayList<String> SpruceWoodLore = new ArrayList<String>();
			
		SpruceWoodLore.add(ChatColor.GREEN+"Generate a spruce biome skyblock island!");
		
		SpruceWoodMeta.setLore(SpruceWoodLore);
		SpruceWoodMeta.setDisplayName("¡±6¡±lSpruce Wood Biome");
		
		
		SpruceWood.setItemMeta(SpruceWoodMeta);
		biomeGUI.setItem(3, SpruceWood);
		
		// Oak Wood Item
		
		ItemStack OakWood = new ItemStack(Material.OAK_WOOD);
		ItemMeta OakWoodMeta = OakWood.getItemMeta();
		ArrayList<String> upgradeIslandLore = new ArrayList<String>();
		
		upgradeIslandLore.add(ChatColor.GREEN+"Generate a oak biome skyblock island!");
		
		OakWoodMeta.setLore(upgradeIslandLore);
		OakWoodMeta.setDisplayName("¡±6¡±lOak Wood Biome");
		
		OakWood.setItemMeta(OakWoodMeta);
		biomeGUI.setItem(5, OakWood);
		
		// Acacia Wood Item
		
		ItemStack AcaciaWood = new ItemStack(Material.ACACIA_WOOD);
		ItemMeta AcaciaWoodMeta = AcaciaWood.getItemMeta();
		ArrayList<String> AcaciaWoodLore = new ArrayList<String>();
		
		AcaciaWoodLore.add(ChatColor.GREEN+"Generate an acacia biome skyblock island!");
		
		AcaciaWoodMeta.setLore(AcaciaWoodLore);
		AcaciaWoodMeta.setDisplayName("¡±6¡±lAcacia Wood Biome");
		
		AcaciaWood.setItemMeta(AcaciaWoodMeta);
		biomeGUI.setItem(7, AcaciaWood);
		
		// Classic Item
		
		ItemStack ClassicIsland = new ItemStack(Material.GRASS);
		ItemMeta ClassicIslandMeta = ClassicIsland.getItemMeta();
		ArrayList<String> ClassicIslandLore = new ArrayList<String>();
		
		ClassicIslandLore.add(ChatColor.GREEN+"Generate a classic Skyblock island");
		
		ClassicIslandMeta.setLore(ClassicIslandLore);
		ClassicIslandMeta.setDisplayName("¡±6¡±lClassic Island");
		
		ClassicIsland.setItemMeta(ClassicIslandMeta);
		biomeGUI.setItem(11, ClassicIsland);
		
		// Hardcore Item
		
		ItemStack Hardcore = new ItemStack(Material.BARRIER);
		ItemMeta HardcoreMeta = Hardcore.getItemMeta();
		ArrayList<String> HardcoreLore = new ArrayList<String>();
		
		HardcoreLore.add(ChatColor.GREEN+"Generate a hardcore skyblock island!");
		
		HardcoreMeta.setLore(HardcoreLore);
		HardcoreMeta.setDisplayName("¡±6¡±lHardcore Island");
		
		Hardcore.setItemMeta(HardcoreMeta);
		biomeGUI.setItem(15, Hardcore);
		
		//Here opens the inventory
		p.openInventory(biomeGUI);
		
	}

}
