package com.stardevmc.shop.objects.shops.gui;

import com.stardevmc.shop.objects.ShopItem;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class GUIItem extends ShopItem {
    
    private GUIShopCategory parent;
    private int place = -1;
    
    public GUIItem(UUID uuid, UUID creator, ItemStack itemStack, String displayName, double buy, double sell) {
        super(uuid, creator, itemStack, displayName, buy, sell);
    }
    
    public GUIItem(ShopItem item, GUIShopCategory parent) {
        super(item.getUuid(), item.getCreator(), item.getItemStack(), item.getDisplayName(), item.getPrices().buy(), item.getPrices().sell());
        this.parent = parent;
    }
    public GUIItem(ShopItem item, GUIShopCategory parent, int place) {
        this(item, parent);
        this.place = place;
    }
    
    public GUIShopCategory getParent() {
        return parent;
    }
    
    public void setParent(GUIShopCategory parent) {
        this.parent = parent;
    }
    
    public int getPlace() {
        return place;
    }
    
    public void setPlace(int place) {
        this.place = place;
    }
}
