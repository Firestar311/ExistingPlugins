package com.kingrealms.realms.entities.type;

import com.kingrealms.realms.items.CustomItemRegistry;
import com.kingrealms.realms.loot.*;
import com.kingrealms.realms.loot.LootTable;
import com.kingrealms.realms.util.RealmsLoot;
import net.minecraft.server.v1_16_R1.*;
import org.bukkit.entity.EntityType;

public class CustomSheep extends EntitySheep implements ICustomEntity {
    private boolean custom = false;
    
    public CustomSheep(World world) {
        super(EntityTypes.SHEEP, world);
    }
    
    public void setCustom(boolean value) {
        this.custom = value;
        this.initPathfinder();
        this.persistent = value;
    }
    
    @Override
    public LootTable getDropTable() {
        LootTable lootTable = new EntityLootTable(EntityType.SHEEP);
        lootTable.addPossibleLoot(new RealmsLoot(CustomItemRegistry.RAW_MUTTON, Rarity.COMMON));
        lootTable.addPossibleLoot(new RealmsLoot(CustomItemRegistry.WHITE_WOOL, Rarity.COMMON));
        lootTable.addPossibleLoot(new RealmsLoot(CustomItemRegistry.WHITE_DYE, Rarity.UNCOMMON));
        lootTable.addPossibleLoot(new RealmsLoot(CustomItemRegistry.GRAY_DYE, Rarity.UNCOMMON));
        lootTable.addPossibleLoot(new RealmsLoot(CustomItemRegistry.LIGHT_GRAY_DYE, Rarity.UNCOMMON));
        lootTable.addPossibleLoot(new RealmsLoot(CustomItemRegistry.BLACK_DYE, Rarity.UNCOMMON));
        lootTable.addPossibleLoot(new RealmsLoot(CustomItemRegistry.SPAWNER_SHARDS.getItem(EntityType.RABBIT), Rarity.LEGENDARY));
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