package com.kingrealms.realms.economy.shop.types.impl.gui;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.economy.shop.Shop;
import com.starmediadev.lib.gui.GUIButton;
import com.starmediadev.lib.gui.PaginatedGUI;

public class ShopGui extends PaginatedGUI {
    public ShopGui(Shop shop) {
        super(Realms.getInstance(), shop.getName(), true, 54);
        
        if (shop instanceof ServerGUIShop) {
            ServerGUIShop guiShop = (ServerGUIShop) shop;
            for (ShopCategory category : guiShop.getCategories()) {
                GUIButton button = new GUIButton(category.getIcon());
                button.setListener(e -> category.getGui().openGUI(e.getWhoClicked()));
                addButton(button);
            }
        }
    }
}