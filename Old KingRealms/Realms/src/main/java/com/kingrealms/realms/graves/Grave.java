package com.kingrealms.realms.graves;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@SerializableAs("Grave")
public class Grave implements ConfigurationSerializable {
    
    private final UUID player; //mysql
    private final long created; //mysql
    private final Location block1, block2, sign; //mysql
    private Material oldBlock1, oldBlock2, oldSign; //mysql
    private final ItemStack[] items; //mysql
    private final int level; //mysql
    private final float exp; //mysql
    
    public Grave(UUID player, long created, Location block1, Location block2, Location sign, ItemStack[] items, int level, float exp) {
        this.player = player;
        this.created = created;
        this.block1 = block1;
        this.block2 = block2;
        this.sign = sign;
        this.items = items;
        this.level = level;
        this.exp = exp;
    }
    
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("player", this.player.toString());
        serialized.put("created", this.created + "");
        serialized.put("block1", this.block1);
        serialized.put("block2", this.block2);
        serialized.put("sign", this.sign);
        serialized.put("items", new HashSet<>(Arrays.asList(items)));
        serialized.put("oldBlock1", this.oldBlock1.name());
        serialized.put("oldBlock2", this.oldBlock2.name());
        serialized.put("oldSign", this.oldSign.name());
        return serialized;
    }
    
    public static Grave deserialize(Map<String, Object> serialized) {
        UUID player = UUID.fromString((String) serialized.get("player"));
        long created = Long.parseLong((String) serialized.get("created"));
        Location block1 = (Location) serialized.get("block1");
        Location block2 = (Location) serialized.get("block2");
        Location sign = (Location) serialized.get("sign");
        Set<ItemStack> items = (Set<ItemStack>) serialized.get("items");
        Material oldBlock1 = Material.valueOf((String) serialized.get("oldBlock1"));
        Material oldBlock2 = Material.valueOf((String) serialized.get("oldBlock2"));
        Material oldSign = Material.valueOf((String) serialized.get("oldSign"));
        int level = 0;
        try {
            level = Integer.parseInt((String) serialized.get("level"));
        } catch (Exception e) {}
        float exp = 0;
        try {
            exp = Float.parseFloat((String) serialized.get("exp"));
        } catch (Exception e) {}
        Grave grave = new Grave(player, created, block1, block2, sign, items.toArray(new ItemStack[0]), level, exp);
        grave.setOldMaterials(oldBlock1, oldBlock2, oldSign);
        return grave;
    }
    
    public void setOldBlock1(Material oldBlock1) {
        this.oldBlock1 = oldBlock1;
    }
    
    public void setOldBlock2(Material oldBlock2) {
        this.oldBlock2 = oldBlock2;
    }
    
    public void setOldSign(Material oldSign) {
        this.oldSign = oldSign;
    }
    
    public void claimGrave(Player player) {
        if (!player.getUniqueId().equals(this.player)) return;
        ItemStack[] previousItems = player.getInventory().getContents();
        player.getInventory().setContents(this.items);
        player.setLevel(this.level);
        player.setExp(this.exp);
        
        for (ItemStack itemStack : previousItems) {
            if (itemStack != null) {
                player.getInventory().addItem(itemStack);
            }
        }
    
        Block block1 = player.getWorld().getBlockAt(this.block1);
        Block block2 = player.getWorld().getBlockAt(this.block2);
        Block signBlock = player.getWorld().getBlockAt(this.sign);
    
        signBlock.setType(this.oldSign);
        block1.setType(this.oldBlock1);
        block2.setType(this.oldBlock2);
    }
    
    public UUID getPlayer() {
        return player;
    }
    
    public Location getBlock1() {
        return block1;
    }
    
    public Location getBlock2() {
        return block2;
    }
    
    public Location getSign() {
        return sign;
    }
    
    public ItemStack[] getItems() {
        return items;
    }
    
    public long getCreated() {
        return created;
    }
    
    public int getLevel() {
        return level;
    }
    
    public float getExp() {
        return exp;
    }
    
    public void setOldMaterials(Material oldBlock1, Material oldBlock2, Material oldSign) {
        this.oldBlock1 = oldBlock1;
        this.oldBlock2 = oldBlock2;
        this.oldSign = oldSign;
    }
}