package com.kingrealms.realms.questing.quests;

import com.kingrealms.realms.profile.RealmProfile;
import com.kingrealms.realms.questing.tasks.types.NetherPickaxeTask;
import com.starmediadev.lib.util.ID;

public class NetherPickaxeCraftQuest extends Quest {
    public NetherPickaxeCraftQuest() {
        super("Craft a Nether Pickaxe", new ID("nether_pickaxe_quest"));
        addTask(new NetherPickaxeTask(this.id));
        setDescription("This is crafted with 3 Diamond Blocks on the top row and then 2 Gold Blocks in the remaining Middle Slots");
    }
    
    @Override
    public boolean onComplete(RealmProfile profile) {
        if (super.onComplete(profile)) {
            profile.sendMessage("&4&lPortal Keeper &cGood, now &c&lmine &c32 more obsidian with that pickaxe. You don't need it for the portal, but I am making you do it anyways.");
            return true;
        }
        
        return false;
    }
}