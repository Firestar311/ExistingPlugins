package com.kingrealms.realms.staffmode;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.channel.channels.StaffChannel;
import com.kingrealms.realms.profile.RealmProfile;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class StaffmodeManager implements Listener {
    
    private Realms plugin = Realms.getInstance();
    
    public StaffmodeManager() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        RealmProfile profile = plugin.getProfileManager().getProfile(e.getPlayer());
        if (profile.getStaffMode().isActive()) {
            StaffChannel staffChannel = plugin.getChannelManager().getStaffChannel();
            staffChannel.sendMessage(profile.getName() + " joined in staff mode.");
            e.setJoinMessage(null);
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onItemPickup(EntityPickupItemEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        if (e.isCancelled()) return;
        
        RealmProfile profile = plugin.getProfileManager().getProfile(e.getEntity());
        if (!profile.getStaffMode().isActive()) return;
        if (!profile.getStaffMode().canPickupItems()) {
            e.setCancelled(true);
        }
    }
}