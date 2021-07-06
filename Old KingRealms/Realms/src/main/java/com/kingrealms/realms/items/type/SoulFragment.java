package com.kingrealms.realms.items.type;

import com.kingrealms.realms.items.ItemType;
import com.starmediadev.lib.items.NBTWrapper;
import com.starmediadev.lib.util.EntityNames;
import com.starmediadev.lib.util.ID;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class SoulFragment extends CraftingPart {
    
    private EntityType entityType;
    
    public SoulFragment(EntityType entityType) {
        super(new ID(entityType.name().toLowerCase() + "_soul_fragment"), "&5&lSoul Fragment: &d" + EntityNames.getName(entityType), null, Material.GHAST_TEAR, ItemType.RESOURCE, true, true);
        this.entityType = entityType;
        addLoreLine("&b&oA mystical aura seems to eminate from this item.");
        setSellMultiplier(0);
    }
    
    public static boolean isFragment(ItemStack itemStack) {
        if (itemStack == null) return false;
        if (itemStack.getType() != Material.BLACK_DYE) return false;
        try {
            String type = NBTWrapper.getNBTString(itemStack, "soultype");
            if (StringUtils.isEmpty(type)) return false;
            
            EntityType.valueOf(type.toUpperCase());
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    
    @Override
    public ItemStack getItemStack() {
        return getItemStack(1);
    }
    
    @Override
    public ItemStack getItemStack(int amount) {
        ItemStack itemStack = super.getItemStack(amount);
        try {
            itemStack = NBTWrapper.addNBTString(itemStack, "soultype", entityType.name());
        } catch (Exception e) {
            return null;
        }
        return itemStack;
    }
}