package net.firecraftmc.api.wrapper;

import net.firecraftmc.api.interfaces.NBTWrapper;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagString;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class NBTWrapper1_8_R3 implements NBTWrapper {
    public void addNBTString(ItemStack stack, String tagName, String value) {
        net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(stack);
        NBTTagCompound tagCompound = nmsStack.hasTag() ? nmsStack.getTag() : new NBTTagCompound();
        NBTTagString nbtTagString = new NBTTagString(value);
        tagCompound.set(tagName, nbtTagString);
        nmsStack.setTag(tagCompound);
        CraftItemStack.asBukkitCopy(nmsStack);
    }

    public String getNBTString(ItemStack stack, String key) {
        net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(stack);
        NBTTagCompound tagCompound = nmsStack.getTag();
        if (tagCompound != null && tagCompound.hasKey(key)) {
            return tagCompound.getString(key);
        }
        return null;
    }
}
