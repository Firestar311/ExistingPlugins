package com.kingrealms.realms.questing.quests;

import com.kingrealms.realms.items.CustomItemRegistry;
import com.kingrealms.realms.questing.rewards.ItemReward;
import com.kingrealms.realms.questing.tasks.types.ItemGatherTask;
import com.kingrealms.realms.skills.farming.CropType;
import com.starmediadev.lib.util.ID;

public class WheatScrapQuest extends Quest {
    public WheatScrapQuest() {
        super("Obtain Wheat Scraps", new ID("wheat_scrap_quest"));
        setDescription("Crop scraps allow you to craft blocks that auto-grow crops faster than normal. You can find wheat in spawn. Scaps can be obtained by harvesting that crop type.");
        addTask(new ItemGatherTask(new ID("wheat_scrap_task"), this.id, "Obtain 8 Wheat Crop Scraps", CustomItemRegistry.CROP_SCRAPS.getItem(CropType.WHEAT).getItemStack(), 8));
        addReward(new ItemReward("Wheat Crop Block (x4)", CustomItemRegistry.CROP_ITEMS.getItem(CropType.WHEAT).getItemStack(4)));
        addReward(new ItemReward("Carrot Crop Block (x1)", CustomItemRegistry.CROP_ITEMS.getItem(CropType.CARROT).getItemStack(1)));
    }
}