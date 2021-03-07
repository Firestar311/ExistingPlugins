package net.firecraftmc.maniacore.spigot.updater;

import net.firecraftmc.maniacore.ManiaCorePlugin;

public class Updater implements Runnable {
    
    private ManiaCorePlugin maniaCorePlugin;
    
    public Updater(ManiaCorePlugin maniaCorePlugin) {
        this.maniaCorePlugin = maniaCorePlugin;
    }
    
    @Override
    public void run() {
        for (net.firecraftmc.maniacore.spigot.updater.UpdateType type : UpdateType.values()) {
            final long lastRun = type.getLastRun();
            if (type.run()) {
                try {
                    maniaCorePlugin.getServer().getPluginManager().callEvent(new UpdateEvent(type, lastRun));
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
