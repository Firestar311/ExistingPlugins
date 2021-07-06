package com.starmediadev.lib.gui;

import com.starmediadev.lib.util.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryListenerGUI implements Listener {
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() != null && event.getInventory().getHolder() instanceof PaginatedGUI) {
            Player player = ((Player) event.getWhoClicked());
            PaginatedGUI paginatedGUI = (PaginatedGUI) event.getInventory().getHolder();
            if (event.getSlot() != event.getRawSlot()) { return; }
            GUIButton button = paginatedGUI.getButton(event.getSlot());
            if (paginatedGUI.getAllowInsert()) {
                if (button != null) {
                    event.setCancelled(!button.getAllowRemoval());
                    if (button != null && button.getListener() != null) {
                        button.getListener().onClick(event);
                    } else if (!paginatedGUI.allowedToInsert(event.getSlot())) {
                        player.sendMessage(Utils.color("&cYou are not allowed to insert items into that slot."));
                        return;
                    }
                }
            } else {
                if (button != null) {
                    event.setCancelled(!button.getAllowRemoval());
                    
                    if (button.getListener() != null) {
                        button.getListener().onClick(event);
                    }
                }
            }
            paginatedGUI.callExtraListeners(event);
        }
    }
}