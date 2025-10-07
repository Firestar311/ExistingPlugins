package com.kingrealms.realms.entities.type;

import com.kingrealms.realms.items.CustomItemRegistry;
import com.kingrealms.realms.loot.*;
import com.kingrealms.realms.util.RealmsLoot;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.bukkit.entity.EntityType;

public class CustomBlaze extends Blaze implements ICustomEntity {
    private boolean custom = false;
    
    public CustomBlaze(ServerLevel world) {
        super(net.minecraft.world.entity.EntityType.BLAZE, world);
    }
    
    @Override
    public LootTable getDropTable() {
        LootTable lootTable = new EntityLootTable(EntityType.BLAZE);
        lootTable.addPossibleLoot(new RealmsLoot(CustomItemRegistry.BLAZE_ROD, Rarity.COMMON));
        lootTable.addPossibleLoot(new RealmsLoot(CustomItemRegistry.NETHER_WART, Rarity.UNCOMMON));
        return lootTable;
    }
    
    @Override
    public boolean isCustom() {
        return this.custom;
    }
    
    public void setCustom(boolean value) {
        this.custom = value;
        this.goalSelector.removeAllGoals(p -> true);
        this.registerGoals();
    }
    
    @SuppressWarnings("DuplicatedCode")
    @Override
    protected void registerGoals() {
        if (custom) {
            this.goalSelector.addGoal(2, new FloatGoal(this));
        } else {
            super.registerGoals();
        }
    }
    
    @Override
    public void load(ValueInput valueinput) {
        this.custom = valueinput.getBooleanOr("custom", false);
        super.load(valueinput);
    }
    
    @Override
    public boolean save(ValueOutput valueoutput) {
        valueoutput.putBoolean("custom", custom);
        return super.save(valueoutput);
    }
}