package com.kingrealms.realms.graves;

import com.kingrealms.realms.storage.StorageManager;
import com.starmediadev.lib.config.ConfigManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.block.data.type.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class GraveManager {
    
    private final Set<Grave> graves = new HashSet<>();
    private final ConfigManager configManager = StorageManager.gravesConfig;
    
    public GraveManager() {
        this.configManager.setup();
    }
    
    public void createGrave(Player player) {
        ItemStack[] items = player.getInventory().getContents();
        
        boolean invEmpty = true;
        for (ItemStack itemStack : items) {
            if (itemStack != null) {
                if (!itemStack.getType().equals(Material.AIR)) {
                    invEmpty = false;
                    break;
                }
            }
        }
        
        if (invEmpty) {
            player.sendMessage("&cYou have died but your inventory does not contain items. A grave has not been created.");
            return;
        }
        
        Location reference = player.getWorld().getBlockAt(player.getLocation()).getLocation();
        Location loc1 = reference.clone().subtract(0, 1, 0);
        Location loc2 = reference.clone().subtract(0, 1, 1);
        Location signLoc = reference.clone();
    
        Block block1 = player.getWorld().getBlockAt(loc1);
        Block block2 = player.getWorld().getBlockAt(loc2);
        Block signBlock = player.getWorld().getBlockAt(signLoc);
    
        Material oldBlock1 = block1.getType(), oldBlock2 = block2.getType(), oldSign = signBlock.getType();
        
        block1.setType(Material.SOUL_SAND);
        block2.setType(Material.SOUL_SAND);
        
        signBlock.setType(Material.OAK_SIGN);
        BlockState signBlockState = signBlock.getState();
        org.bukkit.block.Sign signState = (org.bukkit.block.Sign) signBlockState;
        signState.setLine(1, "Grave of");
        signState.setLine(2, player.getName());
        Sign signData = (Sign) signState.getBlockData();
        signData.setRotation(BlockFace.NORTH);
        signState.setBlockData(signData);
        signState.update();
        
        Grave grave = new Grave(player.getUniqueId(), System.currentTimeMillis(), loc1, loc2, signLoc, items, player.getLevel(), player.getExp());
        grave.setOldMaterials(oldBlock1, oldBlock2, oldSign);
        this.graves.add(grave);
    }
    
    public void removeGrave(UUID uniqueId) {
        this.graves.removeIf(grave -> grave.getPlayer().equals(uniqueId));
    }
    
    public Grave getGrave(Location location) {
        for (Grave grave : this.graves) {
            if (grave.getBlock1().equals(location) || grave.getBlock2().equals(location) || grave.getSign().equals(location)) {
                return grave;
            }
        }
        
        return null;
    }
    
    public Grave getGrave(UUID uuid) {
        for (Grave grave : this.graves) {
            if (grave.getPlayer().equals(uuid)) {
                return grave;
            }
        }
        return null;
    }
    
    public void saveData() {
        this.configManager.getConfig().set("graves", null);
        this.graves.forEach(grave -> configManager.getConfig().set("graves." + grave.getPlayer().toString(), grave));
        this.configManager.saveConfig();
    }
    
    public void loadData() {
        FileConfiguration config = this.configManager.getConfig();
        ConfigurationSection graveSection = config.getConfigurationSection("graves");
        if (graveSection == null) return;
        for (String g : graveSection.getKeys(false)) {
            Grave grave = (Grave) graveSection.get(g);
            this.graves.add(grave);
        }
    }
    
    public Collection<Grave> getGraves() {
        return new ArrayList<>(graves);
    }
    
    public void removeGrave(Location location) {
        this.graves.removeIf(grave -> grave.getSign().equals(location) || grave.getBlock1().equals(location) || grave.getBlock2().equals(location));
    }
}