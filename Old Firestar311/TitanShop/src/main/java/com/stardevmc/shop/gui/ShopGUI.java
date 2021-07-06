package com.stardevmc.shop.gui;

import com.firestar311.lib.builder.ItemBuilder;
import com.firestar311.lib.gui.GUIButton;
import com.firestar311.lib.gui.PaginatedGUI;
import com.stardevmc.shop.TitanShop;
import com.stardevmc.shop.objects.shops.gui.GUIShop;
import com.stardevmc.shop.objects.shops.gui.GUIShopCategory;
import org.bukkit.entity.Player;

import java.util.Map.Entry;

public class ShopGUI extends PaginatedGUI {
    
    public ShopGUI(GUIShop guiShop) {
        super(TitanShop.getInstance(), "Shop: " + guiShop.getName(), true, 54);
        
        for (Entry<Integer, GUIShopCategory> entry : guiShop.getCategories().entrySet()) {
            int place = entry.getKey();
            final GUIShopCategory category = entry.getValue();
    
            GUIButton catButton = new GUIButton(new ItemBuilder(category.getIcon()).withName("&f" + category.getName()).buildItem());
            catButton.setListener(e -> {
                Player player = ((Player) e.getWhoClicked());
                category.getGUI().openGUI(player);
            });
            setButton(place, catButton);
        }
    }
}