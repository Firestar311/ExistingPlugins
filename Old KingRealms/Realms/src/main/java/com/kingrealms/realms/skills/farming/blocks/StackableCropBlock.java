package com.kingrealms.realms.skills.farming.blocks;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.profile.RealmProfile;
import com.kingrealms.realms.skills.farming.*;
import com.kingrealms.realms.util.RealmsUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.*;

@SerializableAs("StackableCropBlock")
public class StackableCropBlock extends CropBlock {
    
    private Location[] cropLocations = new Location[6]; //mysql
    
    public StackableCropBlock(CropType type) {
        super(type);
    }
    
    public StackableCropBlock(CropType type, Location soilLocation) {
        super(type, soilLocation);
    }
    
    public StackableCropBlock(Map<String, Object> serialized) {
        super(serialized);
    }
    
    @Override
    public Map<String, String> getDisplayMap() {
        return new LinkedHashMap<>(super.getDisplayMap()) {{
            int count = 0;
            for (Location location : cropLocations) {
                if (location != null) {
                    count++;
                }
            }
            
            put("Total Stack Count", count + "");
        }};
    }
    
    @Override
    public boolean contains(Location location) {
        boolean superContains = super.contains(location);
        if (!superContains) {
            for (Location loc : cropLocations) {
                if (loc != null) {
                    if (loc.equals(location)) {
                        return true;
                    }
                }
            }
        }
        return superContains;
    }
    
    @Override
    public void grow() {
        if (Realms.getInstance().getServerMode().getMinCropGrow() != 0) {
            FarmingManager.workload.addLoad(() -> {
                int lastGrowIndex = -1;
                for (int i = 0; i < cropLocations.length; i++) {
                    if (cropLocations[i] == null || cropLocations[i].getBlock().getType() != type.getCropMaterial()) {
                        cropLocations[i] = null;
                        continue;
                    }
                    if (i == 0) {
                        if (cropLocations[i + 1] == null) {
                            lastGrowIndex = i;
                        }
                    } else {
                        try {
                            if (cropLocations[i] != null && cropLocations[i + 1] == null) {
                                lastGrowIndex = i;
                            }
                        } catch (IndexOutOfBoundsException e) {
                            break;
                        }
                    }
                }
        
        
                if (lastGrowIndex == cropLocations.length) return;
                Location cropLocation;
                if (lastGrowIndex == -1) {
                    cropLocation = soilLocation.clone().add(0, 1, 0);
                } else {
                    cropLocation = cropLocations[lastGrowIndex].clone().add(0, 1, 0);
                }
                cropLocation.getBlock().setType(type.getCropMaterial());
                cropLocations[lastGrowIndex + 1] = cropLocation;
            });
        }
    }
    
    @Override
    public void onPlace() {
        Location cropLocation = this.soilLocation.clone().add(0, 1, 0);
        cropLocation.getBlock().setType(type.getCropMaterial());
        cropLocations[0] = cropLocation;
    }
    
    @Override
    public void onBreak() {
        for (Location location : cropLocations) {
            if (location != null) {
                location.getBlock().setType(Material.AIR);
            }
        }
    }
    
    @Override
    public void onHarvest(Location location, RealmProfile profile) {
        int harvestIndex = -1;
        for (int i = 0; i < cropLocations.length; i++) {
            Location loc = cropLocations[i];
            if (loc != null) {
                if (loc.equals(location)) {
                    harvestIndex = i;
                }
            }
        }
    
        if (harvestIndex == -1) { return; }
        for (int i = harvestIndex; i < cropLocations.length; i++) {
            Location loc = cropLocations[i];
            if (loc != null) {
                if (loc.getBlock().getType().equals(type.getCropMaterial())) {
                    loc.getBlock().setType(Material.AIR, false);
                    RealmsUtils.addCropLoot(this, profile);
                }
            }
        }
    }
    
    @Override
    public boolean canHarvest(Location location, RealmProfile profile) {
        if (!checkLevel(profile)) { return false; }
        for (Location loc : this.cropLocations) {
            if (loc != null) {
                if (loc.equals(location)) {
                    return checkClaim(location, profile, false);
                }
            }
        }
        return false;
    }
    
    @Override
    public Map<String, Object> serialize() {
        return new HashMap<>(super.serialize()) {{
            for (int i = 0; i < cropLocations.length; i++) {
                Location location = cropLocations[i];
                if (location != null) {
                    put("loc" + i, location);
                }
            }
        }};
    }
}