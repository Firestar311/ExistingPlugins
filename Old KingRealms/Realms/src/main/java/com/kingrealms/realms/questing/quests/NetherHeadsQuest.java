package com.kingrealms.realms.questing.quests;

import com.kingrealms.realms.items.CustomItemRegistry;
import com.kingrealms.realms.profile.RealmProfile;
import com.starmediadev.lib.util.ID;

public class NetherHeadsQuest extends NetherItemQuest {
    public NetherHeadsQuest() {
        super("Skull Sacrifice", new ID("skeleton_heads_quest"), CustomItemRegistry.SKELETON_HEAD.getItemStack(3), new ID("skeleton_head_task"), "Give up 3 skeleton skulls.");
    }
    
    @Override
    public boolean onComplete(RealmProfile profile) {
        if (super.onComplete(profile)) {
            profile.sendMessage("&4&lPortal Keeper &cVery good, however, there is much more...");
            return true;
        }
        return false;
    }
}