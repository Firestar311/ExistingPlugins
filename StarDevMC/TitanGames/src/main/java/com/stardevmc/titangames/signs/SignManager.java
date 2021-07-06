package com.stardevmc.titangames.signs;

import com.stardevmc.titangames.TitanGames;
import com.stardevmc.titangames.arenas.Arena;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SignManager {
    
    private TitanGames plugin;
    
    private File file;
    private FileConfiguration config;
    
    private List<LobbySign> lobbySigns = new ArrayList<>();
    
    public SignManager(TitanGames plugin) {
        this.plugin = plugin;
        
        file = new File(plugin.getDataFolder(), "signs.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        config = YamlConfiguration.loadConfiguration(file);
    }
    
    public List<LobbySign> getLobbySigns(Arena arena) {
       return lobbySigns.stream().filter(sign -> sign.getArena().equals(arena)).collect(Collectors.toList());
    }
    
    public LobbySign getLobbySign(Location location) {
        for (LobbySign sign : this.lobbySigns) {
            if (sign.getLocation().equals(location)) {
                return sign;
            }
        }
        
        return null;
    }
    
    public LobbySign getLobbySign(Sign sign) {
        return getLobbySign(sign.getLocation());
    }
    
    public void addLobbySign(LobbySign sign) {
        this.lobbySigns.add(sign);
        sign.updateSign();
    }
    
    public void removeSign(Location location) {
        LobbySign toRemove = null;
        for (LobbySign sign : this.lobbySigns) {
            if (sign.getLocation().equals(location)) {
                toRemove = sign;
                break;
            }
        }
        
        this.lobbySigns.remove(toRemove);
    }
    
    public void removeSign(Sign sign) {
        removeSign(sign.getLocation());
    }
    
    public void removeSign(LobbySign sign) {
        this.lobbySigns.remove(sign);
    }
    
    public void saveSigns() {
        config.set("signs", null);
        saveFile();
        int counter = 0;
        for (LobbySign lobbySign : this.lobbySigns) {
            this.config.set("signs." + counter + ".arena", lobbySign.getArena().getName());
            this.config.set("signs." + counter + ".location", lobbySign.getLocation());
            counter++;
        }
        saveFile();
    }
    
    public void loadSigns() {
        this.lobbySigns.clear();
        ConfigurationSection section = this.config.getConfigurationSection("signs");
        if (section == null) return;
        for (String sn : section.getKeys(false))  {
            String arenaName = this.config.getString("signs." + sn + ".arena");
            Arena arena = this.plugin.getArenaManager().getArena(arenaName);
            if (arena == null) continue;
            Location location = (Location) this.config.get("signs." + sn + ".location");
            if (location == null) continue;
            this.lobbySigns.add(new LobbySign(location, arena));
        }
    }
    
    public void updateSigns() {
        for (LobbySign sign : this.lobbySigns) {
            sign.updateSign();
        }
    }
    
    private void saveFile() {
        try {
            this.config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}