package com.firestar311.lib.region;

import com.firestar311.lib.customitems.api.IItemManager;
import com.firestar311.lib.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class RegionToolListener implements Listener {
    
    private SelectionManager selectionManager;
    private RegionWandToolHook toolHook;
    
    public RegionToolListener(SelectionManager selectionManager, RegionWandToolHook toolHook) {
        this.selectionManager = selectionManager;
        this.toolHook = toolHook;
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        if (toolHook == null) return;
        if (toolHook.getPlugin() == null) return;
    
        IItemManager itemManager = Bukkit.getServicesManager().getRegistration(IItemManager.class).getProvider();
        String name = itemManager.extractName(mainHand);
        if (name == null || name.equals("")) return;
        e.setCancelled(true);
        if (e.getClickedBlock() == null) return;
        if (name.equalsIgnoreCase("regiontool")) {
            int posNumber;
            Location location = e.getClickedBlock().getLocation();
            if (e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
                this.selectionManager.setPointA(player, location);
                posNumber = 1;
                player.sendMessage(Utils.color("&aSet pos" + posNumber + " to " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ()));
            } else if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getHand().equals(EquipmentSlot.HAND)) {
                this.selectionManager.setPointB(player, location);
                posNumber = 2;
                player.sendMessage(Utils.color("&aSet pos" + posNumber + " to " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ()));
            }
        }
    }
}