package com.craftyun83.ironhavensb.listeners;

import com.craftyun83.ironhavensb.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {

    private Main plugin;

    public BlockBreakListener(Main plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        FileConfiguration dmconfig = plugin.DiamondMinedConfig;
        if (e.getBlock().getType() == Material.DIAMOND_ORE) {
            if (e.getPlayer().getGameMode() == GameMode.SURVIVAL) {
                if (dmconfig.getString(e.getPlayer().getName()) == null) {
                    dmconfig.createSection(e.getPlayer().getName());
                    dmconfig.set(e.getPlayer().getName(), 1);
                    plugin.saveDiamondMinedYML(dmconfig, plugin.DiamondMinedYML);
                } else {
                    dmconfig.set(e.getPlayer().getName(), dmconfig.getInt(e.getPlayer().getName()) + 1);
                    if (dmconfig.getInt(e.getPlayer().getName()) == 50) {
                        Main.econ.withdrawPlayer(e.getPlayer(), 500);
                        e.getPlayer().sendMessage(ChatColor.GREEN + "[Iron Haven] >> Quest Complete: Mine 50 diamonds");
                    }
                    plugin.saveDiamondMinedYML(dmconfig, plugin.DiamondMinedYML);
                }
            }
        }
    }
}