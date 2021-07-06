package com.kingrealms.realms.questing.tasks.types;

import com.kingrealms.realms.api.events.RealmsAPI;
import com.kingrealms.realms.profile.RealmProfile;
import com.starmediadev.lib.items.NBTWrapper;
import com.starmediadev.lib.util.ID;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

public class CraftkingSkillTableTask extends ItemCraftTask {
    public CraftkingSkillTableTask(ID questId) {
        super(new ID("crafting_skill_table_task"), questId, "Craft a Crafting Skill Table");
    }
    
    @EventHandler
    public void onCraft(CraftItemEvent e) {
        ItemStack result = e.getInventory().getResult();
        try {
            String id = NBTWrapper.getNBTString(result, "itemid");
            if (!id.equalsIgnoreCase("crafting_skill_table")) return;
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