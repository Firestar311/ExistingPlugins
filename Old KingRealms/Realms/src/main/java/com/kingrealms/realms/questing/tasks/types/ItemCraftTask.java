package com.kingrealms.realms.questing.tasks.types;

import com.kingrealms.realms.profile.RealmProfile;
import com.kingrealms.realms.questing.tasks.Task;
import com.starmediadev.lib.util.ID;

public abstract class ItemCraftTask extends Task {
    public ItemCraftTask(ID id, ID questId, String name) {
        super(id, questId, name);
    }
    
    @Override
    public String getProgressLine(RealmProfile profile) {
        return getName();
    }
}