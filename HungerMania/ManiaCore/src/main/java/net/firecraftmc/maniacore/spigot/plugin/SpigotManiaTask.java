package net.firecraftmc.maniacore.spigot.plugin;

import net.firecraftmc.maniacore.plugin.ManiaTask;
import org.bukkit.scheduler.BukkitTask;

public class SpigotManiaTask implements ManiaTask {
    
    private BukkitTask task;
    
    public SpigotManiaTask(BukkitTask task) {
        this.task = task;
    }
    
    public void cancel() {
        task.cancel();
    }
}
