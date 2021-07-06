package com.kingrealms.realms.skills.farming.blocks;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.profile.RealmProfile;
import com.kingrealms.realms.skills.farming.*;
import com.kingrealms.realms.util.RealmsUtils;
import com.starmediadev.lib.util.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Farmland;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

@SerializableAs("AgeableCropBlock")
public class AgeableCropBlock extends CropBlock {
    private Location cropLocation; //mysql
    
    public AgeableCropBlock(CropType type) {
        super(type);
    }
    
    public AgeableCropBlock(CropType type, Location soilLocation, Location cropLocation) {
        super(type, soilLocation);
        this.cropLocation = cropLocation;
    }
    
    public AgeableCropBlock(Map<String, Object> serialized) {
        super(serialized);
        this.cropLocation = (Location) serialized.get("cropLocation");
    }
    
    @Override
    public boolean contains(Location location) {
        return super.contains(location) || location.equals(cropLocation);
    }
    
    @Override
    public Map<String, String> getDisplayMap() {
        return new LinkedHashMap<>(super.getDisplayMap()) {{
            put("Crop Location", Utils.locationToString(cropLocation));
        }};
    }
    
    @Override
    public void grow() {
        if (Realms.getInstance().getServerMode().getMinCropGrow() != 0) {
            FarmingManager.workload.addLoad(() -> {
                updateSoil();
                updateCrop();
                BlockState state = cropLocation.getBlock().getState();
                BlockData blockData = state.getBlockData();
                if (System.currentTimeMillis() > getNextGrowth()) {
                    if (blockData instanceof Ageable) {
                        Ageable ageable = (Ageable) blockData;
                        if (ageable.getMaximumAge() > ageable.getAge()) {
                            ageable.setAge(ageable.getAge() + 1);
                            state.setBlockData(ageable);
                            state.update();
                        } else {
                            if (cropLocation.getBlock().getType() != getType().getCropMaterial()) {
                                cropLocation.getBlock().setType(getType().getCropMaterial());
                            }
                        }
                
                        generateNextGrowth();
                        setLastGrowth(System.currentTimeMillis());
                    }
                }
            });
        }
    }
    
    @Override
    public void onPlace() {
        generateNextGrowth();
        if (this.cropLocation.getBlock().getLightLevel() > 8) {
            this.cropLocation.getBlock().setType(this.getType().getCropMaterial());
        }
        updateSoil();
    }
    
    private void updateSoil() {
        Block block = this.soilLocation.getBlock();
        if (!block.getType().equals(type.getSoil())) {
            block.setType(type.getSoil());
        }
        Farmland farmland = (Farmland) block.getBlockData();
        farmland.setMoisture(farmland.getMaximumMoisture());
        BlockState state = block.getState();
        state.setBlockData(farmland);
        state.update();
    }
    
    private void updateCrop() {
        Block block = this.cropLocation.getBlock();
        if (!block.getType().equals(this.type.getCropMaterial())) {
            if (this.cropLocation.getBlock().getLightLevel() > 8) {
                this.cropLocation.getBlock().setType(this.getType().getCropMaterial());
            }
        }
    }
    
    @Override
    public void onBreak() {
        this.cropLocation.getBlock().setType(Material.AIR);
    }
    
    @Override
    public void onHarvest(Location location, RealmProfile profile) {
        updateSoil();
        new BukkitRunnable() {
            public void run() {
                cropLocation.getBlock().setType(getType().getCropMaterial());
            }
        }.runTaskLater(Realms.getInstance(), 1L);
        if (location.getBlock().getBlockData() instanceof Ageable) {
            Ageable ageable = (Ageable) location.getBlock().getBlockData();
            if (ageable.getAge() == ageable.getMaximumAge()) {
                RealmsUtils.addCropLoot(this, profile);
            }
        }
    }
    
    @Override
    public boolean canHarvest(Location location, RealmProfile profile) {
        Block block = this.cropLocation.getBlock();
        Ageable ageable = ((Ageable) block.getBlockData());
        if (ageable.getAge() == ageable.getMaximumAge()) {
            return super.canHarvest(location, profile);
        }
        return false;
    }
    
    @Override
    public Map<String, Object> serialize() {
        return new HashMap<>(super.serialize()) {{
            put("cropLocation", cropLocation);
        }};
    }
}