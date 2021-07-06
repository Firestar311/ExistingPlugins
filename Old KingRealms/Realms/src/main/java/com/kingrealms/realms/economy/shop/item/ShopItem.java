package com.kingrealms.realms.economy.shop.item;

import com.starmediadev.lib.builder.ItemBuilder;
import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static com.starmediadev.lib.util.Constants.NUMBER_FORMAT;

@SerializableAs("ShopItem")
public class ShopItem implements ConfigurationSerializable {
    protected double buyPrice = 0, sellPrice = 0; //mysql
    protected String displayName; //mysql
    @Deprecated
    protected String shopId, shopCategory, shopItemId; //cache, to change
    @Deprecated
    protected String id; //Replace with mysql id
    protected ItemStack itemStack; //mysql
    protected int minAmount = 1;
    @Deprecated
    protected int position = -1;
    
    public ShopItem(ItemStack itemStack, double buyPrice, double sellPrice) {
        this.itemStack = itemStack.clone();
        this.minAmount = itemStack.getAmount();
        this.itemStack.setAmount(1);
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
    }
    
    public ShopItem(ItemStack itemStack, String id) {
        this.itemStack = itemStack.clone();
        this.minAmount = itemStack.getAmount();
        this.itemStack.setAmount(1);
        this.id = id;
    }
    
    public ShopItem(ItemStack itemStack, double buyPrice, double sellPrice, String displayName, String shopId, String shopCategory, String shopItemId, int minAmount) {
        this.itemStack = itemStack;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.displayName = displayName;
        this.shopId = shopId;
        this.shopCategory = shopCategory;
        this.shopItemId = shopItemId;
        this.minAmount = minAmount;
    }
    
    public ShopItem(ItemStack itemStack) {
        this.itemStack = itemStack;
    }
    
    public ShopItem(Map<String, Object> serialized) {
        this.itemStack = (ItemStack) serialized.get("itemStack");
        this.buyPrice = Double.parseDouble((String) serialized.get("buyPrice"));
        this.sellPrice = Double.parseDouble((String) serialized.get("sellPrice"));
        this.minAmount = Integer.parseInt((String) serialized.get("minAmount"));
        this.displayName = (String) serialized.get("displayName");
        if (serialized.containsKey("position")) {
            this.position = Integer.parseInt((String) serialized.get("position"));
        }
        
        if (serialized.containsKey("shopId")) {
            this.shopId = (String) serialized.get("shopId");
        }
        
        if (serialized.containsKey("shopCategory")) {
            this.shopCategory = (String) serialized.get("shopCategory");
        }
        
        if (serialized.containsKey("shopItemId")) {
            this.shopItemId = (String) serialized.get("shopItemId");
        }
        
        if (serialized.containsKey("id")) {
            this.id = (String) serialized.get("id");
        }
    }
    
    @Override
    public Map<String, Object> serialize() {
        return new HashMap<>() {{
            put("itemStack", itemStack);
            put("buyPrice", buyPrice + "");
            put("sellPrice", sellPrice + "");
            put("displayName", displayName);
            put("minAmount", minAmount + "");
            put("position", position + "");
            put("id", id);
            if (StringUtils.isNotEmpty(shopId)) { put("shopId", shopId); }
            if (StringUtils.isNotEmpty(shopCategory)) { put("shopCategory", shopCategory); }
            if (StringUtils.isNotEmpty(shopItemId)) { put("shopItemId", shopItemId); }
        }};
    }
    
    public ItemStack getItemStack() {
        return itemStack;
    }
    
    public void setItemStack(ItemStack item) {
        this.itemStack = item;
    }
    
    public double getBuyPrice() {
        return buyPrice;
    }
    
    public void setBuyPrice(double buyPrice) {
        this.buyPrice = buyPrice;
    }
    
    public double getSellPrice() {
        return sellPrice;
    }
    
    public void setSellPrice(double sellPrice) {
        this.sellPrice = sellPrice;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    public int getMinAmount() {
        return minAmount;
    }
    
    public void setMinAmount(int minAmount) {
        this.minAmount = minAmount;
    }
    
    public int getPosition() {
        return position;
    }
    
    public void setPosition(int position) {
        this.position = position;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(itemStack, buyPrice, sellPrice, minAmount, position);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        ShopItem shopItem = (ShopItem) o;
        return Double.compare(shopItem.buyPrice, buyPrice) == 0 && Double.compare(shopItem.sellPrice, sellPrice) == 0 && minAmount == shopItem.minAmount && position == shopItem.position && Objects.equals(itemStack, shopItem.itemStack);
    }
    
    public String getId() {
        return id;
    }
    
    public ItemStack getShopStack() {
        ItemBuilder itemBuilder = ItemBuilder.start(this.itemStack.getType());
        itemBuilder.withName("&a" + (StringUtils.isNotEmpty(this.displayName) ? this.displayName : this.id));
        
        String buyPrice = "", sellPrice = "";
        if (getBuyPrice() != 0) {
            buyPrice = "&eBuy Price: &f" + NUMBER_FORMAT.format(getBuyPrice());
        }
    
        if (getSellPrice() != 0) {
            sellPrice = "&eSell Price: &f" + NUMBER_FORMAT.format(getSellPrice());
        }
        
        List<String> lore = new LinkedList<>();
        if (StringUtils.isNotEmpty(buyPrice)) {
            lore.add(buyPrice);
        }
        
        if (StringUtils.isNotEmpty(sellPrice)) {
            lore.add(sellPrice);
        }
        lore.add("&eMinimum Amount: &f" + this.minAmount);
        lore.add("");
        lore.add("&6&lLeft Click &fto buy " + this.minAmount);
        lore.add("&6&lShift Left Click &fto buy a stack");
        lore.add("&6&lRight Click &fto sell");
        
        itemBuilder.withLore(lore);
        return itemBuilder.buildItem();
    }
}