package com.kingrealms.realms.items.type;

import com.kingrealms.realms.items.*;
import com.kingrealms.realms.skills.woodcutting.TreeType;
import com.starmediadev.lib.items.NBTWrapper;
import com.starmediadev.lib.util.ID;
import com.starmediadev.lib.util.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ArcaneSapling extends CustomItem {
    
    private TreeType treeType;
    
    public ArcaneSapling(TreeType treeType, Material material) {
        super(new ID(treeType.name().toLowerCase() + "_arcane_sapling"), ChatColor.of("#654321") + "&lArcane Sapling: &f&l" + Utils.capitalizeEveryWord(treeType.name()), "", material, ItemType.WOODCUTTING_RESOURCE_CORE, true);
        this.treeType = treeType;
    }
    
    public static boolean isArcaneSapling(ItemStack itemStack) {
        return getTreeType(itemStack) != null;
    }
    
    public static TreeType getTreeType(ItemStack itemStack) {
        try {
            String treeType = NBTWrapper.getNBTString(itemStack, "treeType");
            return TreeType.valueOf(treeType);
        } catch (Exception e) {
            return null;
        }
    }
    
    @Override
    public ItemStack getItemStack() {
        return this.getItemStack(1);
    }
    
    @Override
    public ItemStack getItemStack(int amount) {
        ItemStack itemStack = super.getItemStack(amount);
        try {
            itemStack = NBTWrapper.addNBTString(itemStack, "treeType", treeType.name());
        } catch (Exception e) {
            return null;
        }
        
        return itemStack;
    }
}