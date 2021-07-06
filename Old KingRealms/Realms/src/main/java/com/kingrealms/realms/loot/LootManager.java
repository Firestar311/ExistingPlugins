package com.kingrealms.realms.loot;

import com.starmediadev.lib.collection.IncrementalMap;

public class LootManager {
    private IncrementalMap<LootTable> lootTables = new IncrementalMap<>();
    
    public LootManager() {}
    
    public void addLootTable(LootTable lootTable) {
        int pos = lootTables.add(lootTable);
        lootTable.setId(pos);
    }
    
    public LootTable getLootTable(int i) {
        return this.lootTables.get(i);
    }
}