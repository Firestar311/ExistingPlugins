package com.kingrealms.realms.economy.shop.types.impl.gui;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.economy.shop.item.ShopItem;
import com.kingrealms.realms.profile.RealmProfile;
import com.starmediadev.lib.builder.ItemBuilder;
import com.starmediadev.lib.gui.GUIButton;
import com.starmediadev.lib.gui.PaginatedGUI;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;

public class CategoryGui extends PaginatedGUI {
    public CategoryGui(ShopCategory category) {
        super(Realms.getInstance(), (StringUtils.isEmpty(category.getName()) ? category.getId() : category.getId()), true, 54);
    
        for (ShopItem item : category.getItems()) {
            GUIButton button = new GUIButton(item.getShopStack());
            button.setListener(e -> {
                RealmProfile profile = Realms.getInstance().getProfileManager().getProfile(e.getWhoClicked());
                if (e.getClick() == ClickType.LEFT) {
                    category.buyItem(item.getPosition(), profile, item.getMinAmount());
                } else if (e.getClick() == ClickType.SHIFT_LEFT) {
                    category.buyItem(item.getPosition(), profile, 64);
                } else if (e.getClick() == ClickType.RIGHT) {
                    category.sellItem(item.getPosition(), profile);
                }
            });
            addButton(button);
        }
    
        GUIButton back = new GUIButton(ItemBuilder.start(Material.SPECTRAL_ARROW).withName("&c&l<- BACK").buildItem());
        back.setListener(e -> new ShopGui(category.getShop()).openGUI(e.getWhoClicked()));
        setToolbarItem(0, back);
    }
}