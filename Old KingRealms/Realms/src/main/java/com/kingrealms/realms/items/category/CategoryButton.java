package com.kingrealms.realms.items.category;

import com.kingrealms.realms.items.gui.CategoryGui;
import com.starmediadev.lib.gui.GUIButton;

public class CategoryButton extends GUIButton {
    public CategoryButton(ItemCategory category) {
        super(category.getIcon());
        setListener(e -> new CategoryGui(category).openGUI(e.getWhoClicked()));
    }
}