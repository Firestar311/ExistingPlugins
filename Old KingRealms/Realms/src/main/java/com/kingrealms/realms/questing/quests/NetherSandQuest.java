package com.kingrealms.realms.questing.quests;

import com.kingrealms.realms.profile.RealmProfile;
import com.starmediadev.lib.util.ID;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class NetherSandQuest extends NetherItemQuest {
    
    public NetherSandQuest() {
        super("Sacrifice Sand", new ID("lots_of_sand_quest"), new ItemStack(Material.SAND, 64), new ID("sand_task"), "Sacrifice a stack of sand.");
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