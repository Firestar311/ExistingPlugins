package com.kingrealms.realms.territory;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.flight.FlightInfo;
import com.kingrealms.realms.flight.FlightResult;
import com.kingrealms.realms.plot.Plot;
import com.kingrealms.realms.profile.RealmProfile;
import com.kingrealms.realms.storage.StorageManager;
import com.kingrealms.realms.territory.base.Territory;
import com.kingrealms.realms.territory.base.member.Member;
import com.starmediadev.lib.config.ConfigManager;
import com.starmediadev.lib.util.Code;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class TerritoryManager {
    
    private final ConfigManager configManager = StorageManager.territoriesConfig;
    
    private final Map<String, Territory> territories = new HashMap<>();
    
    public TerritoryManager() {
        configManager.setup();
        
        new BukkitRunnable() {
            public void run() {
                for (Territory territory : new ArrayList<>(territories.values())) {
                    if (territory.getFlightInfo().checkExpired() == FlightResult.EXPIRED) {
                        for (Member member : territory.getMembers()) {
                            Player player = member.getRealmProfile().getBukkitPlayer();
                            if (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE) {
                                if (player.isFlying()) {
                                    Block highestBlock = player.getLocation().getWorld().getHighestBlockAt(player.getLocation(), HeightMap.WORLD_SURFACE);
                                    player.teleport(highestBlock.getLocation());
                                }
                                player.setAllowFlight(false);
                                player.setFlying(false);
                            }
                        }
                        
                        territory.getFlightInfo().deactivate();
                        territory.sendMemberMessage("&sThe Orb of Flight has expired.");
                    }
                }
            }
        }.runTaskTimer(Realms.getInstance(), 20L, 1L);
        
        new BukkitRunnable() {
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    Territory territory = Realms.getInstance().getTerritoryManager().getTerritory(player);
                    if (territory == null) continue;
    
                    FlightInfo flightInfo = territory.getFlightInfo();
                    if (flightInfo.checkExpired() == FlightResult.ACTIVE) {
                        if (territory.contains(player)) {
                            if (!player.getAllowFlight()) {
                                updateFlight(player, true);
                            }
                        } else {
                            updateFlight(player, false);
                        }
                    } else if (flightInfo.checkExpired() == FlightResult.NOT_SET) {
                        updateFlight(player, false);
                    } else if (flightInfo.checkExpired() == FlightResult.EXPIRED) {
                        updateFlight(player, false);
                    }
                }
            }
        }.runTaskTimerAsynchronously(Realms.getInstance(), 20L, 1L);
    }
    
    private void updateFlight(Player player, boolean flag) {
        new BukkitRunnable() {
            public void run() {
                if (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE) {
                    if (!flag) {
                        if (player.isFlying()) {
                            Block highestBlock = player.getLocation().getWorld().getHighestBlockAt(player.getLocation(), HeightMap.WORLD_SURFACE);
                            player.teleport(highestBlock.getLocation());
                        }
                    }
                    player.setAllowFlight(flag);
                    if (!flag) {
                        player.setFlying(flag);
                    }
                }
            }
        }.runTaskLater(Realms.getInstance(), 1L);
    }
    
    public void saveData() {
        FileConfiguration territoryConfig = this.configManager.getConfig();
        territoryConfig.set("territories", null);
        for (Territory territory : this.territories.values()) {
            territoryConfig.set("territories." + territory.getUniqueId(), territory);
        }
    
        this.configManager.saveConfig();
    }
    
    public void loadData() {
        FileConfiguration config = configManager.getConfig();
        ConfigurationSection territoriesSection = config.getConfigurationSection("territories");
        if (territoriesSection != null) {
            for (String p : territoriesSection.getKeys(false)) {
                Territory territory = (Territory) territoriesSection.get(p);
                this.territories.put(territory.getUniqueId(), territory);
            }
        }
    }
    
    public void removeTerritory(String uniqueId) {
        this.territories.remove(uniqueId);
    }
    
    public Territory getTerritory(String name) {
        for (Territory territory : this.territories.values()) {
            if (territory.getName().equalsIgnoreCase(name)) {
                return territory;
            }
        }
        
        for (Territory territory : this.territories.values()) {
            if (territory.getUniqueId().equalsIgnoreCase(name)) {
                return territory;
            }
        }
        
        return null;
    }
    
    public void addTerritory(Territory territory) {
        if (StringUtils.isEmpty(territory.getUniqueId())) {
            String id;
            do {
                id = Code.generateNewCode(8, false);
            } while (this.territories.containsKey(id));
            territory.setUniqueId(id);
        }
        this.territories.put(territory.getUniqueId(), territory);
    }
    
    public Territory getTerritory(Player player) {
        for (Territory territory : this.territories.values()) {
            Member member = territory.getMember(player.getUniqueId());
            if (member != null) {
                return territory;
            }
        }
        
        return null;
    }
    
    public Set<Territory> getTerritories() {
        return new HashSet<>(this.territories.values());
    }
    
    public void clearTerritories() {
        this.territories.clear();
    }
    
    public Territory getTerritory(Location location) {
        for (Territory territory : this.territories.values()) {
            if (territory.contains(location)) {
                return territory;
            }
        }
        
        return null;
    }
    
    public Territory getTerritory(UUID uniqueId) {
        for (Territory territory : getTerritories()) {
            if (territory.getMember(uniqueId) != null) {
                return territory;
            }
        }
        
        return null;
    }
    
    public Territory getTerritory(RealmProfile player) {
        return getTerritory(player.getUniqueId());
    }
    
    public Territory getTerritory(Plot plot) {
        for (Territory territory : territories.values()) {
            if (territory.getPlot(plot.getUniqueId()) != null) {
                return territory;
            }
        }
        return null;
    }
}