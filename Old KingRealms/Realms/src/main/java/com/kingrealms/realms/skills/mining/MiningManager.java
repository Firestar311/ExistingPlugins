package com.kingrealms.realms.skills.mining;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.ServerMode;
import com.kingrealms.realms.api.events.HammerMineEvent;
import com.kingrealms.realms.api.events.RealmsAPI;
import com.kingrealms.realms.items.CustomItemRegistry;
import com.kingrealms.realms.items.type.*;
import com.kingrealms.realms.loot.Loot;
import com.kingrealms.realms.profile.RealmProfile;
import com.kingrealms.realms.skills.SkillType;
import com.kingrealms.realms.skills.farming.CropType;
import com.kingrealms.realms.storage.StorageManager;
import com.kingrealms.realms.tasks.BlockUpdate;
import com.starmediadev.lib.config.ConfigManager;
import com.starmediadev.lib.util.Utils;
import com.starmediadev.lib.workload.WorkloadThread;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.Map.Entry;

public class MiningManager implements Listener {
    
    public static final Material COOLDOWN_MATERIAL = Material.DEAD_BRAIN_CORAL_BLOCK;
    private final Map<Location, MysticalBlock> mysticalBlocks = new HashMap<>();
    private ConfigManager configManager = StorageManager.miningConfig;
    private Map<UUID, Set<Location>> placedSkillBlocks = new HashMap<>();
    private Realms plugin = Realms.getInstance();
    private WorkloadThread workload = new WorkloadThread();
    private MiningSkill miningSkill = Realms.getInstance().getSkillManager().getMiningSkill();
    
    public MiningManager() {
        configManager.setup();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        workload.start(Realms.getInstance());
        
        new BukkitRunnable() {
            public void run() {
                if (!getMysticalBlocks().isEmpty()) {
                    getMysticalBlocks().forEach(b -> {
                        Block block = b.getLocation().getWorld().getBlockAt(b.getLocation());
                        if (block.getChunk().isLoaded()) {
                            if (block.getType() != b.getMaterial()) {
                                if (b.isCooldownExpired()) {
                                    workload.addLoad(new BlockUpdate(block.getLocation(), b.getMaterial()));
                                }
                            }
                        }
                    });
                }
            }
        }.runTaskTimerAsynchronously(plugin, 20L, 10L);
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent e) {
        RealmProfile profile = RealmsAPI.getProfile(e.getPlayer());
        MysticalResource resource = MysticalResource.getMysticalResource(e.getItemInHand());
        if (resource == null) {
            this.addSkillBlockPlaced(profile, e.getBlock().getLocation());
            return;
        }
        Block block = e.getBlockPlaced();
        if (profile.hasPermission("realms.admin.place.mysticalresource") && profile.getStaffMode().isActive()) {
            this.mysticalBlocks.put(e.getBlockPlaced().getLocation(), new MysticalBlock(resource.getResourceType(), block.getLocation(), profile.getUniqueId(), System.currentTimeMillis()));
            plugin.getProfileManager().getProfile(e.getPlayer()).sendMessage("&iPlaced &j" + resource.getDisplayName());
            return;
        }
        
        if (!plugin.getSeason().isActive()) {
            profile.sendMessage("&cThe season is not active, you cannot place that.");
            return;
        }
        
        MiningLevel miningLevel = miningSkill.getLevel(e.getItemInHand().getType());
        if (profile.getTotalExperience() < miningLevel.getTotalXpNeeded()) {
            if (plugin.getServerMode() != ServerMode.DEVELOPMENT) {
                e.setCancelled(true);
                profile.sendMessage("&cYou must be Mining level " + miningLevel.getLevel() + " to place that type of Mystical Resource.");
                return;
            }
        }
        
        MysticalBlock mysticalBlock = new MysticalBlock(resource.getResourceType(), block.getLocation(), profile.getUniqueId(), System.currentTimeMillis());
        
        this.mysticalBlocks.put(e.getBlockPlaced().getLocation(), mysticalBlock);
        plugin.getProfileManager().getProfile(e.getPlayer()).sendMessage("&gPlaced &h" + resource.getDisplayName());
    }
    
    private void addSkillBlockPlaced(RealmProfile profile, Location location) {
        addSkillBlockPlaced(profile.getUniqueId(), location);
    }
    
    private void addSkillBlockPlaced(UUID uuid, Location location) {
        if (this.placedSkillBlocks.containsKey(uuid)) {
            this.placedSkillBlocks.get(uuid).add(location);
        } else {
            this.placedSkillBlocks.put(uuid, new HashSet<>(Set.of(location)));
        }
    }
    
    private void removePlacedSkillBlock(RealmProfile profile, Location location) {
        if (this.placedSkillBlocks.containsKey(profile.getUniqueId())) {
            this.placedSkillBlocks.get(profile.getUniqueId()).remove(location);
        }
    }
    
    @EventHandler
    public void onHammerMine(HammerMineEvent e) {
        if (e.getBlocks() == null) return;
        if (e.getBlocks().isEmpty()) return;
        RealmProfile player = e.getProfile();
        for (Block block : e.getBlocks()) {
            MysticalBlock mysticalBlock = getMysticalBlock(block.getLocation());
            if (mysticalBlock != null) {
                handleMysticalBlock(player, block, mysticalBlock, e);
            }
        }
    }
    
    private void handleMysticalBlock(RealmProfile player, Block block, MysticalBlock mysticalBlock, Cancellable e) {
        boolean miningLevelHigher, cooldownExpired, serverClaimContains, hasPermissionToBreak, playerCreative, seasonActive;
    
        MiningLevel miningLevel = miningSkill.getLevel(mysticalBlock.getMaterial());
        miningLevelHigher = player.getTotalExperience() > miningLevel.getTotalXpNeeded();
        cooldownExpired = mysticalBlock.isCooldownExpired();
        serverClaimContains = plugin.getSpawn().contains(block.getLocation()) || plugin.getWarzone().contains(block.getLocation());
        hasPermissionToBreak = player.hasPermission("realms.admin.break.mysticalresource");
        playerCreative = player.getBukkitPlayer().getGameMode().equals(GameMode.CREATIVE);
        seasonActive = plugin.getSeason().isActive();
    
        if (seasonActive) {
            if (!miningLevelHigher && !serverClaimContains && !player.getStaffMode().isActive() && !hasPermissionToBreak) {
                if (plugin.getServerMode() != ServerMode.DEVELOPMENT) {
                    e.setCancelled(true);
                    player.sendMessage("&cYou must be Mining level " + miningLevel.getLevel() + " to break that Mystical Resource");
                    return;
                }
            }
        }
    
        if (player.getBukkitPlayer().isSneaking()) {
            if (serverClaimContains && !hasPermissionToBreak) {
                e.setCancelled(true);
                player.sendMessage("&cYou do not have permission to break that.");
                return;
            }
        
            if (!cooldownExpired && !player.getStaffMode().isActive()) {
                player.sendMessage("&cReady to mine in " + Utils.formatTime(mysticalBlock.getResourceType().getCooldown().toMilliseconds() - System.currentTimeMillis()));
                e.setCancelled(true);
                return;
            }
        
            this.mysticalBlocks.remove(block.getLocation());
            player.sendMessage("&gRemoved &h" + Utils.capitalizeEveryWord(mysticalBlock.getResourceType() + " Mystical Resource."));
            if (!playerCreative) {
                player.getInventory().addItem(CustomItemRegistry.MYSTICAL_RESOURCES.getItem(mysticalBlock.getResourceType()).getItemStack());
            }
        
            return;
        }
    
        if (!seasonActive) {
            player.sendMessage("&cYou cannot break that as the Season is not yet active");
            e.setCancelled(true);
            return;
        }
    
        if (!cooldownExpired) {
            player.sendMessage("&cReady to mine in " + Utils.formatTime(mysticalBlock.getResourceType().getCooldown().toMilliseconds() - System.currentTimeMillis()));
            e.setCancelled(true);
            return;
        }
    
        mysticalBlock.setLastMined(System.currentTimeMillis());
        List<Loot> drops = mysticalBlock.getLootTable().generateLoot();
        for (Loot loot : drops) {
            player.getInventory().addItem(loot.getItemStack());
        }
    
        miningSkill.getLevels().values().forEach(level -> {
            if (level instanceof MiningLevel) {
                MiningLevel mLevel = (MiningLevel) level;
                if (mLevel.getMaterial() == block.getType()) {
                    player.addSkillExperience(SkillType.MINING, level.getXpPerHarvest());
                }
            }
        });
    
        new BukkitRunnable() {
            @Override
            public void run() {
                block.setType(COOLDOWN_MATERIAL);
            }
        }.runTaskLater(plugin, 1L);
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent e) {
        Block block = e.getBlock();
        RealmProfile player = plugin.getProfileManager().getProfile(e.getPlayer());
        if (block.getType() == COOLDOWN_MATERIAL) e.setCancelled(true);
        for (CropType type : CropType.values()) {
            if (block.getType() == type.getCropMaterial()) {
                return;
            }
        }
    
        if (!this.mysticalBlocks.containsKey(block.getLocation())) {
            if (block.getType() == Material.SPAWNER) { return; }
            if (this.containsPlacedSkillBlock(player, e.getBlock().getLocation())) { return; }
            if (!plugin.getSeason().isActive()) { return; }
            
            ResourceType resourceType = ResourceType.getType(e.getPlayer().getInventory().getItemInMainHand(), block.getType());
            if (resourceType == null) return;
            
            MysticalSliver sliver = CustomItemRegistry.MYSTICAL_SLIVERS.getItem(resourceType);
            if (sliver == null) return;
            List<Loot> loot = sliver.getLootTable().generateLoot();
            if (!loot.isEmpty()) {
                for (Loot l : loot) {
                    e.getPlayer().getInventory().addItem(l.getItemStack());
                }
            }
    
            if (!e.getPlayer().getGameMode().equals(GameMode.CREATIVE)) {
                player.addSkillExperience(SkillType.MINING, 0.5);
            }
            return;
        }
    
        if (Hammer.isHammer(e.getPlayer().getInventory().getItemInMainHand())) return;
        if (Excavator.isExcavator(e.getPlayer().getInventory().getItemInMainHand())) return;
    
        MysticalBlock mysticalBlock = this.mysticalBlocks.get(block.getLocation());
        handleMysticalBlock(player, block, mysticalBlock, e);
        e.setDropItems(false);
    }
    
    private boolean containsPlacedSkillBlock(RealmProfile profile, Location location) {
        if (!this.placedSkillBlocks.containsKey(profile.getUniqueId())) { return false; }
        return this.placedSkillBlocks.get(profile.getUniqueId()).contains(location);
    }
    
    public void saveData() {
        configManager.getConfig().set("mysticalblocks", null);
        configManager.saveConfig();
        int c = 0;
        for (MysticalBlock block : mysticalBlocks.values()) {
            configManager.getConfig().set("mysticalblocks." + c++, block);
        }
        
        configManager.getConfig().set("placed", null);
        
        if (!this.placedSkillBlocks.isEmpty()) {
            for (Entry<UUID, Set<Location>> entry : this.placedSkillBlocks.entrySet()) {
                if (!entry.getValue().isEmpty()) {
                    int i = 0;
                    for (Location location : entry.getValue()) {
                        configManager.getConfig().set("placed." + entry.getKey().toString() + "." + i, location);
                        i++;
                    }
                }
            }
        }
        
        configManager.saveConfig();
    }
    
    public void loadData() {
        FileConfiguration config = configManager.getConfig();
        if (config.contains("mysticalblocks")) {
            ConfigurationSection mysticalSection = config.getConfigurationSection("mysticalblocks");
            for (String c : mysticalSection.getKeys(false)) {
                MysticalBlock block = (MysticalBlock) mysticalSection.get(c);
                this.mysticalBlocks.put(block.getLocation(), block);
            }
        }
        
        if (config.contains("placed")) {
            ConfigurationSection placedSection = config.getConfigurationSection("placed");
            for (String u : placedSection.getKeys(false)) {
                UUID uuid = UUID.fromString(u);
                ConfigurationSection locSection = placedSection.getConfigurationSection(u);
                if (locSection != null) {
                    for (String l : locSection.getKeys(false)) {
                        addSkillBlockPlaced(uuid, locSection.getLocation(l));
                    }
                }
            }
        }
    }
    
    public MysticalBlock getMysticalBlock(Location location) {
        return this.mysticalBlocks.get(location);
    }
    
    public Collection<MysticalBlock> getMysticalBlocks() {
        return new HashSet<>(this.mysticalBlocks.values());
    }
}