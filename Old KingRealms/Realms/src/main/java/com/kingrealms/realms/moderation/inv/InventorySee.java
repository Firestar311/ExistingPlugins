package com.kingrealms.realms.moderation.inv;

import com.kingrealms.realms.Realms;
import org.bukkit.event.Listener;

public final class InventorySee implements Listener {
    private InventorySee() {
        Realms.getInstance().getServer().getPluginManager().registerEvents(this, Realms.getInstance());
    }
}