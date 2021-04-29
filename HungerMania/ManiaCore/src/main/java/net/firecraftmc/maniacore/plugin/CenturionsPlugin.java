package net.firecraftmc.maniacore.plugin;

import net.firecraftmc.manialib.data.model.DatabaseHandler;

public interface CenturionsPlugin extends DatabaseHandler {
    String getVersion();
    String getName();
    
    default void setupDatabaseRecords() {}
    default void setupUserManager() {}
    default void setupRedisListeners() {}
    default void setupServerManager() {}
    
    CenturionsTask runTask(Runnable runnable);
    CenturionsTask runTaskAsynchronously(Runnable runnable);
    CenturionsTask runTaskLater(Runnable runnable, long delay);
    CenturionsTask runTaskLaterAsynchronously(Runnable runnable, long delay);
    CenturionsTask runTaskTimer(Runnable runnable, long delay, long period);
    CenturionsTask runTaskTimerAsynchronously(Runnable runnable, long delay, long period);
}