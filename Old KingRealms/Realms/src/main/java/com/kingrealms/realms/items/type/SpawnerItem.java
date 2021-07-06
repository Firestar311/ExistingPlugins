package com.kingrealms.realms.items.type;

import com.kingrealms.realms.items.*;
import com.kingrealms.realms.util.Constants;
import com.starmediadev.lib.items.NBTWrapper;
import com.starmediadev.lib.util.*;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class SpawnerItem extends CustomItem {
    
    private EntityType entityType;
    
    public SpawnerItem(EntityType entityType) {
        super(new ID(entityType.name().toLowerCase() + "_spawner"), "&d&lMob Spawner: " + Constants.PLAYER_BASE_COLOR + "&l" + EntityNames.getName(entityType), "", Material.SPAWNER, ItemType.SLAYER_RESOURCE_CORE, false, true);
        this.entityType = entityType;
        setSellMultiplier(0);
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
    
    public ItemStack getItemStack(Material spawnBlock, int amount) {
        ItemStack itemStack = this.getItemStack(amount);
        if (spawnBlock != null) {
            try {
                itemStack = NBTWrapper.addNBTString(itemStack, "spawnblock", spawnBlock.name());
            } catch (Exception e) {
                return null;
            }
            ItemMeta meta = itemStack.getItemMeta();
            List<String> lore = meta.getLore();
            lore.add("");
            lore.add("&bSpawn Block: &f" + MaterialNames.getName(spawnBlock));
            itemStack.setItemMeta(meta);
        }
        
        return itemStack;
    }
}