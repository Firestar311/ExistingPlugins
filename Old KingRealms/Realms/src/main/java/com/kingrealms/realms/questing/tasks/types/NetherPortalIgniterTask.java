package com.kingrealms.realms.questing.tasks.types;

import com.kingrealms.realms.api.events.RealmsAPI;
import com.kingrealms.realms.profile.RealmProfile;
import com.starmediadev.lib.items.NBTWrapper;
import com.starmediadev.lib.util.ID;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.event.world.PortalCreateEvent.CreateReason;
import org.bukkit.inventory.ItemStack;

public class NetherPortalIgniterTask extends ItemCraftTask {
    public NetherPortalIgniterTask(ID questId) {
        super(new ID("nether_portal_igniter"), questId, "Craft a Portal Igniter");
    }
    
    @EventHandler
    public void onPortalCreate(PortalCreateEvent e) {
        if (e.getReason() == CreateReason.FIRE) {
            if (!(e.getEntity() instanceof Player)) return;
            Player player = (Player) e.getEntity();
            RealmProfile profile = RealmsAPI.getProfile(player);
            if (!profile.isTaskComplete(questId, id)) {
                e.setCancelled(true);
                profile.sendMessage("&4&lPortal Keeper &cYou cannot go to the nether until you have completed up to the Portal Igniter Quest.");
                return;
            }
            
            ItemStack held = player.getInventory().getItemInMainHand();
            if (held.getType().equals(Material.FLINT_AND_STEEL)) {
                try {
                    String id = NBTWrapper.getNBTString(held, "itemid");
                    if (!id.equalsIgnoreCase("portal_igniter")) {
                        e.setCancelled(true);
                        profile.sendMessage("&4&lPortal Keeper &cYou must use the portal igniter to active the portal.");
                    }
                } catch (Exception ex) {}
            } else {
                e.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onCraft(CraftItemEvent e) {
        ItemStack result = e.getInventory().getResult();
        try {
            String id = NBTWrapper.getNBTString(result, "itemid");
            if (!id.equalsIgnoreCase("portal_igniter")) return;
        } catch (Exception ex) {
            return;
        }
        
        onComplete(RealmsAPI.getProfile(e.getWhoClicked()));
    }
    
    @Override
    public String getProgressLine(RealmProfile profile) {
        return getName();
    }
}