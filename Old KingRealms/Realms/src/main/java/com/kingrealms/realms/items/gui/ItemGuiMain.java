package com.kingrealms.realms.items.gui;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.items.CustomItemRegistry;
import com.kingrealms.realms.items.category.CategoryButton;
import com.starmediadev.lib.gui.PaginatedGUI;

public class ItemGuiMain extends PaginatedGUI {
    public ItemGuiMain() {
        super(Realms.getInstance(), "Custom Items", false, 18);
        
        int pos = 0;
        setButton(pos++, new CategoryButton(CustomItemRegistry.HAMMERS));
        setButton(pos++, new CategoryButton(CustomItemRegistry.EXCAVATORS));
        setButton(pos++, new CategoryButton(CustomItemRegistry.LUMBERAXES));
        setButton(pos++, new CategoryButton(CustomItemRegistry.SPAWNER_SHARDS));
        setButton(pos++, new CategoryButton(CustomItemRegistry.SPAWNERS));
        setButton(pos++, new CategoryButton(CustomItemRegistry.MYSTICAL_SLIVERS));
        setButton(pos++, new CategoryButton(CustomItemRegistry.SOUL_FRAGMENTS));
        setButton(pos++, new CategoryButton(CustomItemRegistry.MYSTICAL_RESOURCES));
        setButton(pos++, new CategoryButton(CustomItemRegistry.CROP_ITEMS));
        setButton(pos++, new CategoryButton(CustomItemRegistry.CROP_SCRAPS));
        setButton(pos++, new CategoryButton(CustomItemRegistry.RESOURCES));
        //setButton(pos++, new CategoryButton(CustomItemRegistry.MISC_ITEMS));
        setButton(pos++, new CategoryButton(CustomItemRegistry.LEGENDARY_ITEMS));
        setButton(pos++, new CategoryButton(CustomItemRegistry.MACHINES));
        setButton(pos++, new CategoryButton(CustomItemRegistry.ARCANE_SAPLINGS));
        setButton(pos++, new CategoryButton(CustomItemRegistry.WOOD_CHIPS));
        setButton(pos, new CategoryButton(CustomItemRegistry.TREE_DROPS));
    }
}