package com.starmediadev.lib.items;

import com.starmediadev.lib.util.ReflectionUtils;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.*;

public final class NBTWrapper {
    
    private static Class<?> NMSItemStack = ReflectionUtils.getNMSClass("ItemStack");
    private static Class<?> craftItemStack = ReflectionUtils.getCraftClass("inventory.CraftItemStack");
    private static Class<?> nbtTagCompound = ReflectionUtils.getNMSClass("NBTTagCompound");
    private static Class<?> nbtBase = ReflectionUtils.getNMSClass("NBTBase");
    private static Class<?> nbtTagString = ReflectionUtils.getNMSClass("NBTTagString");
    
    private static Constructor<?> tagStringConstructor;
    
    private static Method asNMSCopy;
    private static Method getOrCreateTag;
    private static Method setTagCompound;
    private static Method setTagItemStack;
    private static Method asBukkitCopy;
    private static Method getTag;
    private static Method getString;
    private static Method remove;
    private static Method cloneItemStack;
    
    static {
        try {
            tagStringConstructor = nbtTagString.getDeclaredConstructor(String.class);
            tagStringConstructor.setAccessible(true);
        
            asNMSCopy = craftItemStack.getDeclaredMethod("asNMSCopy", ItemStack.class);
            getOrCreateTag = NMSItemStack.getDeclaredMethod("getOrCreateTag");
            setTagCompound = nbtTagCompound.getDeclaredMethod("set", String.class, nbtBase);
            asBukkitCopy = craftItemStack.getDeclaredMethod("asBukkitCopy", NMSItemStack);
            getTag = NMSItemStack.getDeclaredMethod("getTag");
            getString = nbtTagCompound.getDeclaredMethod("getString", String.class);
            remove = nbtTagCompound.getDeclaredMethod("remove", String.class);
            setTagItemStack = NMSItemStack.getDeclaredMethod("setTag", nbtTagCompound);
            cloneItemStack = NMSItemStack.getDeclaredMethod("cloneItemStack");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private NBTWrapper() {
    }
    
    public static ItemStack addNBTString(ItemStack itemStack, String key, String value) throws Exception {
        Object rnmsStack = asNMSCopy.invoke(null, itemStack);
        Object rtagCompound = getOrCreateTag.invoke(rnmsStack);
        setTagCompound.invoke(rtagCompound, key, tagStringConstructor.newInstance(value));
        setTagItemStack.invoke(rnmsStack, rtagCompound);
        return (ItemStack) asBukkitCopy.invoke(null, rnmsStack);
    }
    
    public static String getNBTString(ItemStack itemStack, String key) throws Exception {
        Object rnmsStack = asNMSCopy.invoke(null, itemStack);
        Object rtagCompound = getTag.invoke(rnmsStack);
        
        if (rtagCompound == null) return null;
        return (String) getString.invoke(rtagCompound, key);
    }
    
    public static ItemStack cloneItemStack(ItemStack itemStack) throws InvocationTargetException, IllegalAccessException {
        Object rnmsStack = asNMSCopy.invoke(null, itemStack);
        Object clone = cloneItemStack.invoke(rnmsStack);
        return (ItemStack) asBukkitCopy.invoke(null, clone);
    }
    
    public static ItemStack resetTags(ItemStack itemStack, String key) throws Exception {
        Object rnmsStack = asNMSCopy.invoke(null, itemStack);
        Object itemTag = getTag.invoke(rnmsStack);
        remove.invoke(itemTag, key);
        setTagItemStack.invoke(rnmsStack, itemTag);
        return (ItemStack) asBukkitCopy.invoke(null, rnmsStack);
    }
}