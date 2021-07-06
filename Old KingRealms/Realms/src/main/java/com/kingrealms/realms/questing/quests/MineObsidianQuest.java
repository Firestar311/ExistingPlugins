package com.kingrealms.realms.questing.quests;

import com.kingrealms.realms.questing.tasks.types.MineObsidianTask;
import com.starmediadev.lib.util.ID;

public class MineObsidianQuest extends Quest {
    public MineObsidianQuest() {
        super("Obtain More Obsidian", new ID("obtain_more_obsidian"));
        addTask(new MineObsidianTask(id));
    }
}