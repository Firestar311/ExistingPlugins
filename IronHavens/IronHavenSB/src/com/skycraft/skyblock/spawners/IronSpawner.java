package com.skycraft.skyblock.spawners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;

import com.skycraft.skyblock.Main;

public class IronSpawner {
	
	private Main plugin;
	
	private int task;
	
	public IronSpawner(Zombie zombie) {
		
		task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
		    @Override
		    public void run() {
		        if (zombie.isValid()) {
		        	
		        	zombie.getWorld().dropItemNaturally(zombie.getLocation(), new ItemStack(Material.IRON_INGOT));
		        	
		        } else {
		        	
		        	Bukkit.getScheduler().cancelTask(task);
		        	
		        }
		    }
		}, 0L, 100L);
		
	}

}
