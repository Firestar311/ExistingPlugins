package com.kingrealms.realms.economy.shop.types.impl.gui;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.economy.EconomyResponse;
import com.kingrealms.realms.economy.shop.Shop;
import com.kingrealms.realms.economy.shop.item.ShopItem;
import com.kingrealms.realms.items.CustomItem;
import com.kingrealms.realms.items.CustomItemRegistry;
import com.kingrealms.realms.items.type.CraftingPart;
import com.kingrealms.realms.profile.RealmProfile;
import com.starmediadev.lib.builder.ItemBuilder;
import com.starmediadev.lib.collection.IncrementalMap;
import com.starmediadev.lib.gui.PaginatedGUI;
import com.starmediadev.lib.items.NBTWrapper;
import com.starmediadev.lib.util.*;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@SerializableAs("ShopCategory")
public class ShopCategory implements ConfigurationSerializable {
    
    private String name, description, id; //mysql
    @Deprecated
    private UUID shopId; //Replace with Mysql autoid
    private Shop shop; //cache
    private Material icon; //mysql
    private IncrementalMap<String> items = new IncrementalMap<>(); //mysql
    
    public ShopCategory(Shop shop, Material material, String id, String name, String description) {
        this.shop = shop;
        this.shopId = shop.getUniqueId();
        this.id = id;
        this.name = name;
        this.description = description;
        icon = material;
    }
    
    public ShopCategory(Map<String, Object> serialized) {
        this.id = (String) serialized.get("id");
        this.name = (String) serialized.get("name");
        this.shopId = UUID.fromString((String) serialized.get("shopId"));
        this.icon = Material.valueOf((String) serialized.get("icon"));
        this.description = (String) serialized.get("description");
        serialized.forEach((key, value) -> {
            if (key.startsWith("item-")) {
                int pos = Integer.parseInt(key.split("-")[1]);
                String item = (String) value;
                items.put(pos, item);
            }
        });
    }
    
    public Map<String, Object> serialize() {
        return new HashMap<>() {{ 
            put("name", name);
            put("description", description);
            put("id", id);
            put("shopId", shopId.toString());
            put("icon", icon.name());
            items.forEach((pos, item) -> put("item-" + pos, item));
        }};
    }
    
    public ShopCategory(Shop shop, Material material, String id) {
        this.shop = shop;
        this.shopId = shop.getUniqueId();
        this.id = id;
        setIcon(material);
    }
    
    public ItemStack getIcon() {
        return ItemBuilder.start(icon).withName("&d" + (StringUtils.isNotEmpty(this.name) ? this.name : this.id)).withLore(Utils.wrapLore(40, this.description)).buildItem();
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getId() {
        return id;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Shop getShop() {
        if (shop == null) {
            shop = Realms.getInstance().getEconomyManager().getShopHandler().getShop(this.shopId);
        }
        return shop;
    }
    
    public Collection<ShopItem> getItems() {
        List<ShopItem> shopItems = new ArrayList<>();
        for (String i : items.values()) {
            shopItems.add(Realms.getInstance().getEconomyManager().getShopHandler().getItem(i));
        }
        return shopItems;
    }
    
    public void addItem(ShopItem item) {
        if (item.getPosition() == -1) {
            int pos = items.add(item.getId());
            item.setPosition(pos);
        } else {
            items.put(item.getPosition(), item.getId());
        }
    }
    
    public void insertItem(int position, ShopItem item) {
        if (this.items.containsKey(position)) {
            SortedMap<Integer, String> tailMap = new TreeMap<>(this.items.tailMap(position));
            item.setPosition(position);
            this.items.put(position, item.getId());
            tailMap.forEach((pos, t) -> {
                items.put(pos + 1, t);
                ShopItem si = Realms.getInstance().getEconomyManager().getShopHandler().getItem(t);
                si.setPosition(pos + 1);
            });
        } else {
            this.items.put(position, item.getId());
        }
    }
    
    public void removeItem(ShopItem item) {
        if (item.getPosition() != -1) {
            this.items.remove(item.getPosition());
        } else {
            this.items.entrySet().removeIf(entry -> entry.getValue().equals(item.getId()));
        }
    }
    
    public ShopItem getItem(int id) {
        return Realms.getInstance().getEconomyManager().getShopHandler().getItem(this.items.get(id));
    }
    
    public void buyItem(int id, RealmProfile buyer, int amount) {
        ShopItem shopItem = getItem(id);
        if (shopItem == null) {
            buyer.sendMessage("&cThe item you selected is not valid.");
            return;
        }
        double price = shopItem.getBuyPrice() * amount;
        if (price == 0) {
            buyer.sendMessage("&cYou cannot buy that item.");
            return;
        }
        EconomyResponse fromResponse = buyer.getAccount().canWithdraw(buyer, price);
        EconomyResponse toResponse = getShop().getAccount().canDeposit(buyer, price);
        if (fromResponse != EconomyResponse.SUCCESS || toResponse != EconomyResponse.SUCCESS) {
            if (fromResponse == EconomyResponse.NOT_ENOUGH_FUNDS) {
                buyer.sendMessage(Utils.color("&cYou do not have enough funds to purchase that item."));
            }
            return;
        }
        
        ItemStack itemStack = shopItem.getItemStack();
        itemStack.setAmount(amount);
        
        getShop().getTransactionHandler().transfer(buyer, price, buyer.getAccount(), getShop().getAccount(), "Shop purchase " + getShop().getUniqueId().toString());
        buyer.getInventory().addItem(itemStack);
        try {
            buyer.sendMessage("&gBought the item &h" + ChatColor.stripColor(Utils.color(shopItem.getDisplayName())));
        } catch (Exception e) {
            buyer.sendMessage("&gBought the item &h" + MaterialNames.getName(shopItem.getItemStack().getType()));
        }
    }
    
    public void sellItem(int id, RealmProfile seller) {
        ItemStack compare = getItem(id).getItemStack();
        int amount = 0, amountCustom = 0;
        String customid = "";
        for (ItemStack itemStack : seller.getInventory().getContents()) {
            if (itemStack != null) {
                if (compare.getType() == itemStack.getType()) {
                    try {
                        String itemId = NBTWrapper.getNBTString(itemStack, "itemid");
                        CustomItem customItem = CustomItemRegistry.REGISTRY.get(new ID(itemId));
                        if (customItem instanceof CraftingPart) continue;
                        if (customItem != null) {
                            if (StringUtils.isEmpty(customid)) {
                                customid = itemId;
                            }
                            amountCustom += itemStack.getAmount();
                            continue;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
        
                    amount += itemStack.getAmount();
                }
            }
        }
    
        if ((amount + amountCustom) == 0) {
            seller.sendMessage("&cYou do not have any of that item in your inventory.");
            return;
        }
        
        ShopItem shopItem = getItem(id);
        if ((amount + amountCustom) < shopItem.getMinAmount()) {
            seller.sendMessage("&cYou must have at least " + shopItem.getMinAmount() + " of that item in your inventory.");
            return;
        }
        
        ItemStack item = getItem(id).getItemStack().clone();
        item.setAmount(amount);
    
        CustomItem customItem = CustomItemRegistry.REGISTRY.get(new ID(customid));
        double customPrice = 0;
        if (customItem != null) {
            customPrice = getItem(id).getSellPrice() * amountCustom * customItem.getSellMultiplier();
        }
    
        double price = (getItem(id).getSellPrice() * amount) + customPrice;
        if (price == 0) {
            seller.sendMessage("&cYou cannot sell that item.");
            return;
        }
        EconomyResponse toResponse = seller.getAccount().canDeposit(seller, price);
        EconomyResponse fromResponse = getShop().getAccount().canWithdraw(seller, price);
        if (fromResponse != EconomyResponse.SUCCESS || toResponse != EconomyResponse.SUCCESS) {
            return;
        }
    
        getShop().getTransactionHandler().transfer(seller, price, getShop().getAccount(), seller.getAccount(), "Shop sell " + getShop().getUniqueId().toString());
        seller.getInventory().removeItem(item);
        if (customItem != null) {
            ItemStack customStack = customItem.getItemStack(amountCustom);
            customStack.setAmount(amountCustom);
            seller.getInventory().removeItem(customStack);
        }
        try {
            seller.sendMessage("&gSold &h" + (amount + amountCustom) + " &gof &h" + ChatColor.stripColor(Utils.color(getItem(id).getDisplayName())));
        } catch (Exception e) {
            seller.sendMessage("&gSold &h" + (amount + amountCustom) + " &gof &h" + MaterialNames.getName(getItem(id).getItemStack().getType()));
        }
    }
    
    public Map<Integer, ShopItem> getAllItems() {
        Map<Integer, ShopItem> shopItems = new TreeMap<>();
        this.items.forEach((pos, id) -> shopItems.put(pos, getItem(pos)));
        return shopItems;
    }
    
    public PaginatedGUI getGui() {
        return new CategoryGui(this);
    }
    
    public String getName() {
        return name;
    }
    
    public void setIcon(Material iconType) {
        this.icon = iconType;
    }
}