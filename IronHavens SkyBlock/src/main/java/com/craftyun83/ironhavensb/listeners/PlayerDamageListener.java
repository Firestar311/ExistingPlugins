package com.craftyun83.ironhavensb.listeners;

import com.craftyun83.ironhavensb.Main;
import com.sk89q.worldedit.WorldEditException;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import java.io.FileNotFoundException;
import java.io.IOException;

public class PlayerDamageListener implements Listener {

    private Main plugin;

    public boolean temp = true;

    public PlayerDamageListener(Main plugin) {
        this.plugin = plugin;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onDamageEvent(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if (e.getCause() == DamageCause.VOID) {
                e.setCancelled(true);
                if (temp) {
                    p.performCommand("spawn");
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.YELLOW + "Teleported you back to spawn!"));
                    temp = false;
                    plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> temp = true, 0, 60L);
                }
            }
        }
    }
}