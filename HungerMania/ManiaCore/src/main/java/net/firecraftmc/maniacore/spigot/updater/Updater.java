package net.firecraftmc.maniacore.spigot.updater;

import net.firecraftmc.maniacore.CenturionsCorePlugin;

public class Updater implements Runnable {
    
    private CenturionsCorePlugin centurionsCorePlugin;
    
    public Updater(CenturionsCorePlugin centurionsCorePlugin) {
        this.centurionsCorePlugin = centurionsCorePlugin;
    }
    
    @Override
    public void run() {
        for (net.firecraftmc.maniacore.spigot.updater.UpdateType type : UpdateType.values()) {
            final long lastRun = type.getLastRun();
            if (type.run()) {
                try {
                    centurionsCorePlugin.getServer().getPluginManager().callEvent(new UpdateEvent(type, lastRun));
                } catch (Exception ex) {
                    try {
                        throw new UpdateException(ex);
                    } catch (UpdateException ex2) {
                        ex2.printStackTrace();
                    }
                }
            }
        }
    }
}
