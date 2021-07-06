package com.firestar311.staffutilities.module;

import com.firestar311.staffutilities.StaffUtilities;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

public class PhantomResetModule {
    
    public PhantomResetModule(StaffUtilities plugin) {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.hasPermission("staffutilities.nophantoms")) {
                    player.setStatistic(Statistic.TIME_SINCE_REST, 0);
                }
            }
        }, 0L, 100L);
    }
}