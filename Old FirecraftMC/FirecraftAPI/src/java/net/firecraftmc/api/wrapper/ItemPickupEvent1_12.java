package net.firecraftmc.api.wrapper;

import net.firecraftmc.api.model.player.FirecraftPlayer;
import net.firecraftmc.api.plugin.IFirecraftCore;
import net.firecraftmc.api.vanish.VanishSetting;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

public class ItemPickupEvent1_12 implements Listener {
   
    private final IFirecraftCore plugin;
    
    public ItemPickupEvent1_12(IFirecraftCore plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void itemPickup(EntityPickupItemEvent e) {
        if (e.getEntity() instanceof Player) {
            FirecraftPlayer player = plugin.getPlayerManager().getPlayer(e.getEntity().getUniqueId());
            if (player.isVanished()) {
                if (!player.getVanishSettings().getSetting(VanishSetting.PICKUP)) {
                    e.setCancelled(true);
                }
            }
        }
    }
}