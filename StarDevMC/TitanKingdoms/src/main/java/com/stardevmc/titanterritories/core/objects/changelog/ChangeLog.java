package com.stardevmc.titanterritories.core.objects.changelog;

import com.firestar311.lib.config.ConfigManager;
import com.firestar311.lib.util.Utils;
import com.stardevmc.titanterritories.core.TitanTerritories;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ChangeLog implements Listener {
    
    private List<Version> versions = new ArrayList<>();
    private Version currentVersion;
    private ConfigManager configManager;
    
    public ChangeLog() {
        Bukkit.getServer().getPluginManager().registerEvents(this, TitanTerritories.getInstance());
        
        this.configManager = new ConfigManager(TitanTerritories.getInstance(), "versions");
        this.configManager.setup();
        
        //Each version is created by code for now, may or may not have an API of sorts in FireLib for other plugins to use
        this.currentVersion = new Version("Initial Release", "1.0", "This is the first beta release of the plugin.");
        this.currentVersion.addChanges("Please refer to /kingdom help to get a list of commands",
                "Be sure to report any issues to ThomasRW as soon as you come across them.");
        
        this.load();
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (!TitanTerritories.getInstance().isKingdomsEnabled()) return;
        Player player = e.getPlayer();
        if (currentVersion == null) return;
        if (currentVersion.getAcknowledged().contains(player.getUniqueId())) return;
        new BukkitRunnable() {
            public void run() {
                StringBuilder sb = new StringBuilder();
                sb.append("&6").append(Utils.blankLine(60)).append("\n");
                sb.append("&dKingdoms is on version ").append(currentVersion.getNumber()).append(" which means the following has happened between versions").append("\n");
                for (String s : currentVersion.getChanges()) {
                    sb.append(" &8- &7").append(s).append("\n");
                }
                sb.append("&dPlaying during this time of development means that things are unstable and can change any time.").append("\n")
                        .append("&dTo remove this message every time you join for this version, please click the button below.");
                BaseComponent[] baseComponents = new ComponentBuilder("").append("[Confirm]").color(ChatColor.GREEN).bold(true).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/debug ack")).create();
                player.sendMessage(Utils.color(sb.toString()));
                player.spigot().sendMessage(baseComponents);
                player.sendMessage(Utils.color("&6" + Utils.blankLine(60)));
            }
        }.runTaskLater(TitanTerritories.getInstance(), 5L);
    }
    
    public Version getCurrentVersion() {
        return currentVersion;
    }
    
    public List<Version> getVersions() {
        return versions;
    }
    
    public void save() {
        //Previous versions do not save who acknowledged them, it does not matter
        if (currentVersion != null) {
            if (!currentVersion.getAcknowledged().isEmpty()) {
                List<String> uuids = new ArrayList<>();
                currentVersion.getAcknowledged().forEach(uuid -> uuids.add(uuid.toString()));
                this.configManager.getConfig().set("current", uuids);
            }
        }
        this.configManager.saveConfig();
    }
    
    private void load() {
        if (configManager.getConfig().contains("current")) {
            List<String> rawUUIDs = configManager.getConfig().getStringList("current");
            for (String u : rawUUIDs) {
                try {
                    this.currentVersion.addAcknoledged(UUID.fromString(u));
                } catch (Exception e) {}
            }
        }
    }
}