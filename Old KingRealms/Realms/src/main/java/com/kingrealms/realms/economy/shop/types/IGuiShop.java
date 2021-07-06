package com.kingrealms.realms.economy.shop.types;

import com.starmediadev.lib.gui.PaginatedGUI;
import org.bukkit.inventory.ItemStack;

public interface IGuiShop {
    PaginatedGUI getGui();
    ItemStack getIcon();
}