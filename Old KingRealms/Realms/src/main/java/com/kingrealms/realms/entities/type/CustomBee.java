package com.kingrealms.realms.entities.type;

import com.kingrealms.realms.entities.controller.LookController;
import com.kingrealms.realms.items.CustomItemRegistry;
import com.kingrealms.realms.loot.*;
import com.kingrealms.realms.util.RealmsLoot;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.bukkit.entity.EntityType;

public class CustomBee extends Bee implements ICustomEntity {
    private boolean custom = false;
    
    public CustomBee(Level world) {
        super(net.minecraft.world.entity.EntityType.BEE, world);
        this.lookControl = new LookController(this);
    }
    
    @Override
    public LootTable getDropTable() {
        LootTable lootTable = new EntityLootTable(EntityType.BEE);
        lootTable.addPossibleLoot(new RealmsLoot(CustomItemRegistry.HONEY, Rarity.RARE));
        lootTable.addPossibleLoot(new RealmsLoot(CustomItemRegistry.HONEY_COMB, Rarity.UNCOMMON));
        lootTable.addPossibleLoot(new RealmsLoot((CustomItemRegistry.YELLOW_DYE), Rarity.UNCOMMON));
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