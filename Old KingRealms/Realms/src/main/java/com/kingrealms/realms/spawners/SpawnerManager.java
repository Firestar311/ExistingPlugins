package com.kingrealms.realms.spawners;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.ServerMode;
import com.kingrealms.realms.api.events.RealmsAPI;
import com.kingrealms.realms.entities.CustomEntities;
import com.kingrealms.realms.entities.type.ICustomEntity;
import com.kingrealms.realms.items.CustomItemRegistry;
import com.kingrealms.realms.items.type.SpawnerShard;
import com.kingrealms.realms.loot.Loot;
import com.kingrealms.realms.profile.RealmProfile;
import com.kingrealms.realms.skills.SkillType;
import com.kingrealms.realms.skills.slayer.SlayerLevel;
import com.kingrealms.realms.skills.slayer.SlayerSkill;
import com.kingrealms.realms.storage.StorageManager;
import com.starmediadev.lib.collection.IncrementalMap;
import com.starmediadev.lib.config.ConfigManager;
import com.starmediadev.lib.items.NBTWrapper;
import com.starmediadev.lib.util.EntityNames;
import com.starmediadev.lib.util.Utils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class SpawnerManager implements Listener {
    
    private final ConfigManager configManager = StorageManager.spawnersConfig;
    private final Set<MobStack> mobStacks = new HashSet<>();
    private final Realms plugin = Realms.getInstance();
    private final IncrementalMap<CustomSpawner> spawners = new IncrementalMap<>();
    private SlayerSkill slayerSkill = Realms.getInstance().getSkillManager().getSlayerSkill();
    
    public SpawnerManager() {
        configManager.setup();
        Realms.getInstance().getSeason().addListener(this);
        
        new BukkitRunnable() {
            public void run() {
                if (!Realms.getInstance().getSeason().isActive()) { return; }
                Iterator<CustomSpawner> iterator = spawners.values().iterator();
                while (iterator.hasNext()) {
                    CustomSpawner spawner = iterator.next();
                    if (System.currentTimeMillis() < spawner.getNextSpawn()) { continue; }
                    BlockState state = spawner.getLocation().getWorld().getBlockAt(spawner.getLocation()).getState();
                    if (!(state instanceof CreatureSpawner)) {
                        iterator.remove();
                    }
                    Collection<Entity> nearbyEntities = spawner.getLocation().getWorld().getNearbyEntities(spawner.getLocation(), 16, 16, 16);
                    
                    boolean playerNearby = false;
                    for (Entity entity : nearbyEntities) {
                        if (entity instanceof Player) {
                            playerNearby = true;
                            break;
                        }
                    }
                    
                    if (playerNearby) {
                        if (!spawner.getSpawnAttempt().get()) {
                            spawner.spawnMobs();
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 20L, 10L);
    }
    
    public void addMobStack(MobStack mobStack) {
        this.mobStacks.add(mobStack);
    }
    
    public void saveData() {
        this.configManager.getConfig().set("spawners", null);
        this.configManager.getConfig().set("mobstacks", null);
        
        for (CustomSpawner spawner : this.spawners.values()) {
            this.configManager.getConfig().set("spawners.spawner" + spawner.getId(), spawner);
        }
        
        int counter = 0;
        for (MobStack mobStack : getMobStacks()) {
            try {
                this.configManager.getConfig().set("mobstacks.stack" + counter, mobStack);
                counter++;
            } catch (Exception e) {}
        }
        
        this.configManager.saveConfig();
    }
    
    public Set<MobStack> getMobStacks() {
        return new HashSet<>(mobStacks);
    }
    
    public Set<CustomSpawner> getSpawners() {
        return new HashSet<>(spawners.values());
    }
    
    public void loadData() {
        ConfigurationSection spawnersSection = this.configManager.getConfig().getConfigurationSection("spawners");
        if (spawnersSection != null) {
            for (String s : spawnersSection.getKeys(false)) {
                CustomSpawner spawner = (CustomSpawner) spawnersSection.get(s);
                if (spawner.getId() == -1) {
                    spawner.setId(Integer.parseInt(s.replace("spawner", "")));
                }
                this.spawners.put(spawner.getId(), spawner);
            }
        }
        
        ConfigurationSection mobStacksSection = this.configManager.getConfig().getConfigurationSection("mobstacks");
        if (mobStacksSection != null) {
            for (String s : mobStacksSection.getKeys(false)) {
                MobStack stack = (MobStack) mobStacksSection.get(s);
                this.mobStacks.add(stack);
            }
        }
    }
    
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player) {
            RealmProfile profile = RealmsAPI.getProfile(e.getDamager());
            MobStack mobStack = getMobStack(e.getEntity().getUniqueId());
            if (mobStack == null) { return; }
            SlayerLevel mobLevel = slayerSkill.getLevel(e.getEntityType());
            if (profile.getTotalExperience() < mobLevel.getTotalXpNeeded() && Realms.getInstance().getServerMode() != ServerMode.DEVELOPMENT) {
                profile.sendMessage("&cYou need to be Slayer level " + mobLevel.getLevel() + " to attack that type of mob from a spawner.");
                e.setCancelled(true);
                return;
            }
        }
    
        e.getEntity().setVelocity(new Vector());
        new BukkitRunnable() {
            public void run() {
                e.getEntity().setVelocity(new Vector());
            }
        }.runTaskLater(Realms.getInstance(), 1L);
    }
    
    public MobStack getMobStack(UUID entityId) {
        for (MobStack mobStack : this.mobStacks) {
            if (mobStack.getEntityId().equals(entityId)) {
                return mobStack;
            }
        }
        
        return null;
    }
    
    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        MobStack mobStack = getMobStack(e.getEntity().getUniqueId());
        if (e.getEntity().getKiller() != null) {
            RealmProfile profile = plugin.getProfileManager().getProfile(e.getEntity().getKiller());
            SlayerLevel mobLevel = slayerSkill.getLevel(e.getEntityType());
    
            if (mobStack == null) {
                profile.addSkillExperience(SkillType.SLAYER, 0.5);
                SpawnerShard shard = CustomItemRegistry.SPAWNER_SHARDS.getItem(e.getEntityType());
                if (shard != null) {
                    int v = new Random().nextInt(1000);
                    if (v < 10) {
                        profile.getInventory().addItem(shard.getItemStack());
                    }
                }
                
                return;
            }
            
            final Location location = e.getEntity().getLocation().clone();
            
            if (profile.getTotalExperience() < mobLevel.getTotalXpNeeded() && Realms.getInstance().getServerMode() != ServerMode.DEVELOPMENT) {
                profile.sendMessage("&cYou need to be Slayer level " + mobLevel.getLevel() + " to kill that type of mob from a spawner.");
            } else {
                int sharpnessLevel = profile.getBukkitPlayer().getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.DAMAGE_ALL);
                int decrementAmount = 1;
                if (sharpnessLevel > 0) {
                    decrementAmount = 2 * sharpnessLevel;
                }
                
                if (mobStack.getCount() < decrementAmount) {
                    decrementAmount = mobStack.getCount();
                }
                
                mobStack.decrement(decrementAmount);
                
                ICustomEntity customEntity = CustomEntities.getCustomEntity(e.getEntity());
                e.getDrops().clear();
                //TODO Until custom enchant system is implemented e.setDroppedExp(0);
                for (int i = 0; i < decrementAmount; i++) {
                    List<Loot> drops = customEntity.getDropTable().generateLoot();
                    for (Loot drop : drops) {
                        profile.getInventory().addItem(drop.getItemStack());
                    }
                    
                    if (profile.getBukkitPlayer().getGameMode() != GameMode.CREATIVE) {
                        profile.addSkillExperience(SkillType.SLAYER, mobLevel.getXpPerHarvest());
                        profile.addKilledMob(e.getEntity().getType());
                    }
                }
                
                if (mobStack.getCount() == 0) {
                    this.removeMobStack(e.getEntity().getUniqueId());
                    return;
                }
            }
    
            new BukkitRunnable() {
                @Override
                public void run() {
                    LivingEntity entity = (LivingEntity) CustomEntities.REGISTRY.get(e.getEntityType()).spawn(location);
                    ICustomEntity customEntity = CustomEntities.getCustomEntity(entity);
                    customEntity.setCustom(true);
                    mobStack.setEntityId(entity.getUniqueId());
                    mobStack.updateName(entity);
                }
            }.runTaskLater(Realms.getInstance(), 1L);
        } else {
            if (mobStack == null) { return; }
            e.getDrops().clear();
            if (mobStack.getCount() == 0) {
                this.removeMobStack(e.getEntity().getUniqueId());
            }
        }
    }
    
    public void removeMobStack(UUID uniqueId) {
        this.mobStacks.removeIf(stack -> stack.getEntityId().equals(uniqueId));
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        BlockState state = e.getBlock().getState();
        if (state instanceof CreatureSpawner) {
            CreatureSpawner creatureSpawner = (CreatureSpawner) state;
            CustomSpawner spawner = this.getSpawner(e.getBlock().getLocation());
            e.setExpToDrop(0);
            if (spawner != null) {
                this.spawners.remove(spawner.getId());
                e.getPlayer().sendMessage(Utils.color("&eRemoved &b" + EntityNames.getName(creatureSpawner.getSpawnedType()) + " &espawner."));
                if (e.getPlayer().getGameMode() == GameMode.SURVIVAL) {
                    ItemStack itemStack = spawner.getItemStack();
                    if (itemStack == null) {
                        e.getPlayer().sendMessage(Utils.color("&cThere was an error giving you the item for the spawner you just broke."));
                        return;
                    }
                    
                    e.getPlayer().getInventory().addItem(itemStack);
                }
            } else {
                if (e.getPlayer().getGameMode() == GameMode.SURVIVAL) {
                    SpawnerShard shard = CustomItemRegistry.SPAWNER_SHARDS.getItem(creatureSpawner.getSpawnedType());
                    ItemStack itemStack = shard.getItemStack(5);
                    
                    if (itemStack == null) {
                        e.getPlayer().sendMessage("&cCould not give you the spawner shards for the broken spawner.");
                        e.setCancelled(true);
                        return;
                    }
                    
                    e.getPlayer().getInventory().addItem(itemStack);
                }
            }
        }
    }
    
    public CustomSpawner getSpawner(Location location) {
        for (CustomSpawner spawner : this.spawners.values()) {
            if (spawner.getLocation().equals(location)) {
                return spawner;
            }
        }
        
        return null;
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        BlockState state = e.getBlock().getState();
        if (state instanceof CreatureSpawner) {
            RealmProfile profile = RealmsAPI.getProfile(e.getPlayer());
            String spawnerType, spawnBlock = null;
            try {
                spawnerType = NBTWrapper.getNBTString(e.getItemInHand(), "spawnertype");
            } catch (Exception ex) {
                return;
            }
            
            try {
                spawnBlock = NBTWrapper.getNBTString(e.getItemInHand(), "spawnblock");
            } catch (Exception ex) {}
            
            EntityType entityType;
            try {
                entityType = EntityType.valueOf(spawnerType);
            } catch (IllegalArgumentException ex) {
                e.setCancelled(true);
                profile.sendMessage("&cCould not get the entity type for that spawner. This is a bugged item and you should get rid of it.");
                return;
            }
            
            if (!profile.getStaffMode().isActive()) {
                SlayerLevel level = slayerSkill.getLevel(entityType);
                if (profile.getTotalExperience() < level.getTotalXpNeeded()) {
                    if (Realms.getInstance().getServerMode() != ServerMode.DEVELOPMENT) {
                        e.setCancelled(true);
                        profile.sendMessage("&cYou must be Slayer level " + level.getLevel() + " to place that type of spawner.");
                        return;
                    }
                }
            }
            
            if (StringUtils.isEmpty(spawnerType)) { return; }
            
            CreatureSpawner spawner = (CreatureSpawner) state;
            spawner.setSpawnedType(entityType);
            spawner.setDelay(Integer.MAX_VALUE);
            spawner.update();
            CustomSpawner customSpawner = new CustomSpawner(e.getBlock().getLocation(), entityType);
            customSpawner.setOwner(profile.getUniqueId());
            customSpawner.setDate(System.currentTimeMillis());
            if (!StringUtils.isEmpty(spawnBlock)) {
                try {
                    Material block = Material.valueOf(spawnBlock);
                    customSpawner.setSpawnBlock(block);
                } catch (Exception ex) { }
            }
            //customSpawner.setNextSpawn(TimeUnit.SECONDS.toMillis(2));
            int id = this.spawners.add(customSpawner);
            customSpawner.setId(id);
            e.getPlayer().sendMessage(Utils.color("&ePlaced &b" + EntityNames.getName(spawner.getSpawnedType()) + " &espawner."));
        }
    }
    
    @EventHandler
    public void onSpawnerSpawn(SpawnerSpawnEvent e) {
        CustomSpawner spawner = plugin.getSpawnerManager().getSpawner(e.getSpawner().getLocation());
        if (spawner != null) {
            e.setCancelled(true);
        }
    }
}