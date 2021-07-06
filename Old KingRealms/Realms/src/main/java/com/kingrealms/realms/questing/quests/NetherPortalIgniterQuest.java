package com.kingrealms.realms.questing.quests;

import com.kingrealms.realms.profile.RealmProfile;
import com.kingrealms.realms.questing.tasks.types.NetherPortalIgniterTask;
import com.starmediadev.lib.util.ID;

public class NetherPortalIgniterQuest extends Quest {
    public NetherPortalIgniterQuest() {
        super("Craft a Portal Igniter", new ID("portal_igniter"));
        addTask(new NetherPortalIgniterTask(this.id));
        setDescription("This is crafted with a Block of Iron and a Flint");
    }
    
    @Override
    public boolean onComplete(RealmProfile profile) {
        if (super.onComplete(profile)) {
            profile.sendMessage("&4&lPortal Keeper &cGood, now you need to pay some money, just a small amount.");
            return true;
        }
        
        return false;
    }
}