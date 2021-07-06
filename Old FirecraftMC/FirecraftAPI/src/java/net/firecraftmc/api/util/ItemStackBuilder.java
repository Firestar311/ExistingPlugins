package net.firecraftmc.api.util;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class ItemStackBuilder {
    
    private String name;
    private Material material = Material.AIR;
    private int amount = 0;
    private short durability = 0;
    private Set<ItemFlag> itemFlags = new HashSet<>();
    private Map<Enchantment, Integer> enchantments = new HashMap<>();
    private List<String> lore = new ArrayList<>();
    private boolean unbreakable = false;
    
    public ItemStackBuilder(Material material) {
        this.material = material;
    }
    
    public ItemStackBuilder(ItemStack stack) {
        this.material = stack.getType();
        this.amount = stack.getAmount();
        this.durability = stack.getDurability();
        if (stack.hasItemMeta()) {
            this.name = stack.getItemMeta().getDisplayName();
            if (stack.getItemMeta().getItemFlags() != null && !stack.getItemMeta().getItemFlags().isEmpty())
                this.itemFlags.addAll(stack.getItemMeta().getItemFlags());
            if (stack.getItemMeta().getEnchants() != null && !stack.getItemMeta().getEnchants().isEmpty())
                this.enchantments.putAll(stack.getItemMeta().getEnchants());
            if (stack.getItemMeta().hasLore())
                this.itemFlags.addAll(stack.getItemMeta().getItemFlags());
            this.unbreakable = stack.getItemMeta().isUnbreakable();
        }
    }
    
    public ItemStackBuilder() {
    }
    
    public ItemStackBuilder withAmount(int amount) {
        this.amount = amount;
        return this;
    }
    
    public ItemStackBuilder asMaterial(Material material) {
        this.material = material;
        return this;
    }
    
    public ItemStackBuilder withName(String name) {
        this.name = name;
        return this;
    }
    
    public ItemStackBuilder withDurability(short durability) {
        this.durability = durability;
        return this;
    }
    
    public ItemStackBuilder withDurability(int durability) {
        return withDurability((short) durability);
    }
    
    public ItemStackBuilder withItemFlags(ItemFlag... flags) {
        this.itemFlags.addAll(Arrays.asList(flags));
        return this;
    }
    
    public ItemStackBuilder withEnchantment(Enchantment enchantment, Integer level) {
        this.enchantments.put(enchantment, level);
        return this;
    }
    
    public ItemStackBuilder withEnchantments(Map<Enchantment, Integer> enchantments) {
        this.enchantments.putAll(enchantments);
        return this;
    }
    
    public ItemStackBuilder makeUnbreakable() {
        this.unbreakable = true;
        return this;
    }
    
    public ItemStackBuilder withLore(String... lore) {
        this.lore.addAll(Arrays.asList(lore));
        return this;
    }
    
    public ItemStack buildItem() {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(Utils.color(name));
        if (!this.lore.isEmpty()) {
            List<String> coloredLore = new ArrayList<>();
            this.lore.forEach(l -> coloredLore.add(Utils.color(l)));
            itemMeta.setLore(coloredLore);
        }
        
        itemMeta.setUnbreakable(unbreakable);
        
        if (!itemFlags.isEmpty()) {
            itemMeta.addItemFlags(itemFlags.toArray(new ItemFlag[0]));
        }
        
        itemStack.setItemMeta(itemMeta);
        
        if (!this.enchantments.isEmpty()) {
            itemStack.addUnsafeEnchantments(enchantments);
        }
        
        if (durability != 0) {
            itemStack.setDurability(durability);
        }
        
        if (amount != 0) {
            itemStack.setAmount(amount);
        }
        
        
        return itemStack;
    }
}
