package com.kingrealms.realms.spawners;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.entities.CustomEntities;
import com.kingrealms.realms.entities.type.ICustomEntity;
import com.kingrealms.realms.items.CustomItemRegistry;
import com.starmediadev.lib.pagination.IElement;
import com.starmediadev.lib.region.Cuboid;
import com.starmediadev.lib.util.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@SerializableAs("CustomSpawner")
public class CustomSpawner implements ConfigurationSerializable, IElement, CommandViewable {
    
    public static final Set<Material> ALLOWED_BLOCKS = Set.of(Material.STONE, Material.COBBLESTONE, Material.DIRT, Material.GRASS_BLOCK); //TODO Add more as time goes on
    public static final long MIN_SPAWNER_TIME = Realms.getInstance().getServerMode().getMinSpawnerTime(), MAX_SPAWNER_TIME = Realms.getInstance().getServerMode().getMaxSpawnerTime();
    
    private int id = -1;
    private int amount = 1; //mysql
    private EntityType entityType; //mysql
    private long lastSpawn, nextSpawn = System.currentTimeMillis() + MAX_SPAWNER_TIME, date; //mysql
    private Location location; //mysql
    private Material spawnBlock; //mysql
    private AtomicBoolean spawnAttempt = new AtomicBoolean(false);
    private UUID owner;
    
    @Override
    public Map<String, String> getDisplayMap() {
        return new LinkedHashMap<>() {{
            put("ID", id + "");
            put("Amount", amount + "");
            put("Type", Utils.capitalizeEveryWord(entityType.name()));
            put("Last Spawn", Utils.formatTime(System.currentTimeMillis() - lastSpawn) + " ago");
            put("Next Spawn", Utils.formatTime(nextSpawn - System.currentTimeMillis()));
            put("Spawn Block", MaterialNames.getName(spawnBlock));
            put("Location", "(" + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + ")");
            if (date != 0) {
                put("Date Placed", Constants.DATE_FORMAT.format(new Date(date)));
            }
            if (owner != null) {
                put("Owner", Realms.getInstance().getProfileManager().getProfile(owner).getName());
            }
        }};
    }
    
    @Override
    public String formatLine(String... args) {
        int x = location.getBlockX(), y = location.getBlockY(), z = location.getBlockZ();
        return " &8- &a" + EntityNames.getName(entityType) + " &6spawner at &a(" + x + ", " + y + ", " + z + ") &6has a stack size of &a" + amount + " &6and spawns in &a" + Utils.formatTime(this.nextSpawn - System.currentTimeMillis());
    }
    
    public CustomSpawner(Location location, EntityType entityType) {
        this.location = location;
        this.entityType = entityType;
    }
    
    public CustomSpawner(Location location, EntityType type, int amount) {
        this.location = location;
        this.amount = amount;
        this.entityType = type;
    }
    
    protected CustomSpawner(int amount, EntityType entityType, Location location, Material spawnBlock, long lastSpawn, long nextSpawn) {
        this.amount = amount;
        this.entityType = entityType;
        this.location = location;
        this.spawnBlock = spawnBlock;
        this.lastSpawn = lastSpawn;
        this.nextSpawn = nextSpawn;
    }
    
    public CustomSpawner(Map<String, Object> serialized) {
        this.location = (Location) serialized.get("location");
        this.amount = Integer.parseInt((String) serialized.get("amount"));
        if (serialized.containsKey("type")) {
            this.entityType = EntityType.valueOf((String) serialized.get("type"));
        }
        
        if (serialized.containsKey("spawnBlock")) {
            this.spawnBlock = Material.valueOf((String) serialized.get("spawnBlock"));
        }
        
        this.lastSpawn = Long.parseLong((String) serialized.get("lastSpawn"));
        this.nextSpawn = Long.parseLong((String) serialized.get("lastSpawn"));
        if (serialized.containsKey("id")) {
            this.id = Integer.parseInt((String) serialized.get("id"));
        }
        if (serialized.containsKey("owner")) {
            this.owner = UUID.fromString((String) serialized.get("owner"));
        }
        if (serialized.containsKey("date")) {
            this.date = Long.parseLong((String) serialized.get("date"));
        }
    }
    
    public void setOwner(UUID owner) {
        this.owner = owner;
    }
    
    public UUID getOwner() {
        return owner;
    }
    
    public long getDate() {
        return date;
    }
    
    public void setDate(long date) {
        this.date = date;
    }
    
    public Location getLocation() {
        return location;
    }
    
    public void setLocation(Location location) {
        this.location = location;
    }
    
    public int getAmount() {
        return amount;
    }
    
    public void setAmount(int amount) {
        this.amount = amount;
    }
    
    public Material getSpawnBlock() {
        return spawnBlock;
    }
    
    public void setSpawnBlock(Material spawnBlock) {
        this.spawnBlock = spawnBlock;
    }
    
    public ItemStack getItemStack() {
        return CustomItemRegistry.SPAWNERS.getItem(this.entityType).getItemStack(spawnBlock, this.amount);
    }
    
    public void spawnMobs() {
        if (!Realms.getInstance().getSeason().isActive()) { return; }
        if (this.location.getBlock().getState() instanceof CreatureSpawner) {
            if (Realms.getInstance().getServerMode().getMinSpawnerTime() != 0) {
                this.spawnAttempt.set(true);
                int mD = 3;
                EntityType type = getEntityType();
                BoundingBox checkArea = BoundingBox.of(location.clone().add(mD * 3, mD * 3, mD * 3), location.clone().subtract(mD * 3, mD * 3, mD * 3));
                Collection<Entity> nearbyEntities = location.getWorld().getNearbyEntities(checkArea, entity -> entity.getType().equals(type));
                new BukkitRunnable() {
                    public void run() {
                        MobStack mobStack = null;
                        LivingEntity entity = null;
            
                        stacks:
                        for (MobStack stack : Realms.getInstance().getSpawnerManager().getMobStacks()) {
                            for (Entity ne : nearbyEntities) {
                                if (ne.getUniqueId().equals(stack.getEntityId())) {
                                    mobStack = stack;
                                    entity = (LivingEntity) ne;
                                    break stacks;
                                }
                            }
                        }
            
                        if (mobStack == null) {
                            Random random = new Random();
                
                            Set<Location> spawnableLocations = new HashSet<>();
                            Cuboid cuboid = new Cuboid(location.clone().add(mD, mD, mD), location.clone().subtract(mD, mD, mD));
                            for (Iterator<Block> it = cuboid.getBlockList(true); it.hasNext(); ) {
                                Block block = it.next();
                                int amountOfAir = 0;
                                for (int y = block.getY() + 1; y < block.getY() + 5; y++) {
                                    Location clone = block.getLocation().clone();
                                    clone.setY(y);
                                    if (clone.getBlock().getType() == Material.AIR || clone.getBlock().getType() == Material.WATER) {
                                        amountOfAir++;
                                    }
                                }
                    
                                if (amountOfAir > 2) {
                                    spawnableLocations.add(block.getLocation());
                                }
                            }
                
                            List<Location> validLocations = new LinkedList<>();
                            if (spawnBlock != null) {
                                for (Location location : spawnableLocations) {
                                    if (location.getBlock().getType() == spawnBlock) {
                                        validLocations.add(location);
                                    }
                                }
                            } else {
                                validLocations.addAll(spawnableLocations);
                            }
                
                            if (validLocations.isEmpty()) return;
                            Collections.shuffle(validLocations);
                
                            Location spawnLocation = validLocations.get(random.nextInt(validLocations.size())).add(0.5, 1, 0.5);
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    LivingEntity entity = (LivingEntity) CustomEntities.REGISTRY.get(type).spawn(spawnLocation);
                                    ICustomEntity customEntity = CustomEntities.getCustomEntity(entity);
                                    if (customEntity != null) {
                                        customEntity.setCustom(true);
                                    }
                        
                                    MobStack mobStack = new MobStack(entity.getUniqueId());
                                    Realms.getInstance().getSpawnerManager().addMobStack(mobStack);
                                    entity.setCustomNameVisible(true);
                                    mobStack.updateName(entity);
                                }
                            }.runTaskLater(Realms.getInstance(), 1L);
                        } else {
                            mobStack.increment(amount);
                            mobStack.updateName(entity);
                        }
            
                        lastSpawn = System.currentTimeMillis();
                        int minSpawn = (int) Realms.getInstance().getServerMode().getMinSpawnerTime();
                        int maxSpawn = (int) Realms.getInstance().getServerMode().getMaxSpawnerTime();
                        int nextSpawnSeconds = new Random().nextInt((maxSpawn - minSpawn)) + minSpawn;
                        nextSpawn = lastSpawn + TimeUnit.SECONDS.toMillis(nextSpawnSeconds);
                        //nextSpawn = lastSpawn + TimeUnit.SECONDS.toMillis(1);
                        spawnAttempt.set(false);
                    }
                }.runTaskAsynchronously(Realms.getInstance());
            }
        }
    }
    
    public EntityType getEntityType() {
        if (this.entityType == null) {
            CreatureSpawner creatureSpawner = (CreatureSpawner) this.location.getBlock().getState();
            this.entityType = creatureSpawner.getSpawnedType();
        }
        return entityType;
    }
    
    @Override
    public Map<String, Object> serialize() {
        return new HashMap<>() {{
            put("location", location);
            put("amount", amount + "");
            if (entityType != null) {
                put("type", entityType.name());
            }
            if (spawnBlock != null) {
                put("spawnBlock", spawnBlock.name());
            }
            
            put("lastSpawn", lastSpawn + "");
            put("nextSpawn", nextSpawn + "");
            put("id", id + "");
            if (owner != null) {
                put("owner", owner.toString());
            }
            put("date", date + "");
        }};
    }
    
    public long getLastSpawn() {
        return lastSpawn;
    }
    
    public void setLastSpawn(long lastSpawn) {
        this.lastSpawn = lastSpawn;
    }
    
    public long getNextSpawn() {
        return nextSpawn;
    }
    
    public void setNextSpawn(long time) {
        this.nextSpawn = time;
    }
    
    public AtomicBoolean getSpawnAttempt() {
        return spawnAttempt;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
}