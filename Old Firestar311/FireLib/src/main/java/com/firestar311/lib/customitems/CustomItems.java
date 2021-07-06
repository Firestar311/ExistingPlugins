package com.firestar311.lib.customitems;

import com.firestar311.lib.FireLib;
import com.firestar311.lib.customitems.api.IItemManager;

public final class CustomItems {
    
    private IItemManager itemManager;
    
    public CustomItems(FireLib plugin) {
        this.itemManager = new CustomItemManager(plugin);
        plugin.getServer().getPluginManager().registerEvents(new PlayerListener(plugin), plugin);
        plugin.getCommand("customitem").setExecutor(new CustomItemCommand(plugin));
    }
    
    public IItemManager getItemManager() {
        return itemManager;
    }
}
