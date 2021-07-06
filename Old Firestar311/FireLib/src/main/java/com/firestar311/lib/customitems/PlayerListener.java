package com.firestar311.lib.customitems;

import com.firestar311.lib.FireLib;
import com.firestar311.lib.customitems.api.ICategory;
import com.firestar311.lib.customitems.api.ICustomItem;
import com.firestar311.lib.util.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

class PlayerListener implements Listener {
    
    private FireLib plugin;
    
    PlayerListener(FireLib plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player player = ((Player) e.getWhoClicked());
        ItemStack current = e.getCurrentItem();
        if (e.getView().getTitle() == null) return; //FIXME Change these to use the PaginatedGUI API
        if (!e.getView().getTitle().toLowerCase().contains("custom items")) return;
        e.setCancelled(true);
        if (current == null) return;
        String name = plugin.getItemManager().extractName(current);
        if (name == null || name.equals("")) {
            player.sendMessage(Utils.color("&cThere was an error getting the name of the item."));
            return;
        }
        
        if (name.equalsIgnoreCase("back")) {
            new BukkitRunnable() {
                public void run() {
                    plugin.getItemManager().openGUI(player);
                }
            }.runTaskLater(plugin, 2L);
        } else if (plugin.getItemManager().getCategory(name) != null) {
            ICategory category = plugin.getItemManager().getCategory(name);
            if (!player.hasPermission(category.getPermission())) {
                player.sendMessage(Utils.color("&cYou do not have permission to use that category"));
                return;
            }
    
            new BukkitRunnable() {
                public void run() {
                    plugin.getItemManager().openCategory(player, category);
                }
            }.runTaskLater(plugin, 2L);
        } else if (plugin.getItemManager().getCustomItem(name) != null) {
            ICustomItem item = plugin.getItemManager().getCustomItem(name);
            if (!player.hasPermission(item.getPermission())) {
                player.sendMessage(Utils.color("&cYou do not have permission for that item."));
                return;
            }
    
            player.getInventory().addItem(item.getItemStack());
            player.sendMessage(Utils.color("&aGave you the custom item &b" + item.getName()));
        }
    }
}