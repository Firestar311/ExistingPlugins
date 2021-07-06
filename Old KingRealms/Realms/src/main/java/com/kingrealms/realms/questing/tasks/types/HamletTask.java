package com.kingrealms.realms.questing.tasks.types;

import com.kingrealms.realms.questing.tasks.Task;
import com.starmediadev.lib.util.ID;

public abstract class HamletTask extends Task {
    public HamletTask(ID id, ID questId, String name) {
        super(id, questId, name);
    }
    
    @Override
    public String getDisplayLine() {
        return getName();
    }
}