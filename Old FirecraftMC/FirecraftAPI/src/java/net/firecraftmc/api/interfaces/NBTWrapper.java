package net.firecraftmc.api.interfaces;

import org.bukkit.inventory.ItemStack;

public interface NBTWrapper {

    void addNBTString(ItemStack stack, String tagName, String value);
    String getNBTString(ItemStack stack, String key);
}