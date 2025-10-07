package com.kingrealms.realms.entities.type;

import com.kingrealms.realms.entities.controller.LookController;
import com.kingrealms.realms.items.CustomItemRegistry;
import com.kingrealms.realms.loot.*;
import com.kingrealms.realms.loot.LootTable;
import com.kingrealms.realms.util.RealmsLoot;
import net.minecraft.server.v1_16_R1.*;
import org.bukkit.entity.EntityType;

public class CustomHoglin extends EntityHoglin implements ICustomEntity {
    private boolean custom = false;
    
    public CustomHoglin(World world) {
        super(EntityTypes.HOGLIN, world);
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
        LootTable lootTable = new EntityLootTable(EntityType.HOGLIN);
        lootTable.addPossibleLoot(new RealmsLoot(CustomItemRegistry.RAW_PORKCHOP, Rarity.COMMON));
        lootTable.addPossibleLoot(new RealmsLoot(CustomItemRegistry.LEATHER, Rarity.UNCOMMON));
        return lootTable;
    }
    

    
    @Override
    public boolean isCustom() {
        return this.custom;
    }
    
    
}