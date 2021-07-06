package com.firestar311.lib.gui;

import com.firestar311.lib.util.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryListenerGUI implements Listener {
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Determine if the Inventory was a PaginatedGUI
        if (event.getInventory().getHolder() != null && event.getInventory().getHolder() instanceof PaginatedGUI) {
            Player player = ((Player) event.getWhoClicked());
    
            // Get the instance of the PaginatedGUI that was clicked.
            PaginatedGUI paginatedGUI = (PaginatedGUI) event.getInventory().getHolder();
    
            if (event.getSlot() != event.getRawSlot()) return;
            if (paginatedGUI.getAllowInsert()) {
                if (!paginatedGUI.allowedToInsert(event.getSlot())) {
                    player.sendMessage(Utils.color("&cYou are not allowed to insert items into that slot."));
                }
    
                // Then, assume the slot holds a GUIButton and attempt to get the button.
                GUIButton button = paginatedGUI.getButton(event.getSlot());
                
                if (button != null) {
                    event.setCancelled(!button.getAllowRemoval());
                    // Finally, if the slot did actually hold a GUIButton (that has a listener)...
                    if (button != null && button.getListener() != null) {
                        // ...execute that button's listener.
                        button.getListener().onClick(event);
                    }
                }
            } else {
                // Then, assume the slot holds a GUIButton and attempt to get the button.
                GUIButton button = paginatedGUI.getButton(event.getSlot());
                if (button != null) {
                    event.setCancelled(!button.getAllowRemoval());
    
                    // Finally, if the slot did actually hold a GUIButton (that has a listener)...
                    if (button.getListener() != null) {
                        // ...execute that button's listener.
                        button.getListener().onClick(event);
                    }
                }
            }
            paginatedGUI.callExtraListeners(event);
        }
    }
}