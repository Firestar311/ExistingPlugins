package com.craftyun83.ironhavensb.listeners;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.craftyun83.ironhavensb.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.sk89q.worldedit.WorldEditException;
import com.craftyun83.ironhavensb.Main;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class PlayerDamageListener implements Listener {
	
	private Main plugin;
	
	public Boolean temp = true;
	
	public PlayerDamageListener(Main plugin) {
		this.plugin = plugin;
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

    @EventHandler
    public void onDamageEvent(EntityDamageEvent e) throws FileNotFoundException, WorldEditException, IOException {
		
		if (e.getEntity() instanceof Player) {
			
			Player p = (Player) e.getEntity();
	        
	        if (e.getCause() == DamageCause.VOID) {
	        	
	        	e.setCancelled(true);
	        	
	        	if (Bukkit.getWorld(p.getUniqueId().toString()) != null) {
	    			
	        		if (temp == true) {
	        			
	        			p.performCommand("spawn");
		    			p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.YELLOW+"Teleported you back to spawn!"));
		    			temp = false;
		    			
		    		    plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
		    		    	
		                    public void run() {
		                    	
		                    	temp = true;
		                    	
		                    }
		                    
		                }, 0, 60L);
	        			
	        		}
	    			
	    		} else {
	    			
	    			if (temp == true) {
	    			
		    			p.performCommand("spawn");
		    			p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.YELLOW+"Teleported you back to spawn!"));
		    			temp = false;
		    			
		    		    plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
		    		    	
		                    public void run() {
		                    	
		                    	temp = true;
		                    	
		                    }
		                    
		                }, 0, 60L);
		    		    
	    			}
	    			
	    		}
	        	
	        }
			
		}
        
    }

}