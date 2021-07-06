package com.kingrealms.realms.items.type;

import com.kingrealms.realms.items.*;
import com.kingrealms.realms.skills.farming.CropType;
import com.kingrealms.realms.util.Constants;
import com.starmediadev.lib.items.NBTWrapper;
import com.starmediadev.lib.util.*;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class CropScrap extends CraftingPart {
    
    private CropType cropType;
    
    public CropScrap(CropType cropType) {
        super(new ID(cropType.name().toLowerCase() + "_crop_scrap"), "&6&lCrop Scrap: " + Constants.PLAYER_VARIABLE_COLOR + Utils.capitalizeEveryWord(cropType.name()), null, Material.BROWN_DYE, ItemType.FARMING_RESOURCE_PART, true, true);
        this.cropType = cropType;
        addLoreLine(Constants.PLAYER_BASE_COLOR + "&lCraft 8 Scraps &fto create a Crop Block");
        setSellMultiplier(0);
    }
    
    public static boolean isScrap(ItemStack itemStack) {
        if (itemStack == null) return false;
        if (itemStack.getType() != Material.BROWN_DYE) return false;
        try {
            String type = NBTWrapper.getNBTString(itemStack, "scraptype");
            if (StringUtils.isEmpty(type)) return false;
            
            EntityType.valueOf(type.toUpperCase());
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    
    public CropType getCropType() {
        return cropType;
    }
    
    @Override
    public ItemStack getItemStack() {
        return getItemStack(1);
    }
    
    @Override
    public ItemStack getItemStack(int amount) {
        ItemStack itemStack = super.getItemStack(amount);
        try {
            itemStack = NBTWrapper.addNBTString(itemStack, "scraptype", cropType.name());
        } catch (Exception e) {
            return null;
        }
        return itemStack;
    }
}