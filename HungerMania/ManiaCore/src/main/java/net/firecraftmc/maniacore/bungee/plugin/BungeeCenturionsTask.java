package net.firecraftmc.maniacore.bungee.plugin;

import net.firecraftmc.maniacore.plugin.CenturionsTask;
import net.md_5.bungee.api.scheduler.ScheduledTask;

public class BungeeCenturionsTask implements CenturionsTask {
    
    private ScheduledTask task;
    
    public BungeeCenturionsTask(ScheduledTask task) {
        this.task = task;
    }
    
    public void cancel() {
        task.cancel();
    }
}
