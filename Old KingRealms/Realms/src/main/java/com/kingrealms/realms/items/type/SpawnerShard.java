package com.kingrealms.realms.items.type;

import com.kingrealms.realms.items.*;
import com.kingrealms.realms.util.Constants;
import com.starmediadev.lib.items.NBTWrapper;
import com.starmediadev.lib.util.EntityNames;
import com.starmediadev.lib.util.ID;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class SpawnerShard extends CraftingPart {
    
    private EntityType entityType;
    
    public SpawnerShard(EntityType entityType) {
        super(new ID(entityType.name().toLowerCase() + "_shard"), "&6&lSpawner Shard: " + Constants.PLAYER_VARIABLE_COLOR + EntityNames.getName(entityType), null, Material.BLACK_DYE, ItemType.SLAYER_RESOURCE_PART, true, true);
        this.entityType = entityType;
        addLoreLine(Constants.PLAYER_BASE_COLOR + "&lCraft 8 Shards &fto create a full spawner");
        setSellMultiplier(0);
    }
    
    public static boolean isShard(ItemStack itemStack) {
        if (itemStack == null) return false;
        if (itemStack.getType() != Material.BLACK_DYE) return false;
        try {
            String type = NBTWrapper.getNBTString(itemStack, "spawnertype");
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
            itemStack = NBTWrapper.addNBTString(itemStack, "spawnertype", entityType.name());
        } catch (Exception e) {
            return null;
        }
        return itemStack;
    }
}