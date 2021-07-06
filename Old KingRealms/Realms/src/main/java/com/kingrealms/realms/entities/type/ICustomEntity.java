package com.kingrealms.realms.entities.type;

import com.kingrealms.realms.loot.LootTable;

public interface ICustomEntity {
    
    void setCustom(boolean value);
    
    LootTable getDropTable();
    
    boolean isCustom();
}