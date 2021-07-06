package com.stardevmc.shop.objects.shops.sign;

import com.firestar311.lib.player.User;
import com.stardevmc.shop.TitanShop;
import com.stardevmc.shop.objects.ShopItem;
import org.bukkit.Location;
import org.bukkit.block.Sign;

public class SignShop {
    
    private String owner;
    private String name;
    private Location sign, chest;
    private ShopItem item;
    private boolean infinite = false;
    
    public SignShop(String owner, String name, Location sign, ShopItem item) {
        this.owner = owner;
        this.name = name;
        this.sign = sign;
        this.item = item;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Location getSign() {
        return sign;
    }
    
    public void setSign(Location sign) {
        this.sign = sign;
    }
    
    public ShopItem getItem() {
        return item;
    }
    
    public void setItem(ShopItem item) {
        this.item = item;
    }
    
    public String getOwner() {
        return owner;
    }
    
    public void setOwner(String owner) {
        this.owner = owner;
    }
    
    public void updateSign() {
        String[] lines = new String[4];
        lines[0] = "&9[SignShop]";
        lines[1] = item.getDisplayName();
        String buy = (item.getPrices().buy() > 0) ? "B:" + item.getPrices().buy() : "";
        String sell = (item.getPrices().sell() > 0) ? "S:" + item.getPrices().sell() + "" : "";
        lines[2] = buy + sell;
        User creatorInfo = TitanShop.getInstance().getPlayerManager().getUser(item.getUuid());
        lines[3] = creatorInfo.getLastName();
        Sign sign = (Sign) this.sign.getWorld().getBlockAt(this.sign).getState();
        for (int i = 0; i < lines.length; i++) {
            sign.setLine(i, lines[i]);
        }
        sign.update(true);
    }
    
    public boolean isInfinite() {
        return infinite;
    }
    
    public void setInfinite(boolean infinite) {
        this.infinite = infinite;
    }
    
    public Location getChest() {
        return chest;
    }
    
    public void setChest(Location chest) {
        this.chest = chest;
    }
}