package com.stardevmc.titangames.arenas;

import com.firestar311.lib.builder.ItemBuilder;
import com.firestar311.lib.pagination.IElement;
import com.firestar311.lib.region.Cuboid;
import com.firestar311.lib.util.Range;
import com.firestar311.lib.util.Utils;
import com.stardevmc.titangames.TitanGames;
import com.stardevmc.titangames.arenas.chests.ArenaChest;
import com.stardevmc.titangames.arenas.chests.ChestTier;
import com.stardevmc.titangames.kits.Kit;
import com.stardevmc.titangames.kits.KitGiveTimer;
import com.stardevmc.titangames.signs.LobbySign;
import com.stardevmc.titangames.signs.SignManager;
import com.stardevmc.titangames.util.Messages;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.Map.Entry;

public class Arena implements IElement {
    
    public enum ArenaState {
        SETUP, WAITING, COUNTDOWN, IN_GAME, ENDING
    }
    
    private String name;
    private ArenaState state;
    private World world;
    private List<Player> tributes;
    private Map<Player, Kit> kitsInUse = new HashMap<>();
    private SortedMap<Integer, Spawn> spawnpoints;
    private List<ArenaChest> chests;
    private Cuboid region;
    
    public Arena(String name, Cuboid region) {
        this.name = name;
        this.region = region;
        this.world = region.getWorld();
        this.tributes = new ArrayList<>();
        this.spawnpoints = new TreeMap<>();
        this.chests = new ArrayList<>();
        this.state = ArenaState.SETUP;
        
        new BukkitRunnable() {
            public void run() {
                if (state.equals(ArenaState.IN_GAME)) {
                    for (Player player : tributes) {
                        if (!region.contains(player)) {
                            removePlayer(player);
                            sendMessageToAll(Messages.KILLED_BY_LEAVING(player));
                        }
                    }
                }
            }
        }.runTaskTimer(TitanGames.getInstance(), 0L, 100L);
    }
    
    public void cleanup() {
        clearPlayers();
        clearSpawns();
        this.kitsInUse.clear();
        clearChests();
    }
    
    public void clearPlayers() {
        clearPlayers(null);
    }
    
    public void clearPlayers(String message) {
        if (!tributes.isEmpty()) {
            for (Player player : this.tributes) {
                player.getInventory().clear();
                player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
                if (!StringUtils.isEmpty(message)) {
                    player.sendMessage(Utils.color(message));
                }
            }
            
            tributes.clear();
        }
    }
    
    public void clearSpawns() {
        if (!spawnpoints.isEmpty()) {
            for (Spawn spawn : this.spawnpoints.values()) {
                spawn.setPlayer(null);
            }
        }
    }
    
    public void clearChests() {
        if (!chests.isEmpty()) {
            for (ArenaChest arenaChest : this.chests) {
                Block block = world.getBlockAt(arenaChest.getLocation());
                if (block.getState() instanceof Chest) {
                    Chest chest = (Chest) block.getState();
                    chest.getBlockInventory().clear();
                }
            }
        }
    }
    
    public void updateSigns() {
        SignManager signManager = TitanGames.getInstance().getSignManager();
        if (signManager != null) {
            List<LobbySign> signs = signManager.getLobbySigns(this);
            if (!(signs == null || signs.isEmpty())) {
                for (LobbySign sign : signs) {
                    sign.updateSign();
                }
            }
        }
    }
    
    public void forceStart(Player starter) {
        sendMessageToAll(Messages.FORCE_START_ARENA(starter));
        countdown();
    }
    
    public void forceStop(Player stopper) {
        sendMessageToAll(Messages.FORCE_STOP_ARENA(stopper));
        cleanup();
        allowPlayers();
    }
    
    public void setPlayerKit(Player player, Kit kit) {
        this.kitsInUse.put(player, kit);
    }
    
    public Kit getKitInUse(Player player) {
        return this.kitsInUse.get(player);
    }
    
    public void addPlayer(Player player) {
        if (this.spawnpoints.size() == this.tributes.size()) return;
        this.tributes.add(player);
        player.getInventory().clear();
        player.setGameMode(GameMode.SURVIVAL);
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setSaturation(5);
        for (Spawn spawn : this.spawnpoints.values()) {
            if (spawn.getPlayer() == null || !this.tributes.contains(spawn.getPlayer())) {
                spawn.setPlayer(player);
                player.teleport(spawn.getLocation());
                player.getInventory().addItem(ItemBuilder.start(Material.ROTTEN_FLESH).withName(Messages.KIT_SELECTOR_TITLE()).buildItem());
                break;
            }
        }
        
        sendMessageToAll(Messages.JOIN_ARENA(player, this));
        
        int spawnsFilled = 0;
        for (Spawn spawn : this.spawnpoints.values()) {
            if (spawn.getPlayer() != null && this.tributes.contains(spawn.getPlayer())) {
                spawnsFilled++;
            }
        }
        
        if (spawnsFilled == this.spawnpoints.size()) {
            countdown();
        }
    }
    
    public void countdown() {
        this.state = ArenaState.COUNTDOWN;
        updateSigns();
        new Countdown(this, 30, 30, 15, 10, 5, 4, 3, 2, 1, 0).runTaskTimer(TitanGames.getInstance(), 0L, 20L);
    }
    
    public void setup() {
        this.state = ArenaState.SETUP;
        clearPlayers("&cThe arena you were in was changed to setup mode.");
        clearChests();
        clearSpawns();
        updateSigns();
    }
    
    public void allowPlayers() {
        this.state = ArenaState.WAITING;
        updateSigns();
    }
    
    public void start() {
        fillChests();
        for (Player player : this.tributes) {
            player.getInventory().clear();
        }
        this.state = ArenaState.IN_GAME;
        new KitGiveTimer(this).runTaskTimer(TitanGames.getInstance(), 0L, 20L);
        updateSigns();
    }
    
    public void giveKits() {
        for (Player player : this.tributes) {
            Kit kit = this.kitsInUse.get(player);
            if (kit != null) {
                kit.apply(player);
            }
        }
    }
    
    public void removePlayer(Player player) {
        this.tributes.remove(player);
        player.getInventory().clear();
        player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
        for (Spawn spawn : this.spawnpoints.values()) {
            if (spawn.getPlayer() != null) {
                if (spawn.getPlayer().equals(player)) {
                    spawn.setPlayer(null);
                }
            }
        }
        
        if (this.tributes.size() <= 1) {
            if (this.state.equals(ArenaState.IN_GAME)) {
                if (this.tributes.size() == 1) {
                    this.state = ArenaState.ENDING;
                    updateSigns();
                    Bukkit.broadcastMessage(Utils.color(Messages.WIN_ARENA(this.tributes.get(0), this)));
                    this.tributes.get(0).teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
                    this.tributes.get(0).getInventory().clear();
                    updateSigns();
                } else {
                    Bukkit.broadcastMessage(Utils.color(Messages.ARENA_END(this)));
                }
            } else if (this.state.equals(ArenaState.COUNTDOWN)) {
                cleanup();
                allowPlayers();
                updateSigns();
                return;
            }
            
            cleanup();
            allowPlayers();
        }
    }
    
    public boolean hasPlayer(Player player) {
        return this.tributes.contains(player);
    }
    
    public void sendMessageToAll(String message) {
        sendMessageToTributes(message);
    }
    
    public void sendMessageToTributes(String message) {
        for (Player player : this.tributes) {
            player.sendMessage(Utils.color(message));
        }
    }
    
    public int addSpawn(Spawn spawn) {
        if (this.state == ArenaState.SETUP) {
            int id = (this.spawnpoints.isEmpty()) ? 0 : this.spawnpoints.lastKey() + 1;
            this.spawnpoints.put(id, spawn);
            updateSigns();
            return id;
        }
        
        return -1;
    }
    
    public boolean removeSpawn(int id) {
        if (this.state == ArenaState.SETUP) {
            this.spawnpoints.remove(id);
            updateSigns();
            return true;
        }
        
        return false;
    }
    
    public boolean addChest(ArenaChest chest) {
        if (this.state == ArenaState.SETUP && !this.chests.contains(chest)) {
            this.chests.add(chest);
            return true;
        }
        
        return false;
    }
    
    public boolean removeChest(ArenaChest aChest) {
        if (this.state == ArenaState.SETUP) {
            return false;
        }
        Chest chest = (Chest) aChest.getLocation().getBlock().getState();
        chest.getBlockInventory().clear();
        return this.chests.remove(aChest);
    }
    
    public boolean removeChest(Location location) {
        ArenaChest chest = getChest(location);
        if (chest != null) {
            return removeChest(chest);
        }
        return false;
    }
    
    public ArenaChest getChest(Location location) {
        for (ArenaChest chest : this.chests) {
            if (chest.getLocation().equals(location)) {
                return chest;
            }
        }
        
        return null;
    }
    
    public boolean hasChest(Location location) {
        for (ArenaChest chest : this.chests) {
            if (chest.getLocation().equals(location)) {
                return true;
            }
        }
        
        return false;
    }
    
    public void fillChests() {
        for (ArenaChest arenaChest : this.chests) {
            ChestTier chestTier = TitanGames.getInstance().getChestTierManger().getChestTeir(arenaChest.getChestTeir());
            List<Range<ItemStack>> ranges = new ArrayList<>();
            int lastChance = 1;
            for (Entry<ItemStack, Integer> entry : chestTier.getItems().entrySet()) {
                Range<ItemStack> range = new Range<>(lastChance, lastChance + entry.getValue(), entry.getKey());
                ranges.add(range);
                lastChance += entry.getValue() + 1;
            }
            
            Random r = new Random();
            int maxItems = r.nextInt(chestTier.getMaxItems()) + 1;
            Chest chest = (Chest) arenaChest.getLocation().getBlock().getState();
            for (int i = 0; i < maxItems; i++) {
                int itemIndex = r.nextInt(lastChance);
                for (Range<ItemStack> range : ranges) {
                    if (range.contains(itemIndex)) {
                        int index;
                        do {
                            index = r.nextInt(chest.getInventory().getSize());
                        } while (chest.getInventory().getItem(index) != null);
                        
                        chest.getInventory().setItem(index, range.getObject());
                    }
                }
            }
        }
    }
    
    public String getName() {
        return name;
    }
    
    public ArenaState getState() {
        return state;
    }
    
    public World getWorld() {
        return world;
    }
    
    public Cuboid getRegion() {
        return region;
    }
    
    public void setRegion(Cuboid region) {
        this.region = region;
    }
    
    public List<Player> getTributes() {
        return new ArrayList<>(tributes);
    }
    
    public Map<Integer, Spawn> getSpawnpoints() {
        return new HashMap<>(spawnpoints);
    }
    
    public List<ArenaChest> getChests() {
        return new ArrayList<>(chests);
    }
    
    public void removeArenaCleanup() {
        this.state = ArenaState.SETUP;
        clearPlayers("&cThe arena you were in was removed.");
        clearChests();
        clearSpawns();
        updateSigns();
    }
    
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Arena arena = (Arena) o;
        return Objects.equals(name, arena.name);
    }
    
    public int hashCode() {
        return Objects.hash(name);
    }
    
    public String formatLine(String... args) {
        return "&aName: &e" + this.name + " &aWorld: &e" + this.world.getName() + " &aState: &e" + this.state.name();
    }
    
    public Map<Player, Kit> getKitsInUse() {
        return new HashMap<>(kitsInUse);
    }
}