package com.kingrealms.realms.items.type;

import com.starmediadev.lib.builder.ItemBuilder;
import com.starmediadev.lib.util.ID;
import com.starmediadev.lib.util.Utils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class RandomRawFish extends Resource {
    
    private static final Set<Material> FISH_MATERIALS = Set.of(Material.TROPICAL_FISH, Material.COD, Material.PUFFERFISH, Material.SALMON);
    
    public RandomRawFish() {
        super();
    }
    
    @Override
    public ItemStack getItemStack(int amount) {
        List<Material> materials = new ArrayList<>(FISH_MATERIALS);
        Material material = materials.get(new Random().nextInt(materials.size()));
        List<String> newLore = new ArrayList<>(List.of("&7&o" + Utils.capitalizeEveryWord(type.name())));
        if (!lore.isEmpty()) {
            newLore.add("");
            newLore.addAll(this.lore);
        }
        return ItemBuilder.start(material).withName("&e" + displayName).withLore(newLore).setGlowing(glowing).addNBTString("itemid", new ID(material.name().toLowerCase()).toString()).buildItem();
    }
    
    @Override
    public ItemStack getItemStack() {
        return this.getItemStack(1);
    }
}