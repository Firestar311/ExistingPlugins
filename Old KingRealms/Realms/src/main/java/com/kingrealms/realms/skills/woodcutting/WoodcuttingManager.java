package com.kingrealms.realms.skills.woodcutting;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.api.events.RealmsAPI;
import com.kingrealms.realms.items.CustomItemRegistry;
import com.kingrealms.realms.items.type.ArcaneSapling;
import com.kingrealms.realms.items.type.LumberAxe;
import com.kingrealms.realms.loot.Loot;
import com.kingrealms.realms.profile.RealmProfile;
import com.kingrealms.realms.storage.StorageManager;
import com.starmediadev.lib.collection.IncrementalMap;
import com.starmediadev.lib.config.ConfigManager;
import com.starmediadev.lib.util.Utils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.block.data.type.Sapling;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.world.StructureGrowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.Map.Entry;

public class WoodcuttingManager implements Listener {
    public static final Map<Material, Material> COOLDOWN_MATERIALS = new HashMap<>() {{
        put(Material.OAK_LOG, Material.STRIPPED_OAK_LOG);
        put(Material.BIRCH_LOG, Material.STRIPPED_BIRCH_LOG);
        put(Material.SPRUCE_LOG, Material.STRIPPED_SPRUCE_LOG);
        put(Material.JUNGLE_LOG, Material.STRIPPED_JUNGLE_LOG);
        put(Material.ACACIA_LOG, Material.STRIPPED_ACACIA_LOG);
        put(Material.DARK_OAK_LOG, Material.STRIPPED_DARK_OAK_LOG);
    }};
    
    private IncrementalMap<ArcaneTree> arcaneTrees = new IncrementalMap<>();
    private ConfigManager configManager = StorageManager.woodcuttingConfig;
    
    public WoodcuttingManager() {
        Realms.getInstance().getSeason().addListener(this);
        configManager.setup();
        
        //TODO Faster growth of trees
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Block block = e.getBlock();
        ItemStack hand = e.getItemInHand();
        RealmProfile profile = Realms.getInstance().getProfileManager().getProfile(e.getPlayer());
        
        if (!ArcaneSapling.isArcaneSapling(hand)) return;
        TreeType treeType = ArcaneSapling.getTreeType(hand);
        if (!Realms.getInstance().getSeason().isActive()) {
            e.setCancelled(true);
            profile.sendMessage("&cYou cannot place that because the season is not active.");
            return;
        }
        
        ArcaneTree arcaneTree = new ArcaneTree(block.getLocation(), treeType);
        int id = this.arcaneTrees.add(arcaneTree);
        arcaneTree.setOwner(profile.getUniqueId());
        arcaneTree.setId(id);
        arcaneTree.setDate(System.currentTimeMillis());
        profile.sendMessage("&gPlaced &h" + Utils.capitalizeEveryWord(treeType.name()) + " Arcane Tree");
    }
    
    @EventHandler
    public void onTreeGrow(StructureGrowEvent e) {
        for (ArcaneTree arcaneTree : arcaneTrees.values()) {
            if (arcaneTree.contains(e.getLocation())) {
                for (BlockState state : e.getBlocks()) {
                    if (state.getType().equals(arcaneTree.getTreeType().getLog())) {
                        arcaneTree.addWoodLocation(state.getLocation());
                    } else if (state.getBlockData() instanceof Leaves) {
                        arcaneTree.addLeafLocation(state.getLocation());
                    }
                }
                return;
            }
        }
    }
    
    @EventHandler
    public void onHarvest(BlockBreakEvent e) {
        Block block = e.getBlock();
        RealmProfile profile = RealmsAPI.getProfile(e.getPlayer());
        ItemStack hand = e.getPlayer().getInventory().getItemInMainHand();
        
        ArcaneTree arcaneTree = null;
        for (ArcaneTree tree : this.arcaneTrees.values()) {
            if (tree.contains(block.getLocation())) {
                arcaneTree = tree;
                break;
            }
        }
        
        if (arcaneTree == null) return;
        
        if (block.getBlockData() instanceof Sapling) {
            this.arcaneTrees.remove(arcaneTree.getId());
            profile.sendMessage("&gRemoved &h" + Utils.capitalizeEveryWord(arcaneTree.getTreeType().name()) + " Arcane Tree");
            e.setDropItems(false);
            Item item = block.getLocation().getWorld().dropItem(block.getLocation(), CustomItemRegistry.ARCANE_SAPLINGS.getItem(arcaneTree.getTreeType()).getItemStack());
            item.setVelocity(new Vector(0, 0, 0));
        } else {
            if (COOLDOWN_MATERIALS.containsKey(block.getType())) {
                if (LumberAxe.isLumberAxe(hand)) {
                    List<Loot> drops = arcaneTree.getLootTable().generateLoot();
                    arcaneTree.removeTree();
                    for (Loot loot : drops) {
                        profile.getInventory().addItem(loot.getItemStack());
                    }
                } else {
                    e.setCancelled(true);
                    profile.sendMessage("&cYou must harvest Arcane Trees with a Lumber Axe.");
                }
            }
        }
    }
    
    public void loadData() {
        ConfigurationSection arcaneSection = configManager.getConfig().getConfigurationSection("arcaneTrees");
        if (arcaneSection != null) {
            for (String i : arcaneSection.getKeys(false)) {
                int id = Integer.parseInt(i);
                ArcaneTree arcaneTree = (ArcaneTree) arcaneSection.get(i);
                this.arcaneTrees.put(id, arcaneTree);
            }
        }
    }
    
    public void saveData() {
        configManager.getConfig().set("arcaneTrees", null);
        configManager.saveConfig();
        for (Entry<Integer, ArcaneTree> entry : arcaneTrees.entrySet()) {
            configManager.getConfig().set("arcaneTrees." + entry.getKey(), entry.getValue());
        }
        
        configManager.saveConfig();
    }
}