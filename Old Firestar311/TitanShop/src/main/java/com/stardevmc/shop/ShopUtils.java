package com.stardevmc.shop;

import com.firestar311.lib.items.NBTWrapper;
import com.firestar311.lib.player.User;
import com.firestar311.lib.util.Utils;
import com.stardevmc.shop.objects.ShopItem;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class ShopUtils {
    
    private ShopUtils() {
    }
    
    public static ItemStack generatePreShopItem(User info, ItemStack item, String name, double buy, double sell, UUID uuid) throws Exception {
        item = NBTWrapper.addNBTString(item, "shopname", name);
        item = NBTWrapper.addNBTString(item, "shopbuy", buy + "");
        item = NBTWrapper.addNBTString(item, "shopsell", sell + "");
        
        if (info != null) item = NBTWrapper.addNBTString(item, "shopcreator", info.getUniqueId().toString());
        if (uuid != null) item = NBTWrapper.addNBTString(item, "shopuuid", uuid.toString());
        
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setLore(Arrays.asList("", Utils.color("&eThis Item is a Shop Item and holds"), Utils.color("&einformation for Shop use only."), Utils.color("&8" + Utils.blankLine(35)), Utils.color("&aDisplay Name: &b" + name), Utils.color("&aBuy Price: &b" + buy), Utils.color("&aSell Price: &b" + sell), Utils.color("&aCreator: &b" + (info != null ? info.getLastName() : "&cNot Set"))));
        item.setItemMeta(itemMeta);
        return item;
    }
    
    public static boolean hasMatch(ItemStack compare, Inventory inventory) {
        for (ItemStack itemStack : inventory.getContents()) {
            if (itemStack != null) {
                if (itemStack.isSimilar(compare)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static List<ItemStack> sellAllInventory(Player player, Inventory inventory, boolean removeUnsellable) {
        Economy economy = TitanShop.getInstance().getEconomy();
        List<ItemStack> unsellable = new ArrayList<>();
        for (int i = 0; i < 45; i++) {
            ItemStack sellItem = inventory.getItem(i);
            if (sellItem == null) continue;
            
            for (ShopItem shopItem : TitanShop.getInstance().getItemManager().getItems()) {
                int amount = sellItem.getAmount();
                double sellPrice = shopItem.getPrices().sell();
                if (sellPrice == 0) {
                    if (removeUnsellable) {
                        unsellable.add(sellItem);
                        inventory.setItem(i, null);
                    }
                    continue;
                }
                
                double totalSaleAmount = amount * sellPrice;
                inventory.setItem(i, null);
                economy.depositPlayer(player, totalSaleAmount);
                player.sendMessage(Utils.color("&aSold &b" + amount + " &aof " + shopItem.getDisplayName() + " &afor &b$" + totalSaleAmount));
            }
        }
        return unsellable;
    }
    
    public static String formatUnlocalizedName(String baseName) {
        return ChatColor.stripColor(baseName.toLowerCase().replace(" ", "_"));
    }
    
    public static int getNextIndex(int currentPage, int maxSlots, int i, boolean paginated) {
        if (currentPage > 0) {
            if (paginated) {
                return ((maxSlots - 9) * currentPage) + i;
            }
            return (maxSlots * currentPage) + i;
        }
        return i;
    }
    
    public static ItemStack resetGuiItemTags(ItemStack itemStack) throws Exception {
        itemStack = NBTWrapper.resetTags(itemStack, "shopname");
        itemStack = NBTWrapper.resetTags(itemStack, "shopbuy");
        itemStack = NBTWrapper.resetTags(itemStack, "shopsell");
        itemStack = NBTWrapper.resetTags(itemStack, "shopcreator");
        return itemStack;
    }
}