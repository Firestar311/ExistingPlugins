package com.craftyun83.ironhavensb.spawners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;

import com.craftyun83.ironhavensb.Main;

public class IronSpawner {
	
	private Main plugin;
	
	private int task;
	
	public IronSpawner(Zombie zombie) {
		task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
			if (zombie.isValid()) {
				zombie.getWorld().dropItemNaturally(zombie.getLocation(), new ItemStack(Material.IRON_INGOT));
			} else {
				Bukkit.getScheduler().cancelTask(task);
			}
		}, 0L, 100L);
	}
}