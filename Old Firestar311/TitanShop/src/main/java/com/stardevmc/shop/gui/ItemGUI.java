package com.stardevmc.shop.gui;

import com.firestar311.lib.builder.ItemBuilder;
import com.firestar311.lib.gui.GUIButton;
import com.firestar311.lib.gui.PaginatedGUI;
import com.firestar311.lib.items.NBTWrapper;
import com.firestar311.lib.player.User;
import com.firestar311.lib.util.Utils;
import com.stardevmc.shop.ShopUtils;
import com.stardevmc.shop.TitanShop;
import com.stardevmc.shop.objects.ShopItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class ItemGUI extends PaginatedGUI {
    
    private List<ShopItem> items = new ArrayList<>();
    
    public ItemGUI() {
        super(TitanShop.getInstance(), "&2Shop Items", true, 54, true);
        
        TitanShop plugin = TitanShop.getInstance();
        for (ShopItem item : plugin.getItemManager().getItems()) {
            UUID creator = item.getCreator();
            User info;
    
            if (creator != null) {
                if (!TitanShop.getInstance().getServerUUID().equals(creator))
                    info = TitanShop.getInstance().getPlayerManager().getUser(creator);
                else {
                    info = new User(creator);
                    info.setLastName("Server");
                }
            } else {
                item.setCreator(plugin.getServerUUID());
                info = new User(plugin.getServerUUID());
                info.setLastName("&cServer");
            }
    
            ItemStack displayItem;
            try {
                displayItem = ShopUtils.generatePreShopItem(info, item.getItemStack(), item.getDisplayName(), item.getPrices().buy(), item.getPrices().sell(), item.getUuid());
            } catch (Exception e) {
                continue;
            }
            GUIButton itemButton = new GUIButton(displayItem);
            itemButton.setAllowRemoval(true);
            addButton(itemButton);
        }
        
        ItemStack newPageItem = ItemBuilder.start(Material.PAPER).withName("&aNew Page").buildItem();
        GUIButton newPageButton = new GUIButton(newPageItem);
        newPageButton.setListener(e -> {
            Player player = ((Player) e.getWhoClicked());
            Inventory inv = e.getClickedInventory();
            handleItems(player, inv, true);
            currentPage++;
            refreshInventory(player);
        });
    
        setToolbarItem(6, newPageButton);
    
        ItemStack saveCurrent = ItemBuilder.start(Material.WRITABLE_BOOK).withName("&eSave Page").buildItem();
        GUIButton saveCurrentButton = new GUIButton(saveCurrent);
        saveCurrentButton.setListener(e -> {
            Player player = ((Player) e.getWhoClicked());
            Inventory inv = e.getClickedInventory();
            handleItems(player, inv, false);
            plugin.getShopManager().recalculateShopItems();
        });
    
        setToolbarItem(7, saveCurrentButton);
    
        ItemStack saveItem = ItemBuilder.start(Material.ENCHANTED_BOOK).withName("&6Save Items").buildItem();
        GUIButton saveButton = new GUIButton(saveItem);
        saveButton.setListener(e -> {
            Player player = ((Player) e.getWhoClicked());
            Inventory inv = e.getClickedInventory();
            handleItems(player, inv, true);
    
            for (ShopItem item : items) {
                plugin.getItemManager().addItem(item);
            }
            player.closeInventory();
            plugin.getShopManager().recalculateShopItems();
        });
    
        setToolbarItem(8, saveButton);
    }
    
    private void handleItems(Player player, Inventory inv, boolean removeItems) {
        for (int i = 0; i <= 44; i++) {
            ItemStack is = inv.getItem(i);
            int index = ShopUtils.getNextIndex(currentPage, maxSlots, i, paginated);
            if (is != null) {
                try {
                    String name = NBTWrapper.getNBTString(is, "shopname");
                    String rawbuy = NBTWrapper.getNBTString(is, "shopbuy");
                    String rawsell = NBTWrapper.getNBTString(is, "shopsell");
                    String rawcreator = NBTWrapper.getNBTString(is, "shopcreator");
                    
                    double buy = Double.parseDouble(rawbuy);
                    double sell = Double.parseDouble(rawsell);
                    UUID uuid = null;
                    try {
                        uuid = UUID.fromString(rawcreator);
                    } catch (Exception e) {}
    
                    if (removeItems) inv.setItem(i, null);
                    setButton(index, new GUIButton(is));
                    
                    is = ShopUtils.resetGuiItemTags(is);
                    ItemMeta itemMeta = is.getItemMeta();
                    itemMeta.setLore(Collections.emptyList());
                    is.setItemMeta(itemMeta);
                    
                    ShopItem shopItem = new ShopItem((uuid == null ? TitanShop.getInstance().getServerUUID() : uuid), is, name, buy, sell);
                    items.add(shopItem);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    player.sendMessage(Utils.color("&cThere was an error getting item information from the item in slot " + i));
                }
            } else {
                removeButton(index);
                this.items.remove(index);
            }
        }
    }
}
