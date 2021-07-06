package com.firestar311.lib.builder;

import com.firestar311.lib.items.NBTWrapper;
import com.firestar311.lib.util.Utils;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;

import java.util.*;
import java.util.Map.Entry;

public class ItemBuilder {
    
    private int amount = 0;
    private int durability = 0;
    private Map<Enchantment, Integer> enchantments = new HashMap<>();
    private Set<ItemFlag> itemFlags = new HashSet<>();
    private ItemStack itemStack;
    private List<String> lore = new ArrayList<>();
    private Material material = Material.AIR;
    private String name;
    private Map<String, String> nbtData = new HashMap<>();
    private UUID skullOwner;
    private boolean unbreakable = false, clearFlags, clearEnchantments, clearLore;
    
    public ItemBuilder(Material material) {
        this.material = material;
    }
    
    public ItemBuilder(ItemStack stack) {
        this.itemStack = stack;
    }
    
    public ItemBuilder() {
    }
    
    public static ItemBuilder start(Material material) {
        return new ItemBuilder(material);
    }
    
    public static ItemBuilder start(ItemStack itemstack) {
        return new ItemBuilder(itemstack);
    }
    
    public ItemBuilder withAmount(int amount) {
        this.amount = amount;
        return this;
    }
    
    public ItemBuilder asMaterial(Material material) {
        this.material = material;
        return this;
    }
    
    public ItemBuilder withName(String name) {
        this.name = name;
        return this;
    }
    
    public ItemBuilder withDurability(int durability) {
        return withDurability((short) durability);
    }
    
    public ItemBuilder withDurability(short durability) {
        this.durability = durability;
        return this;
    }
    
    public ItemBuilder withItemFlags(ItemFlag... flags) {
        this.itemFlags.addAll(Arrays.asList(flags));
        return this;
    }
    
    public ItemBuilder withEnchantment(Enchantment enchantment, Integer level) {
        this.enchantments.put(enchantment, level);
        return this;
    }
    
    public ItemBuilder withEnchantments(Map<Enchantment, Integer> enchantments) {
        this.enchantments.putAll(enchantments);
        return this;
    }
    
    public ItemBuilder makeUnbreakable() {
        this.unbreakable = true;
        return this;
    }
    
    public ItemBuilder withLore(String... lore) {
        this.lore.addAll(Arrays.asList(lore));
        return this;
    }
    
    public ItemBuilder withLore(List<String> lore) {
        this.lore.addAll(lore);
        return this;
    }
    
    public ItemBuilder clearEnchants() {
        this.enchantments.clear();
        this.clearEnchantments = true;
        return this;
    }
    
    public ItemBuilder clearLore() {
        this.lore.clear();
        this.clearLore = true;
        return this;
    }
    
    public ItemBuilder addNBTString(String key, String value) {
        this.nbtData.put(key, value);
        return this;
    }
    
    public ItemBuilder clearNBT() {
        this.nbtData.clear();
        return this;
    }
    
    public ItemBuilder clearFlags() {
        this.itemFlags.clear();
        this.clearFlags = true;
        return this;
    }
    
    public ItemStack buildItem() {
        ItemStack itemStack = (this.itemStack != null) ? this.itemStack.clone() : new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            itemMeta = Bukkit.getItemFactory().getItemMeta(itemStack.getType());
        }
        
        if (itemStack.getType().equals(Material.PLAYER_HEAD) && this.skullOwner != null) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(skullOwner);
            if (player != null) {
                SkullMeta skullMeta = ((SkullMeta) itemMeta);
                skullMeta.setOwningPlayer(player);
            }
        }
    
        if (name != null && !name.equalsIgnoreCase(itemMeta.getDisplayName())) {
            itemMeta.setDisplayName(Utils.color(name));
        }
        if (this.clearLore) {
            itemMeta.setLore(new ArrayList<>());
        }
        if (!this.lore.isEmpty() && !this.lore.equals(itemMeta.getLore())) {
            List<String> coloredLore = new ArrayList<>();
            this.lore.forEach(l -> {
                if (l != null) { coloredLore.add(Utils.color(l)); }
            });
            itemMeta.setLore(coloredLore);
        }
        
        if (this.unbreakable != itemMeta.isUnbreakable()) {
            itemMeta.setUnbreakable(unbreakable);
        }
        
        if (this.clearFlags) {
            itemMeta.removeItemFlags(ItemFlag.values());
        }
        if (!itemFlags.isEmpty() && !itemMeta.getItemFlags().equals(itemFlags)) {
            itemMeta.addItemFlags(itemFlags.toArray(new ItemFlag[0]));
        }
        
        itemStack.setItemMeta(itemMeta);
        
        if (this.clearEnchantments) {
            for (Enchantment enchantment : Enchantment.values()) {
                if (itemStack.containsEnchantment(enchantment)) {
                    itemStack.removeEnchantment(enchantment);
                }
            }
        }
        if (!this.enchantments.isEmpty()) {
            itemStack.addUnsafeEnchantments(enchantments);
        }
        
        if (durability != 0) {
            if (itemMeta instanceof Damageable) {
                ((Damageable) itemMeta).getDamage();
            }
        }
        
        if (amount != 0) {
            itemStack.setAmount(amount);
        }
        
        if (!nbtData.isEmpty()) {
            for (Entry<String, String> entry : this.nbtData.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                try {
                    itemStack = NBTWrapper.addNBTString(itemStack, key, value);
                } catch (Exception e) {}
            }
        }
        
        return itemStack;
    }
    
    public ItemBuilder setLine(int line, String string) {
        try {
            this.lore.set(line, string);
        } catch (IndexOutOfBoundsException e) {
            this.lore.add(line, string);
        }
        return this;
    }
    
    public ItemBuilder setSkullOwner(UUID skullOwner) {
        this.skullOwner = skullOwner;
        return this;
    }
}
