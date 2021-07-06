package com.kingrealms.realms.questing.tasks.types;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.api.events.RealmsAPI;
import com.kingrealms.realms.profile.RealmProfile;
import com.kingrealms.realms.questing.tasks.Task;
import com.starmediadev.lib.util.ID;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.UUID;

public class EntityInteractTask extends Task {
    
    private UUID entityId;
    
    public EntityInteractTask(ID id, ID questId, String name) {
        super(id, questId, name);
    }
    
    @EventHandler
    public void entityInteract(PlayerInteractAtEntityEvent e) {
        if (e.getRightClicked().getUniqueId().equals(entityId) && e.getHand().equals(EquipmentSlot.HAND)) {
            RealmProfile profile = RealmsAPI.getProfile(e.getPlayer());
            if (!profile.isQuestLocked(getQuest())) {
                if (!RealmsAPI.getProfile(e.getPlayer()).isTaskComplete(questId, id)) {
                    onComplete(Realms.getInstance().getProfileManager().getProfile(e.getPlayer()));
                    e.setCancelled(true);
                }
            }
        }
    }
    
    public UUID getEntityId() {
        return entityId;
    }
    
    public void setEntityId(UUID entityId) {
        this.entityId = entityId;
    }
    
    @Override
    public String getProgressLine(RealmProfile profile) {
        return getName();
    }
}