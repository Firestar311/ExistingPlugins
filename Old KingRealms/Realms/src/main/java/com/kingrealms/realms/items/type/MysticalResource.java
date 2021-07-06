package com.kingrealms.realms.items.type;

import com.kingrealms.realms.items.*;
import com.kingrealms.realms.skills.mining.ResourceType;
import com.starmediadev.lib.items.NBTWrapper;
import com.starmediadev.lib.util.ID;
import com.starmediadev.lib.util.Utils;
import org.bukkit.inventory.ItemStack;

public class MysticalResource extends CustomItem {
    
    private ResourceType resourceType;
    
    public MysticalResource(ResourceType resourceType) {
        super(new ID("mystical_" + resourceType.name().toLowerCase().replace("_resource", "")), "&eMystical " + Utils.capitalizeEveryWord(resourceType.name()), "", resourceType.getMaterial(), ItemType.MINING_RESOURCE_CORE, true);
        this.resourceType = resourceType;
        setSellMultiplier(0);
    }
    
    public static MysticalResource getMysticalResource(ItemStack itemStack) {
        if (!isMysticalResource(itemStack)) return null;
        try {
            String id = NBTWrapper.getNBTString(itemStack, "itemid");
            return (MysticalResource) CustomItemRegistry.REGISTRY.get(new ID(id));
        } catch (Exception e) {
            return null;
        }
    }
    
    public static boolean isMysticalResource(ItemStack itemStack) {
        try {
            String id = NBTWrapper.getNBTString(itemStack, "itemid");
            return id.startsWith("mystical_");
        } catch (Exception e) {
            return false;
        }
    }
    
    public ResourceType getResourceType() {
        return resourceType;
    }
}