package com.kingrealms.realms.skills.farming;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.ServerMode;
import com.kingrealms.realms.api.events.RealmsAPI;
import com.kingrealms.realms.items.CustomItemRegistry;
import com.kingrealms.realms.items.type.CropItem;
import com.kingrealms.realms.items.type.CropScrap;
import com.kingrealms.realms.profile.RealmProfile;
import com.kingrealms.realms.skills.SkillType;
import com.kingrealms.realms.storage.StorageManager;
import com.starmediadev.lib.config.ConfigManager;
import com.starmediadev.lib.util.Utils;
import com.starmediadev.lib.workload.WorkloadThread;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class FarmingManager implements Listener {
    
    public static final WorkloadThread workload = new WorkloadThread();
    private ConfigManager configManager = StorageManager.farmingConfig;
    private Set<CropBlock> cropBlocks = new HashSet<>();
    private Realms plugin = Realms.getInstance();
    private FarmingSkill farmingSkill = plugin.getSkillManager().getFarmingSkill();
    
    public FarmingManager() {
        configManager.setup();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        workload.start(Realms.getInstance());
        
        new BukkitRunnable() {
            public void run() {
                if (cropBlocks.isEmpty()) { return; }
                long start = System.currentTimeMillis();
                cropBlocks.forEach(CropBlock::grow);
                long end = System.currentTimeMillis();
                
                long elapsed = end - start;
                if (elapsed > 20) {
                    Realms.getInstance().getLogger().severe("Crop growth calculations took " + elapsed + " milliseconds");
                }
            }
        }.runTaskTimer(plugin, 20L, 10L);
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        Block block = e.getBlockPlaced();
        RealmProfile profile = RealmsAPI.getProfile(e.getPlayer());
        CropItem cropItem = CropItem.getCropItem(e.getItemInHand());
        if (cropItem == null) { return; }
        
        for (CropBlock cb : this.cropBlocks) {
            if (cb.contains(block.getLocation())) {
                profile.sendMessage("&cYou cannot place that there as it interfers with another Crop Block");
                e.setCancelled(true);
                return;
            }
        }
        
        CropBlock cropBlock = cropItem.getCropType().createInstance(block.getLocation());
        if (cropBlock == null) {
            profile.sendMessage("&cError placing that crop block.");
            e.setCancelled(true);
            return;
        }
        
        if (cropBlock.getType() == CropType.SUGAR_CANE) {
            boolean water = false;
            for (BlockFace blockFace : BlockFace.values()) {
                if (block.getRelative(blockFace).getType() == Material.WATER) {
                    water = true;
                    break;
                }
            }
            
            if (!water) {
                profile.sendMessage("&cYou must have at least one water block adjacent to that crop block.");
                e.setCancelled(true);
                return;
            }
        }
        
        cropBlock.setOwner(profile.getUniqueId());
        cropBlock.setDate(System.currentTimeMillis());
        
        if (!plugin.getSeason().isActive()) {
            if (!profile.hasPermission("realms.admin.place.cropblock") && profile.getStaffMode().isActive()) {
                profile.sendMessage("&cYou are not allowed to place crop blocks when the season is not active.");
                e.setCancelled(true);
                return;
            }
        }
        
        if (cropBlock.checkClaim(block.getLocation(), profile, false)) {
            if (!profile.hasPermission("realms.admin.claim.bypass") && profile.getStaffMode().isActive()) {
                profile.sendMessage("&cYou do not have permission to place a Crop Block there.");
                e.setCancelled(true);
                return;
            }
        }
        
        if (plugin.getSeason().isActive()) {
            if (!cropBlock.checkLevel(profile) && !profile.getStaffMode().isActive()) {
                if (plugin.getServerMode() != ServerMode.DEVELOPMENT) {
                    profile.sendMessage("&cYou must be Farming Level " + farmingSkill.getLevel(cropBlock.getType()).getLevel() + " to place that crop block.");
                    e.setCancelled(true);
                    return;
                }
            }
        }
        
        cropBlock.onPlace();
        this.cropBlocks.add(cropBlock);
        profile.sendMessage("&gPlaced &h" + Utils.capitalizeEveryWord(cropBlock.getType().name()) + " Crop Block.");
        if (cropBlock.getType().requiresLight()) {
            if (cropBlock.getSoilLocation().clone().add(0, 1, 0).getBlock().getLightLevel() < 9) {
                profile.sendMessage("&cThat crop type requires a light level of 9 or higher. The crop will not be planted until then.");
            }
        }
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Block block = e.getBlock();
        RealmProfile profile = RealmsAPI.getProfile(e.getPlayer());
        CropBlock cropBlock = getCropBlock(block.getLocation());
        
        if (cropBlock == null) {
            if (!plugin.getSpawn().contains(block.getLocation()) || !plugin.getWarzone().contains(block.getLocation())) {
                for (CropType type : CropType.values()) {
                    if (block.getType() == type.getCropMaterial()) {
                        CropScrap item = CustomItemRegistry.CROP_SCRAPS.getItem(type);
                        int v = new Random().nextInt(100);
                        if (v < 50) {
                            profile.getInventory().addItem(item.getItemStack());
                        }
                        profile.addSkillExperience(SkillType.FARMING, 0.5);
                        break;
                    }
                }
            }
            return;
        }
        
        if (cropBlock.getSoilLocation().equals(block.getLocation())) {
            if (cropBlock.checkClaim(block.getLocation(), profile, false)) {
                if (!profile.hasPermission("realms.admin.claim.bypass") && profile.getStaffMode().isActive()) {
                    profile.sendMessage("&cYou do not have permission to break that crop block.");
                    e.setCancelled(true);
                    return;
                }
            }
            
            if (!plugin.getSeason().isActive()) {
                if (!profile.hasPermission("realms.admin.break.cropblock") && profile.getStaffMode().isActive()) {
                    profile.sendMessage("&cYou do not have permission to break crop blocks while the season is not active.");
                    e.setCancelled(true);
                    return;
                }
            }
            
            if (plugin.getSeason().isActive()) {
                if (!profile.getStaffMode().isActive()) {
                    if (!cropBlock.checkLevel(profile)) {
                        if (plugin.getServerMode() != ServerMode.DEVELOPMENT) {
                            profile.sendMessage("&cYou must be Farming Level " + farmingSkill.getLevel(cropBlock.getType()).getLevel() + " to break that crop block.");
                            e.setCancelled(true);
                            return;
                        }
                    }
                }
            }
            
            cropBlock.onBreak();
            if (profile.getBukkitPlayer().getGameMode() == GameMode.SURVIVAL) {
                CropItem cropItem = CustomItemRegistry.CROP_ITEMS.getItem(cropBlock.getType());
                profile.getInventory().addItem(cropItem.getItemStack());
            }
            e.setDropItems(false);
            e.setExpToDrop(0);
            this.cropBlocks.remove(cropBlock);
            profile.sendMessage("&gRemoved &h" + Utils.capitalizeEveryWord(cropBlock.getType().name()) + " Crop Block.");
        } else {
            e.setDropItems(false);
            e.setExpToDrop(0);
            if (!plugin.getSeason().isActive()) {
                profile.sendMessage("&cYou cannot harvest crop blocks while the season is not active.");
                e.setCancelled(true);
                return;
            }
            
            if (!cropBlock.checkLevel(profile)) {
                if (!(plugin.getSpawn().contains(e.getBlock().getLocation()) || plugin.getWarzone().contains(e.getBlock().getLocation()))) {
                    if (plugin.getServerMode() != ServerMode.DEVELOPMENT) {
                        profile.sendMessage("&cYou must be Farming Level " + farmingSkill.getLevel(cropBlock.getType()).getLevel() + " to harvest that crop block.");
                        e.setCancelled(true);
                        return;
                    }
                }
            }
            
            if (cropBlock.checkClaim(block.getLocation(), profile, true)) {
                profile.sendMessage("&cYou cannot harvest that crop block at that location.");
                e.setCancelled(true);
                return;
            }
            
            cropBlock.onHarvest(block.getLocation(), profile);
        }
    }
    
    public CropBlock getCropBlock(Location location) {
        for (CropBlock cropBlock : this.cropBlocks) {
            if (cropBlock.contains(location)) {
                return cropBlock;
            }
        }
        
        return null;
    }
    
    @EventHandler
    public void onEntityInteract(EntityChangeBlockEvent e) {
        if (e.getBlock().getType().equals(Material.FARMLAND)) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction() == Action.PHYSICAL && e.getClickedBlock() != null) {
            if (e.getClickedBlock().getType() == Material.FARMLAND) {
                e.setCancelled(true);
            }
        }
        
        if (e.getClickedBlock() == null) { return; }
        
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            RealmProfile profile = RealmsAPI.getProfile(e.getPlayer());
            Block block = e.getClickedBlock();
            CropBlock cropBlock = getCropBlock(e.getClickedBlock().getLocation());
            if (cropBlock == null) { return; }
            if (!plugin.getSeason().isActive()) {
                profile.sendMessage("&cYou cannot harvest crop blocks while the season is not active.");
                e.setCancelled(true);
                return;
            }
            
            if (!cropBlock.checkLevel(profile)) {
                if (plugin.getServerMode() != ServerMode.DEVELOPMENT) {
                    profile.sendMessage("&cYou must be Farming Level " + farmingSkill.getLevel(cropBlock.getType()).getLevel() + " to harvest that crop block.");
                    e.setCancelled(true);
                    return;
                }
            }
            
            if (cropBlock.checkClaim(block.getLocation(), profile, true)) {
                profile.sendMessage("&cYou cannot harvest that crop block at that location.");
                e.setCancelled(true);
                return;
            }
            
            cropBlock.onHarvest(block.getLocation(), profile);
        }
    }
    
    public void saveData() {
        configManager.getConfig().set("cropblocks", null);
        configManager.saveConfig();
        int c = 0;
        for (CropBlock cropBlock : cropBlocks) {
            configManager.getConfig().set("cropblocks." + c++, cropBlock);
        }
        
        configManager.saveConfig();
    }
    
    public void loadData() {
        FileConfiguration config = configManager.getConfig();
        if (!config.contains("cropblocks")) { return; }
        ConfigurationSection cropBlocksSection = config.getConfigurationSection("cropblocks");
        for (String c : cropBlocksSection.getKeys(false)) {
            CropBlock cropBlock = (CropBlock) cropBlocksSection.get(c);
            this.cropBlocks.add(cropBlock);
        }
    }
    
    public Collection<CropBlock> getCropBlocks() {
        return new HashSet<>(cropBlocks);
    }
}