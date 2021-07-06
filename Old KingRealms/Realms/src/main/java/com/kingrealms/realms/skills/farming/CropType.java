package com.kingrealms.realms.skills.farming;

import com.kingrealms.realms.skills.farming.blocks.*;
import org.bukkit.Location;
import org.bukkit.Material;

import static org.bukkit.Material.*;

public enum CropType {
    WHEAT(FARMLAND, Material.WHEAT, Material.WHEAT) {
        public CropBlock createInstance(Location location) {
            return new AgeableCropBlock(WHEAT, location, location.clone().add(0, 1, 0));
        }
        public boolean requiresLight() {
            return true;
        }
    }, CARROT(FARMLAND, CARROTS, Material.CARROT) {
        public CropBlock createInstance(Location location) {
            return new AgeableCropBlock(CARROT, location, location.clone().add(0, 1, 0));
        }
        public boolean requiresLight() {
            return true;
        }
    }, POTATO(FARMLAND, POTATOES, Material.POTATO) {
        public CropBlock createInstance(Location location) {
            return new AgeableCropBlock(POTATO, location, location.clone().add(0, 1, 0));
        }
        public boolean requiresLight() {
            return true;
        }
    }, CACTUS(SAND, Material.CACTUS, Material.CACTUS) {
        public CropBlock createInstance(Location location) {
            return new StackableCropBlock(CACTUS, location);
        }
    }, BEETROOT(FARMLAND, BEETROOTS, Material.BEETROOT) {
        public CropBlock createInstance(Location location) {
            return new AgeableCropBlock(BEETROOT, location, location.clone().add(0, 1, 0));
        }
        public boolean requiresLight() {
            return true;
        }
    }, PUMPKIN(FARMLAND, Material.PUMPKIN, Material.PUMPKIN) {
        public CropBlock createInstance(Location location) {
            return new StaticCropBlock(PUMPKIN, location, location.clone().add(0, 1, 0));
        }
    }, MELON(FARMLAND, Material.MELON, Material.MELON) {
        public CropBlock createInstance(Location location) {
            return new StaticCropBlock(MELON, location, location.clone().add(0, 1, 0));
        }
    }, KELP(STONE, KELP_PLANT, Material.KELP) {
        public CropBlock createInstance(Location location) {
            return new StackableCropBlock(KELP, location);
        }
    }, BAMBOO(DIRT, Material.BAMBOO, Material.BAMBOO) {
        public CropBlock createInstance(Location location) {
            return new StackableCropBlock(BAMBOO, location);
        }
    }, COCO_BEANS(JUNGLE_LOG, COCOA, COCOA_BEANS) {
        public CropBlock createInstance(Location location) {
            return new AgeableCropBlock(COCO_BEANS, location, location.clone().add(0, 1, 0));
        }
    }, RED_MUSHROOM(MYCELIUM, Material.RED_MUSHROOM, Material.RED_MUSHROOM) {
        public CropBlock createInstance(Location location) {
            return new StaticCropBlock(RED_MUSHROOM, location, location.clone().add(0, 1, 0));
        }
    }, BROWN_MUSHROOM(MYCELIUM, Material.BROWN_MUSHROOM, Material.BROWN_MUSHROOM) {
        public CropBlock createInstance(Location location) {
            return new StaticCropBlock(BROWN_MUSHROOM, location, location.clone().add(0, 1, 0));
        }
    }, NETHER_WART(SOUL_SAND, Material.NETHER_WART, Material.NETHER_WART) {
        public CropBlock createInstance(Location location) {
            return new AgeableCropBlock(NETHER_WART, location, location.clone().add(0, 1, 0));
        }
    }, CHORUS(END_STONE, CHORUS_FLOWER, CHORUS_FRUIT) {
        public CropBlock createInstance(Location location) {
            return new StaticCropBlock(CHORUS, location, location.clone().add(0, 1, 0));
        }
    }, SUGAR_CANE(SAND, Material.SUGAR_CANE, Material.SUGAR_CANE) {
        public CropBlock createInstance(Location location) {
            return new StackableCropBlock(SUGAR_CANE, location);
        }
    };
    
    private Material cropMaterial;
    private Material iconMaterial;
    private Material soil;
    
    CropType(Material soil, Material cropMaterial, Material iconMaterial) {
        this.soil = soil;
        this.cropMaterial = cropMaterial;
        this.iconMaterial = iconMaterial;
    }
    
    public abstract CropBlock createInstance(Location location);
    
    public boolean requiresLight() {
        return false;
    }
    
    public Material getSoil() {
        return soil;
    }
    
    public Material getCropMaterial() {
        return cropMaterial;
    }
    
    public Material getIconMaterial() {
        return iconMaterial;
    }
}