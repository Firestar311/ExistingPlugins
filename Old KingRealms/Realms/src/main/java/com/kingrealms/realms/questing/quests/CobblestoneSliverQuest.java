package com.kingrealms.realms.questing.quests;

import com.kingrealms.realms.items.CustomItemRegistry;
import com.kingrealms.realms.questing.rewards.ItemReward;
import com.kingrealms.realms.questing.tasks.types.ItemGatherTask;
import com.kingrealms.realms.skills.mining.ResourceType;
import com.starmediadev.lib.util.ID;

public class CobblestoneSliverQuest extends Quest {
    public CobblestoneSliverQuest() {
        super("Obtain Cobblestone Slivers", new ID("cobblestone_sliver_quest"));
        setDescription("Mystical Slivers allow you to craft renewable blocks of that resource type. They can be obtained by breaking cobblestone and stone.");
        addTask(new ItemGatherTask(new ID("cobblestone_sliver_task"), this.id, "Obtain 8 Cobblestone Slivers", CustomItemRegistry.MYSTICAL_SLIVERS.getItem(ResourceType.COBBLESTONE).getItemStack(), 8));
        addReward(new ItemReward("Mystical Cobblestone (x4)", CustomItemRegistry.MYSTICAL_RESOURCES.getItem(ResourceType.COBBLESTONE).getItemStack(4)));
        addReward(new ItemReward("Mystical Coal (x1)", CustomItemRegistry.MYSTICAL_RESOURCES.getItem(ResourceType.COAL).getItemStack(1)));
    }
}