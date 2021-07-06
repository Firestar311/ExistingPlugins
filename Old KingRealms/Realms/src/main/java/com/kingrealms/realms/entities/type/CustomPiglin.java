package com.kingrealms.realms.entities.type;

import com.kingrealms.realms.entities.controller.LookController;
import com.kingrealms.realms.items.CustomItemRegistry;
import com.kingrealms.realms.loot.*;
import com.kingrealms.realms.loot.LootTable;
import com.kingrealms.realms.util.RealmsLoot;
import net.minecraft.server.v1_16_R1.*;
import org.bukkit.entity.EntityType;

public class CustomPiglin extends EntityPiglin implements ICustomEntity {
    private boolean custom = false;
    
    public CustomPiglin(World world) {
        super(EntityTypes.PIGLIN, world);
    }
    
    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        if (nbttagcompound.hasKey("custom")) {
            setCustom(nbttagcompound.getBoolean("custom"));
        }
    }
    
    @SuppressWarnings("DuplicatedCode")
    @Override
    protected void initPathfinder() {
        this.goalSelector = new PathfinderGoalSelector(world != null && world.getMethodProfilerSupplier() != null ? world.getMethodProfilerSupplier() : null);
        if (custom) {
            this.goalSelector.a(new PathfinderGoalFloat(this));
            this.lookController = new LookController(this);
        } else {
            super.initPathfinder();
        }
    }    
    
    public void setCustom(boolean value) {
        this.custom = value;
        this.initPathfinder();
        this.persistent = value;
        t(value);
    }
    
    @Override
    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        nbttagcompound.setBoolean("custom", custom);
        return super.save(nbttagcompound);
    }
    
    @Override
    public LootTable getDropTable() {
        LootTable lootTable = new EntityLootTable(EntityType.PIGLIN);
        lootTable.addPossibleLoot(new RealmsLoot(CustomItemRegistry.GOLD_SWORD, Rarity.UNCOMMON));
        lootTable.addPossibleLoot(new RealmsLoot(CustomItemRegistry.CROSS_BOW, Rarity.UNCOMMON));
        lootTable.addPossibleLoot(new RealmsLoot(CustomItemRegistry.ARROW, Rarity.COMMON));
        lootTable.addPossibleLoot(new RealmsLoot(CustomItemRegistry.RAW_PORKCHOP, Rarity.COMMON));
        lootTable.addPossibleLoot(new RealmsLoot(CustomItemRegistry.GOLD, Rarity.COMMON));
        lootTable.addPossibleLoot(new RealmsLoot(CustomItemRegistry.GOLD_HELMET, Rarity.RARE));
        lootTable.addPossibleLoot(new RealmsLoot(CustomItemRegistry.GOLD_CHESTPLATE, Rarity.RARE));
        lootTable.addPossibleLoot(new RealmsLoot(CustomItemRegistry.GOLD_LEGGINGS, Rarity.RARE));
        lootTable.addPossibleLoot(new RealmsLoot(CustomItemRegistry.GOLD_BOOTS, Rarity.RARE));
        return lootTable;
    }
    

    
    @Override
    public boolean isCustom() {
        return this.custom;
    }
    
    
}