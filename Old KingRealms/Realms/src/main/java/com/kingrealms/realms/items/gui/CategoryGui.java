package com.kingrealms.realms.items.gui;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.items.CustomItem;
import com.kingrealms.realms.items.category.ItemCategory;
import com.starmediadev.lib.builder.ItemBuilder;
import com.starmediadev.lib.gui.GUIButton;
import com.starmediadev.lib.gui.PaginatedGUI;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class CategoryGui extends PaginatedGUI {
    public CategoryGui(ItemCategory<?, ? extends CustomItem> category) {
        super(Realms.getInstance(), category.getName(), true, 54);
        
        category.getItems().forEach(item -> {
            final CustomItem customItem = item;
            ItemStack display = ItemBuilder.start(customItem.getMaterial()).withName("&e" + customItem.getDisplayName()).withItemFlags(ItemFlag.HIDE_ATTRIBUTES).buildItem();
            GUIButton button = new GUIButton(display);
            button.setListener(e -> e.getWhoClicked().getInventory().addItem(customItem.getItemStack()));
            int slot = addButton(button);
        });
        
        setToolbarItem(0, new GUIButton(ItemBuilder.start(Material.REDSTONE_BLOCK).withName("&cBACK").buildItem()).setListener(e -> new ItemGuiMain().openGUI(e.getWhoClicked())));
    }
}