package com.skycraft.skyblock.island;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.skycraft.skyblock.Main;

public class DeleteIsland {
	
	public void unloadIsland(World world) {
		
	    if(world != null) {
	    	
	        Bukkit.getServer().unloadWorld(world, true);
	        
	    }
	    
	}
	
	public boolean deleteWorld(File path) {
	      if(path.exists()) {
	          File files[] = path.listFiles();
	          for(int i=0; i<files.length; i++) {
	              if(files[i].isDirectory()) {
	                  deleteWorld(files[i]);
	              } else {
	                  files[i].delete();
	              }
	          }
	      }
	      return(path.delete());
	}
	
	public DeleteIsland(File path, World world, Main plugin, Player p) {
		
		p.performCommand("spawn");
		
		unloadIsland(world);
		
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
			@Override
			public void run() {
				deleteWorld(path);
			}
		}, 60L);
		
	}

}

