package com.stardevmc.shop.gui;

import com.firestar311.lib.builder.ItemBuilder;
import com.firestar311.lib.gui.GUIButton;
import com.firestar311.lib.gui.PaginatedGUI;
import com.firestar311.lib.util.Utils;
import com.stardevmc.shop.ShopUtils;
import com.stardevmc.shop.TitanShop;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class SellGUI extends PaginatedGUI {
    public SellGUI() {
        super(TitanShop.getInstance(), "&aSell Items", false, 54, true);
        
        ItemStack sellStack = ItemBuilder.start(Material.NETHER_STAR).withLore("&aSell all items that can be sold").buildItem();
        GUIButton sellButton = new GUIButton(sellStack);
        sellButton.setListener(e -> {
            Player player = ((Player) e.getWhoClicked());
            Inventory inventory = e.getClickedInventory();
            List<ItemStack> unsellable = ShopUtils.sellAllInventory(player, inventory, true);
            if (!unsellable.isEmpty()) {
                unsellable.forEach(item -> player.getInventory().addItem(item));
                player.sendMessage(Utils.color("&eThere were items that could not be sold... They have been returned to you."));
            }
            player.closeInventory();
        });
        setToolbarItem(4, sellButton);
    }
}
