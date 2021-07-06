package com.kingrealms.realms.questing.tasks.types;

import com.kingrealms.realms.api.events.RealmsAPI;
import com.kingrealms.realms.profile.RealmProfile;
import com.starmediadev.lib.items.NBTWrapper;
import com.starmediadev.lib.util.ID;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

public class NetherPickaxeTask extends ItemCraftTask {
    public NetherPickaxeTask(ID questId) {
        super(new ID("nether_pickaxe_task"), questId, "Craft a Nether Pickaxe");
    }
    
    @EventHandler
    public void onCraft(CraftItemEvent e) {
        ItemStack result = e.getInventory().getResult();
        try {
            String id = NBTWrapper.getNBTString(result, "itemid");
            if (!id.equalsIgnoreCase("nether_pickaxe")) return;
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