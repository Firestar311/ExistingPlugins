package com.stardevmc.titangames.kits;

import com.firestar311.lib.gui.GUIButton;
import com.firestar311.lib.gui.PaginatedGUI;
import com.stardevmc.titangames.TitanGames;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class KitViewGUI extends PaginatedGUI {
    
    static {
        PaginatedGUI.prepare(TitanGames.getInstance());
    }
    
    public KitViewGUI(Kit kit) {
        super(TitanGames.getInstance(), "Contents of kit " + kit.getName(), false, 45);
        
        for (ItemStack itemStack : kit.getItems()) {
            if (itemStack != null && !itemStack.getType().equals(Material.AIR)) {
                addButton(new GUIButton(itemStack));
            }
        }
    }
}
