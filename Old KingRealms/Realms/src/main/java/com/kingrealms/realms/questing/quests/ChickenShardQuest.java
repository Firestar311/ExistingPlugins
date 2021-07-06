package com.kingrealms.realms.questing.quests;

import com.kingrealms.realms.items.CustomItemRegistry;
import com.kingrealms.realms.questing.rewards.ItemReward;
import com.kingrealms.realms.questing.tasks.types.ItemGatherTask;
import com.starmediadev.lib.util.ID;
import org.bukkit.entity.EntityType;

public class ChickenShardQuest extends Quest {
    public ChickenShardQuest() {
        super("Obtain Chicken Shards", new ID("chicken_shard_quest"));
        setDescription("Spawner shards allow you to craft Mob Spawners. Chickens can be found in the wild. Shards can be obtained by killing the mob type.");
        addTask(new ItemGatherTask(new ID("chicken_shard_task"), this.id, "Obtain 8 Chicken Spawner Shards", CustomItemRegistry.SPAWNER_SHARDS.getItem(EntityType.CHICKEN).getItemStack(), 8));
        addReward(new ItemReward("Chicken Spawners (x4)", CustomItemRegistry.SPAWNERS.getItem(EntityType.CHICKEN).getItemStack(4)));
        addReward(new ItemReward("Pig Spawners (x1)", CustomItemRegistry.SPAWNERS.getItem(EntityType.PIG).getItemStack(1)));
    }
}