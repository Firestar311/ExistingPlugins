package com.kingrealms.realms.economy.shop.types.impl.gui;

import com.kingrealms.realms.economy.shop.types.IGuiShop;
import com.kingrealms.realms.economy.shop.types.ServerShop;
import com.starmediadev.lib.builder.ItemBuilder;
import com.starmediadev.lib.gui.PaginatedGUI;
import com.starmediadev.lib.util.Utils;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@SerializableAs("ServerGUIShop")
public class ServerGUIShop extends ServerShop implements IGuiShop {
    
    private ItemStack icon; //mysql
    private Map<String, ShopCategory> categories = new TreeMap<>();
    
    public ServerGUIShop(Material iconType) {
        super();
        this.icon = ItemBuilder.start(iconType).withName("&e" + name).withLore(Utils.wrapLore(40, this.description)).buildItem();
    }
    
    public ServerGUIShop(Map<String, Object> serialized) {
        super(serialized);
        this.icon = (ItemStack) serialized.get("icon");
        serialized.forEach((key, value) -> {
            if (key.startsWith("category-")) {
                ShopCategory category = (ShopCategory) value;
                categories.put(category.getId(), category);
            }
        });
    }
    
    public Map<String, Object> serialize() {
        return new HashMap<>(super.serialize()) {{ 
            put("icon", icon);
            for (Entry<String, ShopCategory> entry : categories.entrySet()) {
                put("category-" + entry.getKey(), entry.getValue());
            }
        }};
    }
    
    public void addCategory(ShopCategory category) {
        this.categories.put(category.getId(), category);
    }
    
    public ShopCategory getCategory(String id) {
        return this.categories.get(id);
    }
    
    public Collection<ShopCategory> getCategories() {
        return new ArrayList<>(categories.values());
    }
    
    @Override
    public PaginatedGUI getGui() {
        return new ShopGui(this);
    }
    
    @Override
    public ItemStack getIcon() {
        return icon;
    }
    
    @Override
    public void update() {}
}