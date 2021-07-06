package com.kingrealms.realms.questing.quests;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.profile.RealmProfile;
import com.kingrealms.realms.questing.tasks.types.EntityCoinSubmitTask;
import com.starmediadev.lib.util.ID;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class NetherPaymentQuest extends Quest {
    public NetherPaymentQuest() {
        super("Pay the Portal Keeper", new ID("pay_portal_keeper"));
    
        UUID entityId = Realms.getInstance().getSettingsManager().getNetherPortalKeeper();
        EntityCoinSubmitTask task = new EntityCoinSubmitTask(new ID("nether_coin_task"), id, "Sacrifice 1 million coins", 1000000);
        if (entityId != null) {
            task.setEntityId(entityId);
        } else {
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
        addTask(task);
    }
    
    @Override
    public boolean onComplete(RealmProfile profile) {
        if (super.onComplete(profile)) {
            profile.sendMessage("&4&lPortal Keeper &cThank you, now you can build the portal.");
            return true;
        }
        
        return false;
    }
}