package com.kingrealms.realms.questing.quests;

import com.kingrealms.realms.profile.RealmProfile;
import com.kingrealms.realms.questing.tasks.EnterDimensionTask;
import com.starmediadev.lib.util.ID;
import org.bukkit.World.Environment;

public class EnterTheNetherQuest extends Quest {
    public EnterTheNetherQuest() {
        super("Enter the Nether", new ID("enter_the_nether"));
        addTask(new EnterDimensionTask(new ID("enter_nether_task"), this.id, "Visit the Nether", Environment.NETHER));
    }
    
    @Override
    public boolean onComplete(RealmProfile profile) {
        if (super.onComplete(profile)) {
            profile.sendMessage("&4&lPortal Keeper &cYou may now enter and leave the nether as you please.");
            return true;
        }
        
        return false;
    }
}