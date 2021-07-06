package com.kingrealms.realms.questing.quests;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.profile.RealmProfile;
import com.kingrealms.realms.questing.tasks.types.EntityItemSubmitTask;
import com.starmediadev.lib.util.ID;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class NetherItemQuest extends Quest {
    private ID itemTask;
    
    public NetherItemQuest(String name, ID id, ItemStack itemStack, ID itemTask, String taskName) {
        super(name, id);
        this.itemTask = itemTask;
        EntityItemSubmitTask task = new EntityItemSubmitTask(this.itemTask, id, taskName);
        task.setItemStack(itemStack);
        addTask(task);
        new BukkitRunnable() {
            @Override
            public void run() {
                UUID entityId = Realms.getInstance().getSettingsManager().getNetherPortalKeeper();
                if (entityId != null) {
                    task.setEntityId(entityId);
                }
            }
        }.runTaskTimer(Realms.getInstance(), 20L, 20L);
    }
    
    @Override
    public boolean checkComplete(RealmProfile profile) {
        if (((EntityItemSubmitTask) getTask(itemTask)).getEntityId() == null) {
            return false;
        }
        
        return super.checkComplete(profile);
    }
}