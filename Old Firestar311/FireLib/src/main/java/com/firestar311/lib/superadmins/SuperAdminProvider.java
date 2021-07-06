package com.firestar311.lib.superadmins;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;
import java.util.UUID;

public interface SuperAdminProvider extends Listener {
    
    boolean isEnabled();
    JavaPlugin getPlugin();
    Set<UUID> getSuperAdmins();
    void addSuperAdmin(UUID uuid);
    void removeSuperAdmin(UUID uuid);
    boolean isSuperAdmin(UUID uuid);
    void saveData();
    void loadData();
    
    @EventHandler
    void onSuperAdminToggleEvent(SuperAdminToggleEvent e);
}