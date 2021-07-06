package com.kingrealms.realms.questing.tasks.types;

import com.kingrealms.realms.api.events.HamletCreateEvent;
import com.starmediadev.lib.util.ID;
import org.bukkit.event.EventHandler;

public class HamletCreateTask extends HamletTask {
    public HamletCreateTask(ID id, ID questId) {
        super(id, questId, "Create a Hamlet");
        setOptional(true);
    }
    
    @EventHandler
    public void onHamletCreate(HamletCreateEvent e) {
        onComplete(e.getProfile());
    }
}