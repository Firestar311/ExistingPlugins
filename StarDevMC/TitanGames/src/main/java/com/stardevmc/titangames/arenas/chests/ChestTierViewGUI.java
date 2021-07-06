package com.stardevmc.titangames.arenas.chests;

import com.firestar311.lib.builder.ItemBuilder;
import com.firestar311.lib.gui.GUIButton;
import com.firestar311.lib.gui.PaginatedGUI;
import com.stardevmc.titangames.TitanGames;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Map.Entry;

public class ChestTierViewGUI extends PaginatedGUI {
    
    static {
        PaginatedGUI.prepare(TitanGames.getInstance());
    }
    
    public ChestTierViewGUI(ChestTier tier) {
        super(TitanGames.getInstance(), "Contents of Chest Tier " + tier.getNumber(), false, 27);
        
        final double totalChance = tier.getTotalChance();
        for (Entry<ItemStack, Integer> entry : tier.getItems().entrySet()) {
            ItemStack itemStack = entry.getKey();
            double chance = (entry.getValue() / totalChance) * 100;
            if (itemStack != null && !itemStack.getType().equals(Material.AIR)) {
                addButton(new GUIButton(new ItemBuilder(itemStack).withLore("", "&eChance: " + chance + "%").buildItem()));
            }
        }
    }
}
