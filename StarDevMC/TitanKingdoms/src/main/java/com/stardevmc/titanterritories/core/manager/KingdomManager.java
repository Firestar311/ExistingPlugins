package com.stardevmc.titanterritories.core.manager;

import com.firestar311.lib.config.ConfigManager;
import com.firestar311.lib.util.Utils;
import com.stardevmc.chat.TitanChat;
import com.stardevmc.chat.api.DefaultRoles;
import com.stardevmc.titanterritories.core.TitanTerritories;
import com.stardevmc.titanterritories.core.objects.holder.Kingdom;
import com.stardevmc.titanterritories.core.objects.member.Member;
import com.stardevmc.titanterritories.core.util.Constants;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

public class KingdomManager {
    private ConfigManager configManager;
    private TitanTerritories plugin = TitanTerritories.getInstance();
    private Map<UUID, Kingdom> kingdoms = new HashMap<>();
    
    public KingdomManager() {
        this.configManager = new ConfigManager(plugin, "kingdoms");
        this.configManager.setup();
    }
    
    public void saveData() {
        FileConfiguration config = configManager.getConfig();
        config.set("kingdoms", null);
        configManager.saveConfig();
        for (Kingdom kingdom : this.kingdoms.values()) {
            config.set("kingdoms." + kingdom.getUniqueId().toString(), kingdom);
        }
        this.configManager.saveConfig();
    }
    
    public void loadData() {
        FileConfiguration config = configManager.getConfig();
        if (!config.contains("kingdoms")) return;
        for (String k : config.getConfigurationSection("kingdoms").getKeys(false)) {
            Kingdom kingdom = (Kingdom) config.get("kingdoms." + k);
            this.kingdoms.put(kingdom.getUniqueId(), kingdom);
        }
    }
    
    public void removeKingdom(Kingdom kingdom) {
        this.kingdoms.remove(kingdom.getUniqueId());
    }
    
    public void removeKingdom(String name) {
        Kingdom targetKingdom = getKingdom(name);
        removeKingdom(targetKingdom);
    }
    
    public Kingdom getKingdom(String name) {
        for (Kingdom kingdom : kingdoms.values()) {
            if (kingdom.getName().equalsIgnoreCase(name.toLowerCase())) {
                return kingdom;
            }
        }
        return null;
    }
    
    public void addKingdom(Kingdom kingdom) {
        if (kingdom.getUniqueId() == null) {
            kingdom.setUniqueId(generateUUID());
        }
        this.kingdoms.put(kingdom.getUniqueId(), kingdom);
    }
    
    public List<Kingdom> getKingdoms() {
        return new ArrayList<>(kingdoms.values());
    }
    
    public Kingdom getKingdom(Player player) {
        for (Kingdom kingdom : this.kingdoms.values()) {
            if (kingdom.getUserController().get(player.getUniqueId()) != null) {
                return kingdom;
            }
            
            if (kingdom.isMonarch(player)) {
                return kingdom;
            }
        }
        
        return null;
    }
    
    public Kingdom getKingdom(Location location) {
        for (Kingdom kingdom : this.kingdoms.values()) {
            if (kingdom.getClaimController().contains(location)) {
                return kingdom;
            }
        }
        return null;
    }
    
    public Kingdom getKingdom(UUID uuid) {
        return kingdoms.get(uuid);
    }
    
    public UUID generateUUID() {
        UUID uuid;
        do {
            uuid = UUID.randomUUID();
        } while (this.kingdoms.containsKey(uuid));
        return uuid;
    }
    
    public Kingdom createKingdom(Member member, String name) {
        EconomyResponse economyResponse = plugin.getVaultEconomy().withdrawPlayer(member.getPlayer(), Constants.KINGDOM_CREATE_COST);
        if (!economyResponse.transactionSuccess()) {
            member.sendMessage("&cYou must have $" + Constants.KINGDOM_CREATE_COST + " to create a kingdom");
            return null;
        }
        UUID uuid = generateUUID();
        Kingdom createdKingdom = new Kingdom(member, member.getLocation(), name, uuid);
        createdKingdom.getClaimController().addPlot(plugin.getPlotManager().getPlot(member.getLocation()));
        createdKingdom.createChatroom();
        createdKingdom.getChatroom().addMember(member.getUniqueId(), DefaultRoles.OWNER);
        TitanChat.getInstance().getChatroomManager().registerChatroom(createdKingdom.getChatroom());
        plugin.getKingdomManager().addKingdom(createdKingdom);
        Bukkit.broadcastMessage(Utils.color("&9" + member.getName() + " has created a new Kingdom called " + createdKingdom.getName()));
        return createdKingdom;
    }
}