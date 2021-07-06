package com.kingrealms.realms.questing.quests;

import com.kingrealms.realms.questing.rewards.ItemReward;
import com.kingrealms.realms.questing.rewards.MoneyReward;
import com.kingrealms.realms.questing.tasks.types.GatherLogTask;
import com.kingrealms.realms.questing.tasks.types.ItemGatherTask;
import com.starmediadev.lib.util.ID;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class GatherResourcesQuest extends Quest {
    
    public GatherResourcesQuest() {
        super("Gather Basic Resources", new ID("gather_resources_quest"));
        setDescription("Gather some starter resources to get yourself going.");
        ID gatherWood = new ID("gather_wood_task");
        addTask(new GatherLogTask(gatherWood, this.getId(), "Gather 10 Wood", 10));
        ID gatherStone = new ID("gather_stone_task");
        addTask(new ItemGatherTask(gatherStone, this.getId(), "Gather 8 Cobblestone", new ItemStack(Material.COBBLESTONE), 8));
        ID gatherCoal = new ID("gather_coal_task");
        addTask(new ItemGatherTask(gatherCoal, this.getId(), "Gather 4 Coal", new ItemStack(Material.COAL), 4));
        addReward(new MoneyReward("500 Coins", 500));
        addReward(new ItemReward("Stone Pickaxe", new ItemStack(Material.IRON_PICKAXE)));
    }
}