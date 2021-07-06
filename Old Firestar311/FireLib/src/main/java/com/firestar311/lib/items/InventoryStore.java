package com.firestar311.lib.items;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.*;
import java.util.*;

public class InventoryStore {
    
    //Save list of items into a NBTTag of a given item
    public static ItemStack saveItemsInNBT(ItemStack item, ItemStack[] items) throws Exception {
        String dataString = itemsToString(items);
        NBTWrapper.addNBTString(item, "invdata", dataString);
        return item;
    }
    
    //Load list of items from the NBTTag of the item
    public static ItemStack[] getItemsFromNBT(ItemStack item) throws Exception {
        String itemString = NBTWrapper.getNBTString(item, "invdata");
        
        if (!itemString.equals("")) {
            return stringToItems(itemString);
        }
        return null;
    }
    
    //Convert list of items into string
    public static String itemsToString(ItemStack[] items) {
        try {
            Map<String, Object>[] serializedItemStacks = serializeItemStack(items);
            if (serializedItemStacks == null) return "empty";
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(serializeItemStack(items));
            oos.flush();
            return Base64.getEncoder().encodeToString(bos.toByteArray());
        } catch (Exception e) {
            //Logger.exception(e);
        }
        return "";
    }
    
    //Convert string to list of items
    public static ItemStack[] stringToItems(String s) {
        if (!s.equalsIgnoreCase("empty")) {
            try {
                ByteArrayInputStream bis = new ByteArrayInputStream(Base64.getDecoder().decode(s));
                ObjectInputStream ois = new ObjectInputStream(bis);
                return deserializeItemStack((Map<String, Object>[]) ois.readObject());
            } catch (Exception e) {
                //Logger.exception(e);
            }
        }
        return new ItemStack[]{new ItemStack(Material.AIR)};
    }
    
    //Serialize list of items
    private static Map<String, Object>[] serializeItemStack(ItemStack[] items) {
        Map<String, Object>[] result = new Map[items.length];
        boolean empty = true;
        
        for (int i = 0; i < items.length; i++) {
            ItemStack is = items[i];
            if (is == null || is.getType().equals(Material.AIR)) {
                result[i] = null;
            } else {
                empty = false;
                result[i] = is.serialize();
                if (is.hasItemMeta()) {
                    result[i].put("meta", is.getItemMeta().serialize());
                }
            }
        }
        
        if (empty) return null;
        return result;
    }
    
    //Deserialize list of items
    private static ItemStack[] deserializeItemStack(Map<String, Object>[] map) {
        ItemStack[] items = new ItemStack[map.length];
        
        for (int i = 0; i < items.length; i++) {
            Map<String, Object> s = map[i];
            if (s == null || s.isEmpty()) {
                items[i] = null;
            } else {
                try {
                    if (s.containsKey("meta")) {
                        Map<String, Object> im = new HashMap<>((Map<String, Object>) s.remove("meta"));
                        im.put("==", "ItemMeta");
                        ItemStack is = ItemStack.deserialize(s);
                        is.setItemMeta((ItemMeta) ConfigurationSerialization.deserializeObject(im));
                        items[i] = is;
                    } else {
                        items[i] = ItemStack.deserialize(s);
                    }
                } catch (Exception e) {
                    //Logger.exception(e);
                    items[i] = null;
                }
            }
            
        }
        
        return items;
    }
}