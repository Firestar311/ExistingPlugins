package com.stardevmc.shop.gui;

import com.firestar311.lib.builder.ItemBuilder;
import com.firestar311.lib.gui.GUIButton;
import com.firestar311.lib.gui.PaginatedGUI;
import com.firestar311.lib.util.Utils;
import com.stardevmc.shop.ShopUtils;
import com.stardevmc.shop.TitanShop;
import com.stardevmc.shop.objects.shops.gui.GUIItem;
import com.stardevmc.shop.objects.shops.gui.GUIShopCategory;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map.Entry;

public class CategoryGUI extends PaginatedGUI {
    public CategoryGUI(GUIShopCategory category) {
        super(TitanShop.getInstance(), "Category: " + category.getName(), true, 54, false);
        
        GUIButton backToCategory = new GUIButton(ItemBuilder.start(Material.SPECTRAL_ARROW).withName("&c<- Back to " + category.getParent().getName()).buildItem());
        backToCategory.setListener(e -> category.getParent().getGUI().openGUI(((Player) e.getWhoClicked())));
        setToolbarItem(0, backToCategory);
        
        for (Entry<Integer, GUIItem> entry : category.getShopItems().entrySet()) {
            Integer place = entry.getKey();
            GUIItem item = entry.getValue();
            ItemStack displayStack = ItemBuilder.start(item.getItemStack().getType()).withName("&f" + item.getDisplayName()).withLore("&8" + Utils.blankLine(35), "&fPrice &b$" + item.getPrices().buy(), "&fSells for &b$" + item.getPrices().sell(), "", "&6&lLeft Click &fto buy!", "&6&lShift Click &fto buy 64. &7($" + (item.getPrices().buy() * 64) + ")", "&6&lRight Click &fto sell.").buildItem();
            GUIButton button = new GUIButton(displayStack);
            button.setAllowRemoval(false);
            button.setListener(e -> {
                Player player = ((Player) e.getWhoClicked());
                Economy economy = TitanShop.getInstance().getEconomy();
                if (e.isLeftClick()) {
                    double balance = economy.getBalance(player);
                    double buyPrice = item.getPrices().buy();
                    int amount = 1;
                    if (e.isShiftClick()) {
                        buyPrice = buyPrice * 64;
                        amount = 64;
                    }
                    if (balance <= 0 || balance < buyPrice) {
                        player.sendMessage(Utils.color("&cYou do not have the funds to buy this item."));
                        return;
                    }
                    
                    EconomyResponse response = economy.withdrawPlayer(player, buyPrice);
                    if (response.transactionSuccess()) {
                        ItemStack itemStack = new ItemStack(item.getItemStack());
                        itemStack.setAmount(amount);
                        player.getInventory().addItem(itemStack);
                        player.sendMessage(Utils.color("&aYou have bought the item &b" + item.getDisplayName() + " &afor &b" + item.getPrices().buy()));
                    } else {
                        player.sendMessage(Utils.color("&cThere was an error proccessing your transaction: " + response.errorMessage));
                    }
                } else if (e.isRightClick()) {
                    double sellPrice = item.getPrices().sell();
                    if (sellPrice == 0) {
                        player.sendMessage(Utils.color("&cYou cannot sell that item."));
                        return;
                    }
                    Inventory inv = player.getInventory();
                    if (!ShopUtils.hasMatch(item.getItemStack(), inv)) {
                        player.sendMessage(Utils.color("&cYou cannot sell that item because you do not have any in your inventory."));
                        return;
                    }
                    
                    inv.removeItem(item.getItemStack());
                    economy.depositPlayer(player, sellPrice);
                    player.sendMessage(Utils.color("&aYou sold &b" + item.getDisplayName() + " &afor &b$" + sellPrice));
                }
            });
            setButton(place, button);
        }
    }
}
