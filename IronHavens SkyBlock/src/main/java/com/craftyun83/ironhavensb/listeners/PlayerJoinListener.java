package com.craftyun83.ironhavensb.listeners;

import com.craftyun83.ironhavensb.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;


import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class PlayerJoinListener implements Listener {
	
	private Main plugin;
	
	public PlayerJoinListener(Main plugin) {
		this.plugin = plugin;
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		
		FileConfiguration config = plugin.getConfig();
		Player p = e.getPlayer();
		
		// Sets player's join message
		
		e.setJoinMessage(config.getString("join_message").replace("&", "§").replace("%player%", p.getName()));
		p.performCommand("spawn");
		p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GREEN+"Welcome to Iron Haven!"));
		
		
	}
	
	public void onPlayerQuit(PlayerQuitEvent e) {
		
		FileConfiguration config = plugin.getConfig();
		Player p = e.getPlayer();
		
		// Sets player's join message
		
		e.setQuitMessage(config.getString("quit_message").replace("&", "§").replace("%player%", p.getName()));
		
	}

}
