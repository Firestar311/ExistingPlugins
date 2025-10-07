package com.kingrealms.realms.entities.type;

import com.kingrealms.realms.items.CustomItemRegistry;
import com.kingrealms.realms.loot.*;
import com.kingrealms.realms.loot.LootTable;
import com.kingrealms.realms.util.RealmsLoot;
import net.minecraft.server.v1_16_R1.*;
import org.bukkit.entity.EntityType;

public class CustomCow extends EntityCow implements ICustomEntity {
    private boolean custom = false;
    
    public CustomCow(World world) {
        super(EntityTypes.COW, world);
    }
    
    public void setCustom(boolean value) {
        this.custom = value;
        this.initPathfinder();
        this.persistent = value;
    }
    
    @Override
    public LootTable getDropTable() {
        LootTable lootTable = new EntityLootTable(EntityType.COW);
        lootTable.addPossibleLoot(new RealmsLoot(CustomItemRegistry.RAW_BEEF, Rarity.COMMON));
        lootTable.addPossibleLoot(new RealmsLoot(CustomItemRegistry.LEATHER, Rarity.COMMON));
        lootTable.addPossibleLoot(new RealmsLoot(CustomItemRegistry.WHITE_DYE, Rarity.UNCOMMON));
        lootTable.addPossibleLoot(new RealmsLoot(CustomItemRegistry.BROWN_DYE, Rarity.UNCOMMON));
        lootTable.addPossibleLoot(new RealmsLoot(CustomItemRegistry.MILK_BUCKET, Rarity.EPIC));
        return lootTable;
    }
    
    @Override
    public boolean isCustom() {
        return this.custom;
    }
    
    @Override
    protected void initPathfinder() {
        this.goalSelector = new PathfinderGoalSelector(world != null && world.getMethodProfilerSupplier() != null ? world.getMethodProfilerSupplier() : null);
        if (custom) {
            this.goalSelector.a(new PathfinderGoalFloat(this));
        } else {
            super.initPathfinder();
        }
    }
    
    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        if (nbttagcompound.hasKey("custom")) {
            this.custom = nbttagcompound.getBoolean("custom");
            this.initPathfinder();
        }
    }
    
    @Override
    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        nbttagcompound.setBoolean("custom", custom);
        return super.save(nbttagcompound);
    }
}