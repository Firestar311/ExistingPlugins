package com.firestar311.lib.items;

import com.firestar311.lib.util.ReflectionUtils;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class NBTWrapper {
    
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
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    
    public static ItemStack resetTags(ItemStack itemStack, String key) throws Exception {
        Object rnmsStack = asNMSCopy.invoke(null, itemStack);
        Object itemTag = getTag.invoke(rnmsStack);
        remove.invoke(itemTag, key);
        setTagItemStack.invoke(rnmsStack, itemTag);
        return (ItemStack) asBukkitCopy.invoke(null, rnmsStack);
    }
}