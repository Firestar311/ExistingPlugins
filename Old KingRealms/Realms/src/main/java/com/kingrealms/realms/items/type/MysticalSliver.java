package com.kingrealms.realms.items.type;

import com.kingrealms.realms.items.ItemType;
import com.kingrealms.realms.loot.*;
import com.kingrealms.realms.skills.mining.ResourceType;
import com.kingrealms.realms.util.Constants;
import com.starmediadev.lib.items.NBTWrapper;
import com.starmediadev.lib.util.ID;
import com.starmediadev.lib.util.Utils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class MysticalSliver extends CraftingPart {
    
    private ResourceType resourceType;
    
    private LootTable lootTable = new LootTable("sliver_table", 1, 1);
    
    public MysticalSliver(ResourceType resourceType) {
        super(new ID("mystical_" + resourceType.name().toLowerCase() + "_Sliver"), "&6&lMystical Sliver: " + Constants.PLAYER_VARIABLE_COLOR + Utils.capitalizeEveryWord(resourceType.name()), null, Material.WHITE_DYE, ItemType.MINING_RESOURCE_PART, true, true);
        addLoreLine(Constants.PLAYER_BASE_COLOR + "&lCraft 8 Slivers &fto create a Mystical Resource");
        this.resourceType = resourceType;
        setSellMultiplier(0);
        lootTable.addPossibleLoot(new Loot(getItemStack(), Rarity.EPIC));
    }
    
    public static boolean isSliver(ItemStack itemStack) {
        if (itemStack == null) return false;
        if (itemStack.getType() != Material.WHITE_DYE) return false;
        try {
            String type = NBTWrapper.getNBTString(itemStack, "resourcetype");
            if (StringUtils.isEmpty(type)) return false;
            
            ResourceType.valueOf(type);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    
    public LootTable getLootTable() {
        return lootTable;
    }
    
    public Material getMaterial() {
        return material;
    }
    
    @Override
    public ItemStack getItemStack() {
        return this.getItemStack(1);
    }
    
    @Override
    public ItemStack getItemStack(int amount) {
        ItemStack itemStack = super.getItemStack(amount);
        try {
            itemStack = NBTWrapper.addNBTString(itemStack, "resourcetype", resourceType.name());
        } catch (Exception e) {
            return null;
        }
        return itemStack;
    }
    
    public ResourceType getResourceType() {
        return resourceType;
    }
}