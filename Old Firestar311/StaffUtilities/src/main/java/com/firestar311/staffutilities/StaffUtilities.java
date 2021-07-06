package com.firestar311.staffutilities;

import com.firestar311.staffutilities.module.AntiBotModule;
import com.firestar311.staffutilities.module.PhantomResetModule;
import org.bukkit.plugin.java.JavaPlugin;

public final class StaffUtilities extends JavaPlugin {
    
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents(new AntiBotModule(this), this);
        //new AuraModule(this);
        new PhantomResetModule(this);
    }
}
