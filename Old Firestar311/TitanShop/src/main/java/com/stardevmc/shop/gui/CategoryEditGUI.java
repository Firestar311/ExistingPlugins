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
import com.stardevmc.shop.objects.shops.gui.GUIItem;
import com.stardevmc.shop.objects.shops.gui.GUIShopCategory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.Map.Entry;

public class CategoryEditGUI extends PaginatedGUI {
    
    private Map<Integer, GUIItem> items = new HashMap<>();
    
    public CategoryEditGUI(GUIShopCategory category) {
        super(TitanShop.getInstance(), "Edit Category: " + category.getName(), true, 54, true);
        
        for (Entry<Integer, GUIItem> mapEntry : category.getShopItems().entrySet()) {
            Integer key = mapEntry.getKey();
            ShopItem item = mapEntry.getValue();
            ItemStack displayItem;
            UUID creator = item.getCreator();
            User info = null;
            
            if (creator != null) {
                if (TitanShop.getInstance().getServerUUID().equals(creator)) {
                    info = new User(creator);
                    info.setLastName("Server");
                } else {
                    info = TitanShop.getInstance().getPlayerManager().getUser(creator);
                }
            }
            
            try {
                displayItem = ShopUtils
                        .generatePreShopItem(info, item.getItemStack(), item.getDisplayName(), item.getPrices()
                                                                                                   .buy(), item
                                .getPrices().sell(), item.getUuid());
            } catch (Exception e) {
                continue;
            }
            
            GUIButton itemButton = new GUIButton(displayItem);
            itemButton.setAllowRemoval(true);
            setButton(key, itemButton);
        }
        
        ItemStack newPageItem = ItemBuilder.start(Material.PAPER).withName("&aNew Page").buildItem();
        GUIButton newPageButton = new GUIButton(newPageItem);
        newPageButton.setListener(e -> {
            Player player = ((Player) e.getWhoClicked());
            Inventory inv = e.getClickedInventory();
            handleItems(player, inv, category, true);
            currentPage++;
            refreshInventory(player);
        });
        
        setToolbarItem(6, newPageButton);
        
        ItemStack saveCurrent = ItemBuilder.start(Material.WRITABLE_BOOK).withName("&eSave Page").buildItem();
        GUIButton saveCurrentButton = new GUIButton(saveCurrent);
        saveCurrentButton.setListener(e -> {
            Player player = ((Player) e.getWhoClicked());
            Inventory inv = e.getClickedInventory();
            handleItems(player, inv, category, false);
        });
        
        setToolbarItem(7, saveCurrentButton);
        
        ItemStack saveItem = ItemBuilder.start(Material.ENCHANTED_BOOK).withName("&6Save Category").buildItem();
        GUIButton saveButton = new GUIButton(saveItem);
        saveButton.setListener(e -> {
            Player player = ((Player) e.getWhoClicked());
            Inventory inv = e.getClickedInventory();
            handleItems(player, inv, category, true);
            
            category.clearItems();
            for (Entry<Integer, GUIItem> entry : items.entrySet()) {
                int place = entry.getKey();
                GUIItem item = entry.getValue();
                item.setPlace(place);
                category.addShopItem(item);
                
                TitanShop titanShop = ((TitanShop) plugin);
                titanShop.getItemManager().addItem(item);
            }
            player.closeInventory();
        });
        
        setToolbarItem(8, saveButton);
    }
    
    private void handleItems(Player player, Inventory inv, GUIShopCategory category, boolean removeItems) {
        for (int i = 0; i <= 44; i++) {
            ItemStack is = inv.getItem(i);
            int index = ShopUtils.getNextIndex(currentPage, maxSlots, i, paginated);
            if (is != null) {
                try {
                    String name = NBTWrapper.getNBTString(is, "shopname");
                    String rawbuy = NBTWrapper.getNBTString(is, "shopbuy");
                    String rawsell = NBTWrapper.getNBTString(is, "shopsell");
                    String rawcreator = NBTWrapper.getNBTString(is, "shopcreator");
                    String rawuuid = NBTWrapper.getNBTString(is, "shopuuid");
                    
                    double buy = Double.parseDouble(rawbuy);
                    double sell = Double.parseDouble(rawsell);
                    UUID creator = null, uuid = null;
                    try {
                        creator = UUID.fromString(rawcreator);
                    } catch (Exception e) {
                    }
                    
                    try {
                        uuid = UUID.fromString(rawuuid);
                    } catch (Exception e) {
                    }
    
                    if (removeItems) { inv.setItem(i, null); }
                    setButton(index, new GUIButton(is));
                    
                    is = ShopUtils.resetGuiItemTags(is);
                    is = NBTWrapper.resetTags(is, "shopuuid");
                    
                    ItemMeta itemMeta = is.getItemMeta();
                    List<String> lore = itemMeta.getLore();
                    Iterator<String> loreIterator = lore.iterator();
                    while (loreIterator.hasNext()) {
                        String loreLine = loreIterator.next();
                        if (loreLine.toLowerCase().contains("This Item is a Shop Item and holds".toLowerCase())) {
                            loreIterator.remove();
                        }
                        if (loreLine.toLowerCase().contains("information for Shop use only.".toLowerCase())) {
                            loreIterator.remove();
                        }
                        if (loreLine.toLowerCase().contains(Utils.blankLine(35))) { loreIterator.remove(); }
                        if (loreLine.toLowerCase().contains("Display Name:".toLowerCase())) { loreIterator.remove(); }
                        if (loreLine.toLowerCase().contains("Buy Price:".toLowerCase())) { loreIterator.remove(); }
                        if (loreLine.toLowerCase().contains("Sell Price:".toLowerCase())) { loreIterator.remove(); }
                        if (loreLine.toLowerCase().contains("Creator:".toLowerCase())) { loreIterator.remove(); }
                    }
                    itemMeta.setLore(lore);
                    is.setItemMeta(itemMeta);
                    
                    ShopItem shopItem = TitanShop.getInstance().getItemManager().getItem(uuid);
                    if (shopItem == null) { shopItem = new ShopItem(creator, is, name, buy, sell); }
                    GUIItem guiItem = new GUIItem(shopItem, category, index);
                    items.put(index, guiItem);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    player.sendMessage(Utils
                            .color("&cThere was an error getting item information from the item in slot " + i));
                }
            } else {
                removeButton(index);
                this.items.remove(index);
            }
        }
    }
}