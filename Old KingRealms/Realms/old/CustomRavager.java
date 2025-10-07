package com.kingrealms.realms.entities.type;

import com.kingrealms.realms.entities.controller.LookController;
import com.kingrealms.realms.items.CustomItemRegistry;
import com.kingrealms.realms.loot.*;
import com.kingrealms.realms.loot.LootTable;
import com.kingrealms.realms.util.RealmsLoot;
import net.minecraft.server.v1_16_R1.*;
import org.bukkit.entity.EntityType;

public class CustomRavager extends EntityRavager implements ICustomEntity {
    private boolean custom = false;
    
    public CustomRavager(World world) {
        super(EntityTypes.RAVAGER, world);
    }
    
    public void setCustom(boolean value) {
        this.custom = value;
        this.initPathfinder();
        this.persistent = value;
    }
    
    @Override
    public LootTable getDropTable() {
        LootTable lootTable = new EntityLootTable(EntityType.RAVAGER);
        lootTable.addPossibleLoot(new RealmsLoot(CustomItemRegistry.SADDLE, Rarity.RARE));
        lootTable.addPossibleLoot(new RealmsLoot(CustomItemRegistry.RAW_BEEF, Rarity.COMMON));
        return lootTable;
    }
    
    @Override
    public boolean isCustom() {
        return this.custom;
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
    
    @Override
    public void loadData(NBTTagCompound nbttagcompound) {
        super.loadData(nbttagcompound);
        if (nbttagcompound.hasKey("custom")) {
            setCustom(nbttagcompound.getBoolean("custom"));
        }
    }
    
    @Override
    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        nbttagcompound.setBoolean("custom", custom);
        return super.save(nbttagcompound);
    }
}