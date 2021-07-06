package net.firecraftmc.api.wrapper;

import net.firecraftmc.api.model.player.FirecraftPlayer;
import net.firecraftmc.api.plugin.IFirecraftCore;
import net.firecraftmc.api.vanish.VanishSetting;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryPickupItemEvent;

public class ItemPickupEvent1_8 implements Listener {
   
    private final IFirecraftCore plugin;
    
    public ItemPickupEvent1_8(IFirecraftCore plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void inventoryItemPickup(InventoryPickupItemEvent e) {
        if (e.getInventory().getHolder() instanceof Player) {
            Player p = (Player) e.getInventory().getHolder();
            FirecraftPlayer player = plugin.getPlayerManager().getPlayer(p.getUniqueId());
            if (player.isVanished()) {
                if (!player.getVanishSettings().getSetting(VanishSetting.PICKUP)) {
                    e.setCancelled(true);
                }
            }
        }
    }
}