package com.firestar311.lib.region;

import com.firestar311.lib.builder.ItemBuilder;
import com.firestar311.lib.customitems.CustomItemFactory;
import com.firestar311.lib.customitems.api.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class RegionWandToolHook {
    
    private JavaPlugin plugin;
    private ICategory category;
    
    public RegionWandToolHook(JavaPlugin plugin, Material categoryIcon) {
        this.plugin = plugin;
        RegisteredServiceProvider<IItemManager> rsp = plugin.getServer().getServicesManager().getRegistration(IItemManager.class);
        if (rsp == null) return;
        IItemManager itemManager = rsp.getProvider();
        
        ItemStack icon = ItemBuilder.start(categoryIcon).withName("&e" + plugin.getName() + " Region Tools").buildItem();
        this.category = CustomItemFactory.createCategory(plugin, plugin.getName() + " Region Tools", icon, "region.tools");
        itemManager.addCategory(category);
        
        ItemStack wandTool = ItemBuilder.start(Material.DIAMOND_AXE).withName("&bWand Tool").withLore("&7Use this tool to set region points").buildItem();
        ICustomItem wandCItem = CustomItemFactory.createCustomItem(plugin, "regiontool", wandTool, "firelib.items.tool.wand");
        category.addItem(wandCItem);
    }
    
    public JavaPlugin getPlugin() {
        return plugin;
    }
    
    public ICategory getCategory() {
        return category;
    }
}