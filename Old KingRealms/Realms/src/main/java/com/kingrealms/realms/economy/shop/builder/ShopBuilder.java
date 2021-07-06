package com.kingrealms.realms.economy.shop.builder;

import com.kingrealms.realms.economy.shop.Shop;
import com.kingrealms.realms.economy.shop.enums.OwnerType;
import com.kingrealms.realms.economy.shop.enums.ShopType;
import com.kingrealms.realms.economy.shop.item.ShopItem;
import com.kingrealms.realms.economy.shop.types.impl.ServerSignShop;
import com.starmediadev.lib.builder.ItemBuilder;
import com.starmediadev.lib.items.InventoryStore;
import com.starmediadev.lib.items.NBTWrapper;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.UUID;

@SuppressWarnings("unused")
public class ShopBuilder {
    protected ShopItem item;
    protected String name, description;
    protected String owner;
    protected OwnerType ownerType;
    protected ShopType shopType;
    protected Location signLocation;
    protected boolean template;
    protected UUID uniqueId;
    
    public ShopBuilder setName(String name) {
        this.name = name;
        return this;
    }
    
    public boolean checkRequired() {
        if (StringUtils.isEmpty(this.name)) {
            return false;
        }
        
        if (this.item == null) {
            return false;
        }
        
        if (this.item.getBuyPrice() == -1 || this.item.getSellPrice() == -1) {
            return false;
        }
        
        return !StringUtils.isEmpty(this.item.getDisplayName());
    }
    
    public LinkedList<String> getRemainingValues() {
        return new LinkedList<>() {{
            if (StringUtils.isEmpty(name)) {
                add("Name");
            }
            
            if (item == null) {
                add("Item");
                add("Item Buy Price");
                add("Item Sell Price");
                add("Item Display Name");
            } else {
                if (item.getBuyPrice() == -1) {
                    add("Item Buy Price");
                }
                
                if (item.getSellPrice() == -1) {
                    add("Item Sell Price");
                }
                
                if (StringUtils.isEmpty(item.getDisplayName())) {
                    add("Item Display Name");
                }
            }
            
            if (StringUtils.isEmpty(description)) {
                add("[Optional] Description");
            }
        }};
    }
    
    public ShopBuilder setDescription(String description) {
        this.description = description;
        return this;
    }
    
    public ShopBuilder setOwner(String owner) {
        this.owner = owner;
        return this;
    }
    
    public ShopBuilder setUniqueId(UUID uniqueId) {
        this.uniqueId = uniqueId;
        return this;
    }
    
    public ShopBuilder setSignLocation(Location signLocation) {
        this.signLocation = signLocation;
        return this;
    }
    
    public ShopItem getItem() {
        return this.item;
    }
    
    public ShopBuilder setItem(ShopItem item) {
        this.item = item;
        return this;
    }
    
    @SuppressWarnings("DuplicatedCode")
    public ItemStack createTemplate() {
        //Temp Item thing
        ItemStack itemStack = ItemBuilder.start(Material.OAK_SIGN).withName("&e&l[Template] Shop Placer: &f&l" + this.name).withEnchantment(Enchantment.ARROW_DAMAGE, 1).withItemFlags(ItemFlag.HIDE_ENCHANTS).withLore("&7This is a template item.", "&7Use this to place multiple shops.").buildItem();
        try {
            itemStack = NBTWrapper.addNBTString(itemStack, "shopitem", InventoryStore.serializeItemStack(this.item.getItemStack()));
            itemStack = NBTWrapper.addNBTString(itemStack, "shopbuy", this.item.getBuyPrice() + "");
            itemStack = NBTWrapper.addNBTString(itemStack, "shopsell", this.item.getSellPrice() + "");
            itemStack = NBTWrapper.addNBTString(itemStack, "displayname", this.item.getDisplayName());
            itemStack = NBTWrapper.addNBTString(itemStack, "name", this.name);
            itemStack = NBTWrapper.addNBTString(itemStack, "description", this.description);
        } catch (Exception e) {
        }
        
        return itemStack;
    }
    
    public Shop build() {
        ServerSignShop shop = new ServerSignShop(this.item);
        shop.setFromTemplate(this.template);
        shop.setName(this.name);
        shop.setDescription(this.description);
        return shop;
    }
    
    public OwnerType getOwnerType() {
        return ownerType;
    }
    
    public ShopBuilder setOwnerType(OwnerType ownerType) {
        this.ownerType = ownerType;
        return this;
    }
    
    public ShopType getShopType() {
        return shopType;
    }
    
    public ShopBuilder setShopType(ShopType shopType) {
        this.shopType = shopType;
        return this;
    }
    
    public boolean isTemplate() {
        return template;
    }
    
    public ShopBuilder setTemplate(boolean template) {
        this.template = template;
        return this;
    }
}