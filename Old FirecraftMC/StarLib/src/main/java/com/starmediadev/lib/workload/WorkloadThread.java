package com.starmediadev.lib.workload;

import com.google.common.collect.Queues;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayDeque;

public class WorkloadThread extends BukkitRunnable {
    
    private static final int MAX_MS_PER_TICK = 10;
    private final ArrayDeque<Workload> workloadDeque;
    
    public WorkloadThread() {
        workloadDeque = Queues.newArrayDeque();
    }
    
    public void addLoad(Workload workload) {
        this.workloadDeque.add(workload);
    }
    
    @Override
    public void run() {
        long stopTime = System.currentTimeMillis() + MAX_MS_PER_TICK;
        while (!workloadDeque.isEmpty() && System.currentTimeMillis() <= stopTime) {
            workloadDeque.poll().compute();
        }
    }
    
    public BukkitRunnable start(JavaPlugin plugin) {
        this.runTaskTimer(plugin, 0L, 1L);
        return this;
    }
}