package net.firecraftmc.maniacore.spigot.plugin;

import net.firecraftmc.maniacore.plugin.CenturionsTask;
import org.bukkit.scheduler.BukkitTask;

public class SpigotCenturionsTask implements CenturionsTask {
    
    private BukkitTask task;
    
    public SpigotCenturionsTask(BukkitTask task) {
        this.task = task;
    }
    
    public void cancel() {
        task.cancel();
    }
}
