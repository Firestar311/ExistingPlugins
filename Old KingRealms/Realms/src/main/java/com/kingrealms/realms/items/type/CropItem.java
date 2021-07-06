package com.kingrealms.realms.items.type;

import com.kingrealms.realms.items.*;
import com.kingrealms.realms.skills.farming.CropType;
import com.kingrealms.realms.util.Constants;
import com.starmediadev.lib.items.NBTWrapper;
import com.starmediadev.lib.util.ID;
import com.starmediadev.lib.util.Utils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.inventory.ItemStack;

public class CropItem extends CustomItem {
    
    private CropType cropType;
    private CustomItem dropItem;
    
    public CropItem(CropType type, CustomItem dropItem) {
        this.id = new ID(type.name().toLowerCase() + "_crop_item");
        this.cropType = type;
        this.displayName = "&6&lCrop Block: " + Constants.PLAYER_VARIABLE_COLOR + Utils.capitalizeEveryWord(type.name());
        this.material = type.getSoil();
        this.type = ItemType.FARMING_RESOURCE_CORE;
        this.dropItem = dropItem;
        CustomItemRegistry.REGISTRY.put(this.id, this);
        setSellMultiplier(0);
    }
    
    public static boolean isCropItem(ItemStack itemStack) {
        return getCropItem(itemStack) != null;
    }
    
    @Override
    public CropItem addLoreLine(String line) {
        this.lore.add(line);
        return this;
    }
    
    public static CropItem getCropItem(ItemStack itemStack) {
        try {
            String type = NBTWrapper.getNBTString(itemStack, "croptype");
            if (StringUtils.isEmpty(type)) return null;
            
            CropType cropType = CropType.valueOf(type.toUpperCase());
            if (cropType == null) return null;
    
            return CustomItemRegistry.CROP_ITEMS.getItem(cropType);
        } catch (Exception e) {
            return null;
        }
    }
    
    @Override
    public ItemStack getItemStack() {
        return getItemStack(1);
    }
    
    @Override
    public ItemStack getItemStack(int amount) {
        ItemStack itemStack = super.getItemStack(amount);
        try {
            itemStack = NBTWrapper.addNBTString(itemStack, "croptype", cropType.name());
        } catch (Exception e) {
            return null;
        }
        return itemStack;
    }
    
    public CropType getCropType() {
        return cropType;
    }
    
    public CustomItem getDropItem() {
        return dropItem;
    }
}