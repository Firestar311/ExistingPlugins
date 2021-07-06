package com.kingrealms.realms.trash;

import com.kingrealms.realms.storage.StorageManager;
import com.starmediadev.lib.config.ConfigManager;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class TrashManager implements Listener {
    
    private ConfigManager configManager = StorageManager.trashConfig;
    private Set<TrashUse> trashUses = new HashSet<>();
    
    public TrashManager() {
        configManager.setup();
    }
}