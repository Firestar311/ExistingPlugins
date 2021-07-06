package com.kingrealms.realms.economy.shop.types.impl;

import com.kingrealms.realms.economy.EconomyResponse;
import com.kingrealms.realms.economy.shop.enums.ShopType;
import com.kingrealms.realms.economy.shop.item.ShopItem;
import com.kingrealms.realms.economy.shop.types.ISignShop;
import com.kingrealms.realms.economy.shop.types.ServerShop;
import com.kingrealms.realms.items.CustomItem;
import com.kingrealms.realms.items.CustomItemRegistry;
import com.kingrealms.realms.profile.RealmProfile;
import com.starmediadev.lib.builder.ItemBuilder;
import com.starmediadev.lib.items.InventoryStore;
import com.starmediadev.lib.items.NBTWrapper;
import com.starmediadev.lib.util.ID;
import com.starmediadev.lib.util.Utils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

@SuppressWarnings("deprecation")
@SerializableAs("ServerSignShop")
public class ServerSignShop extends ServerShop implements ISignShop {
    private ShopItem item; //mysql
    private Location signLocation; //mysql
    
    public ServerSignShop(ShopItem item, Location signLocation) {
        this(item);
        this.signLocation = signLocation;
    }
    
    public ServerSignShop(ShopItem item) {
        super();
        this.item = item;
        this.shopType = ShopType.SIGN;
    }
    
    public ServerSignShop(Map<String, Object> serialized) {
        super(serialized);
        this.item = (ShopItem) serialized.get("item");
        this.signLocation = (Location) serialized.get("signLocation");
    }
    
    public void setItem(ShopItem item) {
        this.item = item;
    }
    
    public void setLocation(Location location) {
        this.signLocation = location;
    }
    
    @Override
    public void buyItem(RealmProfile buyer, int amount) {
        double price = getItem().getBuyPrice() * amount;
        EconomyResponse fromResponse = buyer.getAccount().canWithdraw(buyer, price);
        EconomyResponse toResponse = getAccount().canDeposit(buyer, price);
        if (fromResponse != EconomyResponse.SUCCESS || toResponse != EconomyResponse.SUCCESS) {
            if (fromResponse == EconomyResponse.NOT_ENOUGH_FUNDS) {
                buyer.sendMessage(Utils.color("&cYou do not have enough funds to purchase that item."));
            }
            return;
        }
    
        ItemStack itemStack = getItem().getItemStack();
        itemStack.setAmount(amount);
        getTransactionHandler().transfer(buyer, price, buyer.getAccount(), getAccount(), "Shop purchase " + getUniqueId().toString());
        buyer.getInventory().addItem(itemStack);
        buyer.sendMessage("&gBought the item &h" + ChatColor.stripColor(Utils.color(getItem().getDisplayName())));
    }
    
    @Override
    public void sellItem(RealmProfile seller) {
        ItemStack compare = getItem().getItemStack();
        int amount = 0, amountCustom = 0;
        String customid = "";
        for (ItemStack itemStack : seller.getInventory().getContents()) {
            if (compare.isSimilar(itemStack)) {
                try {
                    String itemId = NBTWrapper.getNBTString(itemStack, "itemid");
                    CustomItem customItem = CustomItemRegistry.REGISTRY.get(new ID(itemId));
                    if (customItem != null) {
                        if (StringUtils.isEmpty(customid)) {
                            customid = itemId;
                        }
                        amountCustom+= itemStack.getAmount();
                        continue;
                    }
                } catch (Exception e) {}
    
                amount += itemStack.getAmount();
            }
        }
    
        if ((amount + amountCustom) == 0) {
            seller.sendMessage("&cYou do not have any of that item in your inventory.");
            return;
        }
        
        ItemStack item = getItem().getItemStack().clone();
        item.setAmount(amount);
        CustomItem customItem = CustomItemRegistry.REGISTRY.get(new ID(customid));
        double price = (getItem().getSellPrice() * amount) + (getItem().getSellPrice() * amountCustom * customItem.getSellMultiplier());
        EconomyResponse toResponse = seller.getAccount().canDeposit(seller, price);
        EconomyResponse fromResponse = getAccount().canWithdraw(seller, price);
        if (fromResponse != EconomyResponse.SUCCESS || toResponse != EconomyResponse.SUCCESS) {
            return;
        }
    
        getTransactionHandler().transfer(seller, price, getAccount(), seller.getAccount(), "Shop sell " + getUniqueId().toString());
        seller.getInventory().removeItem(item);
        ItemStack customStack = customItem.getItemStack(amountCustom);
        seller.getInventory().removeItem(customStack);
        seller.sendMessage("&gSold &h" + (amount + amountCustom) + " &gof &h" + ChatColor.stripColor(Utils.color(getItem().getDisplayName())));
    }
    
    @Override
    public void update() {
        Block block = getLocation().getBlock();
        BlockState state = block.getState();
        if (state instanceof Sign) {
            Sign sign = (Sign) state;
            sign.setLine(0, Utils.color("&9[SignShop]"));
            sign.setLine(1, Utils.color(getItem().getDisplayName()));
            String prices;
            double buy = getItem().getBuyPrice();
            double sell = getItem().getSellPrice();
            if (buy > 0 && sell == 0) {
                prices = "B:" + buy;
            } else if (sell > 0 && buy == 0) {
                prices = "S:" + sell;
            } else {
                prices = "B:" + buy + " S:" + sell;
            }
            sign.setLine(2, prices);
            sign.setLine(3, "Server Shop");
            sign.update();
        }
    }
    
    @Override
    public Location getLocation() {
        return signLocation;
    }
    
    @Override
    public ShopItem getItem() {
        return item;
    }
    
    @Override
    public Sign getSign() {
        return (Sign) this.signLocation.getBlock().getState();
    }
    
    @SuppressWarnings("DuplicatedCode")
    @Override
    public ItemStack getPlacer() {
        //Temp Item thing
        ItemBuilder itemBuilder = ItemBuilder.start(Material.OAK_SIGN).withEnchantment(Enchantment.ARROW_DAMAGE, 1).withItemFlags(ItemFlag.HIDE_ENCHANTS);
        ItemStack itemStack;
        if (this.uniqueId == null) {
            itemBuilder.withName("&7[Template] &e" + this.name).withLore("&7This is a template item.", "&7Use this to place multiple shops.");
            itemStack = itemBuilder.buildItem();
            try {
                itemStack = NBTWrapper.addNBTString(itemStack, "shopitem", InventoryStore.serializeItemStack(this.item.getItemStack()));
                itemStack = NBTWrapper.addNBTString(itemStack, "shopbuy", this.item.getBuyPrice() + "");
                itemStack = NBTWrapper.addNBTString(itemStack, "shopsell", this.item.getSellPrice() + "");
                itemStack = NBTWrapper.addNBTString(itemStack, "displayname", this.item.getDisplayName());
                itemStack = NBTWrapper.addNBTString(itemStack, "name", this.name);
                itemStack = NBTWrapper.addNBTString(itemStack, "description", this.description);
            } catch (Exception e) {
                return null;
            }
        } else {
            itemBuilder.withName("&e&lShop Placer: &f&l" + this.name).withLore("&7Use this to place the shop.", "&7Note, placing multiple will invalidate", "&7the other signs if they exist.");
            itemStack = itemBuilder.buildItem();
            try {
                itemStack = NBTWrapper.addNBTString(itemStack, "shopid", this.uniqueId.toString());
            } catch (Exception e) {
                return null;
            }
        }
        
        return itemStack;
    }
}