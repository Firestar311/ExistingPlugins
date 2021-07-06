package com.kingrealms.realms.questing.quests;

import com.kingrealms.realms.profile.RealmProfile;
import com.starmediadev.lib.util.ID;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class NetherObsidianQuest extends NetherItemQuest {
    public NetherObsidianQuest() {
        super("Sacrifice Obsidian", new ID("obsidian_quest"), new ItemStack(Material.OBSIDIAN, 64), new ID("obsidian_task"), "Sacrifice a stack of obsidian");
    }
    
    @Override
    public boolean onComplete(RealmProfile profile) {
        if (super.onComplete(profile)) {
            profile.sendMessage("&4&lPortal Keeper &cI hope that took a while.");
            profile.sendMessage("&4&lPortal Keeper &cYou need more obsidian, but you need a special tool for that.");
            return true;
        }
        return false;
    }
}