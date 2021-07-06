package com.kingrealms.realms.economy.shop;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.economy.shop.item.ShopItem;
import com.kingrealms.realms.economy.shop.types.ISignShop;
import com.kingrealms.realms.economy.shop.types.impl.ServerSignShop;
import com.kingrealms.realms.economy.shop.types.impl.gui.ServerGUIShop;
import com.kingrealms.realms.profile.RealmProfile;
import com.starmediadev.lib.items.InventoryStore;
import com.starmediadev.lib.items.NBTWrapper;
import com.starmediadev.lib.util.Utils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ShopHandler implements Listener {
    
    private final Map<String, ShopItem> items = new HashMap<>();
    private final Map<UUID, Shop> shops = new HashMap<>();
    private ServerGUIShop defaultShop;
    
    public ShopHandler() {
        Realms.getInstance().getServer().getPluginManager().registerEvents(this, Realms.getInstance());
    }
    
    public void saveData(ConfigurationSection section) {
        if (!shops.isEmpty()) {
            for (Shop shop : this.shops.values()) {
                section.set(shop.getUniqueId().toString(), shop);
            }
        }
        
        if (!items.isEmpty()) {
            for (ShopItem shopItem : items.values()) {
                section.set(shopItem.getId(), shopItem);
            }
        }
    }
    
    public ServerGUIShop getDefaultShop() {
        if (this.defaultShop == null) {
            this.defaultShop = new ServerGUIShop(Material.STONE);
            this.defaultShop.setName("Main Shop");
            this.defaultShop.setDescription("The main server gui shop");
            addShop(defaultShop);
        }
        
        return defaultShop;
    }
    
    public void addShop(Shop shop) {
        if (shop.getUniqueId() == null) {
            UUID uuid;
            do {
                uuid = UUID.randomUUID();
            } while (this.shops.containsKey(uuid));
            shop.setUniqueId(uuid);
        }
        
        this.shops.put(shop.getUniqueId(), shop);
    }
    
    public void loadData(ConfigurationSection section) {
        if (section != null) {
            for (String s : section.getKeys(false)) {
                if (section.get(s) instanceof Shop) {
                    Shop shop = (Shop) section.get(s);
                    this.shops.put(shop.getUniqueId(), shop);
                    
                    if (shop instanceof ServerGUIShop) {
                        this.defaultShop = (ServerGUIShop) shop;
                    }
                } else if (section.get(s) instanceof ShopItem) {
                    ShopItem item = (ShopItem) section.get(s);
                    this.items.put(item.getId(), item);
                }
            }
        }
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Material handType = e.getItemInHand().getType();
        if (!(handType == Material.OAK_SIGN || handType == Material.ACACIA_SIGN || handType == Material.BIRCH_SIGN || handType == Material.DARK_OAK_SIGN || handType == Material.SPRUCE_SIGN || handType == Material.JUNGLE_SIGN)) {
            return;
        }
        
        ItemStack heldItem = e.getItemInHand();
        UUID shopId = null;
        ItemStack itemStack = null;
        double buy = 0, sell = 0;
        String itemDisplay = null, name = null, description = null;
        try {
            shopId = UUID.fromString(NBTWrapper.getNBTString(heldItem, "shopid"));
        } catch (Exception ex) {}
        
        try {
            itemStack = InventoryStore.deserializeItemStack(NBTWrapper.getNBTString(heldItem, "shopitem"));
        } catch (Exception ex) {}
        
        try {
            buy = Double.parseDouble(NBTWrapper.getNBTString(heldItem, "shopbuy"));
        } catch (Exception ex) {}
        
        try {
            sell = Double.parseDouble(NBTWrapper.getNBTString(heldItem, "shopsell"));
        } catch (Exception ex) {}
        
        try {
            itemDisplay = NBTWrapper.getNBTString(heldItem, "displayname");
        } catch (Exception ex) {}
        
        try {
            name = NBTWrapper.getNBTString(heldItem, "name");
        } catch (Exception ex) {}
        
        try {
            description = NBTWrapper.getNBTString(heldItem, "description");
        } catch (Exception ex) {}
        
        
        ISignShop signShop;
        if (shopId != null) {
            Shop shop = Realms.getInstance().getEconomyManager().getShopHandler().getShop(shopId);
            shop.setFromTemplate(false);
            signShop = (ISignShop) shop;
        } else {
            if (itemStack != null && !StringUtils.isEmpty(itemDisplay) || !StringUtils.isEmpty(name)) {
                ShopItem shopItem = new ShopItem(itemStack, buy, sell);
                shopItem.setDisplayName(itemDisplay);
                signShop = new ServerSignShop(shopItem);
                Shop shop = (Shop) signShop;
                shop.setFromTemplate(true);
                shop.setName(name);
                if (!StringUtils.isEmpty(description)) {
                    shop.setDescription(description);
                }
                Realms.getInstance().getEconomyManager().getShopHandler().addShop((Shop) signShop);
            } else {
                return;
            }
        }
        
        if (signShop.getLocation() != null) {
            Block oldBlock = signShop.getLocation().getBlock();
            if (oldBlock != null) {
                if (oldBlock.getState() instanceof Sign) {
                    Sign oldSign = (Sign) oldBlock.getState();
                    oldSign.setLine(0, "");
                    oldSign.setLine(1, Utils.color("&cINVALID"));
                    oldSign.setLine(2, Utils.color("&cSHOP"));
                    oldSign.setLine(3, "");
                    oldSign.update();
                }
            }
        }
        
        signShop.setLocation(e.getBlockPlaced().getLocation());
        
        ((ServerSignShop) signShop).update();
    }
    
    public Shop getShop(UUID uuid) {
        return this.shops.get(uuid);
    }
    
    @EventHandler
    public void onSignClick(PlayerInteractEvent e) {
        Block block = e.getClickedBlock();
        if (!(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.LEFT_CLICK_BLOCK))) {
            return;
        }
        if (!(block.getState() instanceof Sign)) { return; }
        
        ISignShop shop = getSignShop(block.getLocation());
        if (shop == null) {
            return;
        }
        
        RealmProfile profile = Realms.getInstance().getProfileManager().getProfile(e.getPlayer());
        if (e.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            e.setCancelled(true);
            int amount = 1;
            
            if (e.getPlayer().isSneaking()) {
                amount = 64;
            }
            
            shop.buyItem(profile, amount);
        } else if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            shop.sellItem(profile);
        }
    }
    
    public ISignShop getSignShop(Location location) {
        for (Shop shop : this.shops.values()) {
            if (shop instanceof ISignShop) {
                ISignShop signShop = (ISignShop) shop;
                if (signShop.getLocation() != null) {
                    if (signShop.getLocation().equals(location)) {
                        return signShop;
                    }
                }
            }
        }
        
        return null;
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        ISignShop signShop = getSignShop(e.getBlock().getLocation());
        if (signShop == null) { return; }
        
        Shop shop = (Shop) signShop;
        if (shop.isFromTemplate()) {
            removeShop(shop);
        }
    }
    
    public void removeShop(Shop shop) {
        this.shops.remove(shop.getUniqueId());
    }
    
    public Shop getShop(String name) {
        for (Shop shop : this.shops.values()) {
            if (shop.getName().equalsIgnoreCase(name)) {
                return shop;
            }
        }
        return null;
    }
    
    public Collection<Shop> getShops() {
        return new ArrayList<>(this.shops.values());
    }
    
    public void addItem(ShopItem shopItem) {
        this.items.put(shopItem.getId(), shopItem);
    }
    
    public ShopItem getItem(String id) {
        return this.items.get(id);
    }
    
    public void removeItem(ShopItem shopItem) {
        this.items.remove(shopItem.getId());
    }
    
    public Collection<ShopItem> getItems() {
        return new ArrayList<>(this.items.values());
    }
}