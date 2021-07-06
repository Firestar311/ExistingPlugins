package com.kingrealms.realms.limits;

import com.kingrealms.realms.limits.group.*;
import com.kingrealms.realms.limits.limit.Limit;
import com.kingrealms.realms.storage.StorageManager;
import com.starmediadev.lib.config.ConfigManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashSet;
import java.util.Set;

public class LimitsManager {
    
    private Set<LimitGroup> limitGroups = new HashSet<>();
    private ConfigManager configManager = StorageManager.limitsConfig;
    
    public LimitsManager() {
        configManager.setup();
        
        PlayerLimits playerLimits = new PlayerLimits();
        playerLimits.createDefaultLimits();
        TerritoryLimits territoryLimits = new TerritoryLimits();
        territoryLimits.createDefaultLimits();
        
        this.limitGroups.add(playerLimits);
        this.limitGroups.add(territoryLimits);
    }
    
    public Set<LimitGroup> getLimitGroups() {
        return limitGroups;
    }
    
    public void saveData() {
        for (LimitGroup group : limitGroups) {
            configManager.getConfig().set("limits.groups." + group.getId(), group);
        }
        
        configManager.saveConfig();
    }
    
    public void loadData() {
        FileConfiguration config = configManager.getConfig();
        if (!config.contains("limits.groups")) {
            return;
        }
    
        ConfigurationSection limitGroupsSection = config.getConfigurationSection("limits.groups");
        for (String l : limitGroupsSection.getKeys(false)) {
            LimitGroup limitGroup = (LimitGroup) limitGroupsSection.get(l);
            this.limitGroups.add(limitGroup);
        }
    }
    
    public Limit getLimit(String limit_id) {
        for (LimitGroup group : this.limitGroups) {
            for (Limit limit : group.getLimits()) {
                if (limit.getId().equalsIgnoreCase(limit_id)) {
                    return limit;
                }
                
                if (limit.getName().equalsIgnoreCase(limit_id)) {
                    return limit;
                }
            }
        }
        return null;
    }
    
    public LimitGroup getLimitGroup(String groupId) {
        for (LimitGroup group : limitGroups) {
            if (group.getId().equalsIgnoreCase(groupId)) {
                return group;
            }
        }
        return null;
    }
}