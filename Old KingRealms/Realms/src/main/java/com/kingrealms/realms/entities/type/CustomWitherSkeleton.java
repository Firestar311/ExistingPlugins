package com.kingrealms.realms.entities.type;

import com.kingrealms.realms.entities.controller.LookController;
import com.kingrealms.realms.items.CustomItemRegistry;
import com.kingrealms.realms.loot.*;
import com.kingrealms.realms.loot.LootTable;
import com.kingrealms.realms.util.RealmsLoot;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.minecraft.server.v1_16_R1.*;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

public class CustomWitherSkeleton extends EntitySkeletonWither implements ICustomEntity {
    private boolean custom = false;
    private boolean portalKeeper = false;
    public static Location location;
    
    public CustomWitherSkeleton(World world) {
        super(EntityTypes.WITHER_SKELETON, world);
    }
    
    public boolean isPortalKeeper() {
        return portalKeeper;
    }    
    
    public void setCustom(boolean value) {
        this.custom = value;
        this.initPathfinder();
        this.persistent = value;
        this.collides = !value;
    }
    
    public void setPortalKeeper(boolean portalKeeper) {
        this.portalKeeper = portalKeeper;
        if (this.portalKeeper) {
            setCustom(true);
            setInvulnerable(true);
            setCustomNameVisible(true);
            TextComponent name = new TextComponent("Portal Keeper");
            name.setColor(ChatColor.DARK_RED);
            name.setBold(true);
            String serialized = ComponentSerializer.toString(name);
            setCustomName(IChatBaseComponent.ChatSerializer.a(serialized));
            location = getBukkitEntity().getLocation().clone();
            this.lookController = new LookController(this);
        }
    }
    
    @Override
    protected SoundEffect getSoundAmbient() {
        if (isPortalKeeper()) {
            return null;
        } else {
            return super.getSoundAmbient();
        }
    }
    
    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        if (isPortalKeeper()) {
            return null;
        } else {
            return super.getSoundHurt(damagesource);
        }
    }
    
    @Override
    protected SoundEffect getSoundDeath() {
        if (isPortalKeeper()) {
            return null;
        } else {
            return super.getSoundDeath();
        }
    }
    
    @Override
    protected SoundEffect getSoundSwim() {
        if (isPortalKeeper()) {
            return null;
        } else {
            return super.getSoundSwim();
        }
    }
    
    @Override
    protected SoundEffect getSoundSplash() {
        if (isPortalKeeper()) {
            return null;
        } else {
            return super.getSoundSplash();
        }
    }
    
    @Override
    protected SoundEffect getSoundFall(int var0) {
        if (isPortalKeeper()) {
            return null;
        } else {
            return super.getSoundFall(var0);
        }
    }
    
    @Override
    protected boolean playStepSound() {
        if (isPortalKeeper()) {
            return false;
        } else {
            return super.playStepSound();
        }
    }
    
    @Override
    public LootTable getDropTable() {
        LootTable lootTable = new EntityLootTable(EntityType.WITHER_SKELETON);
        lootTable.addPossibleLoot(new RealmsLoot(CustomItemRegistry.BONE, Rarity.COMMON));
        lootTable.addPossibleLoot(new RealmsLoot(CustomItemRegistry.COAL, Rarity.UNCOMMON));
        lootTable.addPossibleLoot(new RealmsLoot(CustomItemRegistry.STONE_SWORD, Rarity.EPIC));
        lootTable.addPossibleLoot(new RealmsLoot(CustomItemRegistry.WITHER_SKULL, Rarity.LEGENDARY));
        return lootTable;
    }
    
    @Override
    public NBTTagCompound save(NBTTagCompound nbt) {
        nbt.setBoolean("custom", custom);
        nbt.setBoolean("portalkeeper", portalKeeper);
        return super.save(nbt);
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
        }
        
        if (nbttagcompound.hasKey("portalkeeper")) {
            setPortalKeeper(nbttagcompound.getBoolean("portalkeeper"));
        }
    }
}