package com.kingrealms.realms.loot;

import com.kingrealms.realms.items.CustomItemRegistry;
import com.kingrealms.realms.util.RealmsLoot;
import org.bukkit.entity.EntityType;

public class EntityLootTable extends LootTable {
    public EntityLootTable(EntityType entityType) {
        super(entityType.name().toLowerCase() + "_table", 1, 3);
        addPossibleLoot(new RealmsLoot(CustomItemRegistry.SPAWNER_SHARDS.getItem(entityType), Rarity.LEGENDARY));
        addPossibleLoot(new RealmsLoot(CustomItemRegistry.SOUL_FRAGMENTS.getItem(entityType), Rarity.ULTRA_LEGENDARY));
    }
}
