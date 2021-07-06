package com.kingrealms.realms.questing.tasks.types;

import com.kingrealms.realms.profile.RealmProfile;
import com.starmediadev.lib.util.ID;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;

public class EntityItemSubmitTask extends EntityInteractTask {
    
    private ItemStack itemStack;
    
    public EntityItemSubmitTask(ID id, ID questId, String name) {
        super(id, questId, name);
    }
    
    @Override
    @EventHandler
    public void entityInteract(PlayerInteractAtEntityEvent e) {
        super.entityInteract(e);
    }
    
    @Override
    public String onComplete(RealmProfile profile) {
        if (profile.getInventory().containsAtLeast(itemStack, itemStack.getAmount())) {
            profile.getInventory().removeItem(itemStack);
        } else {
            return "";
        }
        return super.onComplete(profile);
    }
    
    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }
    
    public ItemStack getItemStack() {
        return itemStack;
    }
    
    @Override
    public String getProgressLine(RealmProfile profile) {
        return getName();
    }
}