package com.kingrealms.realms.supplydrops;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.api.events.RealmsAPI;
import com.kingrealms.realms.items.CustomItem;
import com.kingrealms.realms.items.CustomItemRegistry;
import com.kingrealms.realms.items.type.*;
import com.kingrealms.realms.loot.LootTable;
import com.kingrealms.realms.loot.Rarity;
import com.kingrealms.realms.plot.Plot;
import com.kingrealms.realms.profile.RealmProfile;
import com.kingrealms.realms.storage.StorageManager;
import com.kingrealms.realms.util.RealmsLoot;
import com.starmediadev.lib.collection.IncrementalMap;
import com.starmediadev.lib.config.ConfigManager;
import com.starmediadev.lib.util.Utils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class SupplyDropManager implements Listener {
    
    private int supplyCrateLootTable = -1;
    private IncrementalMap<SupplyCrate> supplyCrates = new IncrementalMap<>();
    private ConfigManager configManager = StorageManager.supplyDropConfig;
    
    public SupplyDropManager() {
        Realms.getInstance().getSeason().addListener(this);
        configManager.setup();
    }
    
    public void addSupplyCrate(SupplyCrate crate) {
        int pos = supplyCrates.add(crate);
        crate.setId(pos);
    }
    
    public void generateSupplyDrop() {
        if (!supplyCrates.isEmpty()) {
            for (SupplyCrate crate : supplyCrates.values()) {
                Block block = crate.getLocation().getBlock();
                if (block instanceof Chest) {
                    Chest chest = (Chest) block.getState();
                    for (int i = 0; i < chest.getInventory().getSize(); i++) {
                        ItemStack itemStack = chest.getInventory().getItem(i);
                        if (itemStack != null && itemStack.getType() != Material.AIR) {
                            chest.getInventory().setItem(i, null);
                        }
                    }
                }
                
                block.setType(Material.AIR);
            }
            supplyCrates.clear();
        }
        
        Random random = new Random();
        int cratesToGenerate = random.nextInt(75) + 25;
        List<Plot> plots = new LinkedList<>(Realms.getInstance().getWarzone().getPlots());
        Collections.shuffle(plots);
        for (int i = 0; i < cratesToGenerate; i++) {
            int plotIndex = random.nextInt(plots.size());
            Plot plot = plots.get(plotIndex);
            Location location = plot.getRandomLocation();
            location.setY(location.getWorld().getHighestBlockYAt(location.getBlockX(), location.getBlockZ(), HeightMap.WORLD_SURFACE) + 1);
            
            Block block = location.getBlock();
            if (Realms.getInstance().getFarmingManager().getCropBlock(block.getLocation()) != null) {
                i--;
                continue;
            }
            
            location.getBlock().setType(Material.CHEST);
            SupplyCrate crate = new SupplyCrate(location, getSupplyCrateLootTable());
            supplyCrates.add(crate);
        }
    }
    
    private LootTable getSupplyCrateLootTable() {
        if (supplyCrateLootTable == -1) {
            LootTable lootTable = new LootTable("Supply Crate", 1, 5);
            for (CustomItem item : CustomItemRegistry.MISC_ITEMS.getItems()) {
                lootTable.addPossibleLoot(new RealmsLoot(item, Rarity.COMMON));
            }
    
            for (CropScrap item : CustomItemRegistry.CROP_SCRAPS.getItems()) {
                lootTable.addPossibleLoot(new RealmsLoot(item, Rarity.RARE));
            }
            
            for (MysticalSliver item : CustomItemRegistry.MYSTICAL_SLIVERS.getItems()) {
                lootTable.addPossibleLoot(new RealmsLoot(item, Rarity.RARE));
            }
            
            for (SpawnerShard item : CustomItemRegistry.SPAWNER_SHARDS.getItems()) {
                lootTable.addPossibleLoot(new RealmsLoot(item, Rarity.RARE));
            }
            
            for (CropItem item : CustomItemRegistry.CROP_ITEMS.getItems()) {
                lootTable.addPossibleLoot(new RealmsLoot(item, Rarity.EPIC));
            }
            
            for (MysticalResource item : CustomItemRegistry.MYSTICAL_RESOURCES.getItems()) {
                lootTable.addPossibleLoot(new RealmsLoot(item, Rarity.EPIC));
            }
            
            for (SpawnerItem item : CustomItemRegistry.SPAWNERS.getItems()) {
                lootTable.addPossibleLoot(new RealmsLoot(item, Rarity.EPIC));
            }
            
            Realms.getInstance().getLootManager().addLootTable(lootTable);
            supplyCrateLootTable = lootTable.getId();
        }
        
        return Realms.getInstance().getLootManager().getLootTable(supplyCrateLootTable);
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        Inventory inv = e.getInventory();
        if (inv.getHolder() instanceof Chest) {
            Chest chest = (Chest) e.getInventory().getHolder();
            SupplyCrate crate = getSupplyCrate(chest.getLocation());
            if (crate == null) { return; }
            boolean invEmpty = Utils.inventoryEmpty(chest.getInventory());
            
            if (invEmpty) {
                chest.getBlock().setType(Material.AIR);
                this.supplyCrates.remove(crate.getId());
            }
        }
    }
    
    public SupplyCrate getSupplyCrate(Location location) {
        for (SupplyCrate crate : supplyCrates.values()) {
            if (crate.getLocation().equals(location)) {
                return crate;
            }
        }
        
        return null;
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) { return; }
        if (e.getClickedBlock() == null) { return; }
        SupplyCrate crate = null;
        for (SupplyCrate supplyCrate : getSupplyCrates()) {
            if (supplyCrate.getLocation().equals(e.getClickedBlock().getLocation())) {
                crate = supplyCrate;
                break;
            }
        }
    
        if (crate == null) { return; }
        
        RealmProfile profile = RealmsAPI.getProfile(e.getPlayer());
        crate.openCrate(profile);
    }
    
    public Set<SupplyCrate> getSupplyCrates() {
        return new HashSet<>(this.supplyCrates.values());
    }
    
    public void saveData() {
        for (SupplyCrate supplyCrate : getSupplyCrates()) {
            configManager.getConfig().set("supplycrates." + supplyCrate.getId(), supplyCrate);
        }
        this.configManager.saveConfig();
    }
    
    public void loadData() {
        FileConfiguration config = configManager.getConfig();
        if (config.contains("supplycrates")) {
            ConfigurationSection section = config.getConfigurationSection("supplycrates");
            for (String s : section.getKeys(false)) {
                SupplyCrate supplyCrate = (SupplyCrate) section.get(s);
                this.supplyCrates.add(supplyCrate);
            }
        }
    }
    
    public void removeCrate(int id) {
        this.supplyCrates.remove(id);
    }
}