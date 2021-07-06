package com.kingrealms.realms.skills.farming.blocks;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.profile.RealmProfile;
import com.kingrealms.realms.skills.farming.*;
import com.kingrealms.realms.tasks.BlockUpdate;
import com.kingrealms.realms.util.RealmsUtils;
import com.starmediadev.lib.util.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.*;

@SerializableAs("StaticCropBlock")
public class StaticCropBlock extends CropBlock {
    
    private Location cropLocation; //mysql
    
    public StaticCropBlock(CropType type) {
        super(type);
    }
    
    public StaticCropBlock(CropType type, Location soilLocation, Location cropLocation) {
        super(type, soilLocation);
        this.cropLocation = cropLocation;
    }
    
    public StaticCropBlock(Map<String, Object> serialized) {
        super(serialized);
    }
    
    @Override
    public Map<String, String> getDisplayMap() {
        return new LinkedHashMap<>(super.getDisplayMap()) {{
            put("Crop Location", Utils.locationToString(cropLocation));
        }};
    }
    
    @Override
    public boolean contains(Location location) {
        return super.contains(location) || cropLocation.equals(location);
    }
    
    @Override
    public void grow() {
        if (Realms.getInstance().getServerMode().getMinCropGrow() != 0) {
            FarmingManager.workload.addLoad(new BlockUpdate(this.cropLocation, this.type.getCropMaterial()));
        }
    }
    
    @Override
    public void onPlace() {
        generateNextGrowth();
        this.cropLocation.getBlock().setType(type.getCropMaterial());
    }
    
    @Override
    public void onBreak() {
        this.cropLocation.getBlock().setType(Material.AIR);
    }
    
    @Override
    public void onHarvest(Location location, RealmProfile profile) {
        RealmsUtils.addCropLoot(this, profile);
    }
    
    @Override
    public Map<String, Object> serialize() {
        return new HashMap<>(super.serialize()) {{
            put("cropLocation", cropLocation);
        }};
    }
}