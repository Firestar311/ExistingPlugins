package com.kingrealms.realms.items.type;

import com.kingrealms.realms.items.ItemType;
import com.kingrealms.realms.skills.woodcutting.TreeType;
import com.kingrealms.realms.util.Constants;
import com.starmediadev.lib.items.NBTWrapper;
import com.starmediadev.lib.util.ID;
import com.starmediadev.lib.util.Utils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class WoodChips extends CraftingPart {
    
    private TreeType treeType;
    
    public WoodChips(TreeType treeType) {
        super(new ID(treeType.name().toLowerCase() + "_wood_chips"), "&6&lWood Chips: " + Constants.PLAYER_VARIABLE_COLOR + Utils.capitalizeEveryWord(treeType.name()), null, Material.BEETROOT_SEEDS, ItemType.WOODCUTTING_RESOURCE_PART, true, true);
        this.treeType = treeType;
        addLoreLine(Constants.PLAYER_BASE_COLOR + "&lCraft 8 Chips &fto create an Arcane Sapling");
        setSellMultiplier(0);
    }
    
    public static boolean isChip(ItemStack itemStack) {
        if (itemStack == null) return false;
        if (itemStack.getType() != Material.BROWN_DYE) return false;
        try {
            String type = NBTWrapper.getNBTString(itemStack, "chiptype");
            if (StringUtils.isEmpty(type)) return false;
    
            TreeType.valueOf(type.toUpperCase());
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    
    public TreeType getTreeType() {
        return treeType;
    }
    
    @Override
    public ItemStack getItemStack() {
        ItemStack itemStack = super.getItemStack();
        try {
            itemStack = NBTWrapper.addNBTString(itemStack, "chiptype", treeType.name());
        } catch (Exception e) {
            return null;
        }
        return itemStack;
    }
    
    @Override
    public ItemStack getItemStack(int amount) {
        ItemStack itemStack = getItemStack();
        itemStack.setAmount(amount);
        return itemStack;
    }
}