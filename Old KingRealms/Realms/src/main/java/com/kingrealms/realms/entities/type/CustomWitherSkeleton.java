package com.kingrealms.realms.entities.type;

import com.kingrealms.realms.entities.controller.LookController;
import com.kingrealms.realms.items.CustomItemRegistry;
import com.kingrealms.realms.loot.*;
import com.kingrealms.realms.util.RealmsLoot;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

public class CustomWitherSkeleton extends WitherSkeleton implements ICustomEntity {
    private boolean custom = false;
    private boolean portalKeeper = false;
    public static Location location;
    
    public CustomWitherSkeleton(Level world) {
        super(net.minecraft.world.entity.EntityType.WITHER_SKELETON, world);
    }
    
    public boolean isPortalKeeper() {
        return portalKeeper;
    }
    
    public void setCustom(boolean value) {
        this.custom = value;
//        this.persistent = value;
        this.collides = !value;
    }
    
    public void setPortalKeeper(boolean portalKeeper) {
        this.portalKeeper = portalKeeper;
        if (this.portalKeeper) {
            setCustom(true);
            setInvulnerable(true);
            setCustomNameVisible(true);
            setCustomName(Component.literal("Portal Keeper").withStyle(style -> style.withColor(ChatFormatting.DARK_RED).withBold(true)));
            location = getBukkitEntity().getLocation().clone();
            this.lookControl = new LookController(this);
        }
    }
    
    @Override
    protected SoundEvent getAmbientSound() {
        if (isPortalKeeper()) {
            return null;
        } else {
            return super.getAmbientSound();
        }
    }
    
    @Override
    protected SoundEvent getHurtSound(DamageSource damagesource) {
        if (isPortalKeeper()) {
            return null;
        } else {
            return super.getHurtSound(damagesource);
        }
    }
    
    @Override
    protected SoundEvent getDeathSound() {
        if (isPortalKeeper()) {
            return null;
        } else {
            return super.getDeathSound();
        }
    }
    
    @Override
    protected SoundEvent getSwimSound() {
        if (isPortalKeeper()) {
            return null;
        } else {
            return super.getSwimSound();
        }
    }
    
    @Override
    protected SoundEvent getSwimSplashSound() {
        if (isPortalKeeper()) {
            return null;
        } else {
            return super.getSwimSplashSound();
        }
    }
    
    @Override
    public Fallsounds getFallSounds() {
        if (isPortalKeeper()) {
            return null;
        } else {
            return super.getFallSounds();
        }
    }
    
    @Override
    protected void playStepSound(BlockPos blockposition, BlockState iblockdata) {
        if (!isPortalKeeper()) {
            super.playStepSound(blockposition, iblockdata);
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
    public boolean save(ValueOutput valueoutput) {
        valueoutput.putBoolean("custom", custom);
        valueoutput.putBoolean("portalkeeper", portalKeeper);
        return super.save(valueoutput);
    }
    
    @Override
    public boolean isCustom() {
        return this.custom;
    }
    
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
        super.load(valueinput);
        this.custom = valueinput.getBooleanOr("custom", false);
        setPortalKeeper(valueinput.getBooleanOr("portalkeeper", false));
    }
}