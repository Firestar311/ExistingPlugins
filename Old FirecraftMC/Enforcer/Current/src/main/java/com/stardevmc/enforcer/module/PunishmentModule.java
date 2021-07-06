package com.stardevmc.enforcer.module;

import com.stardevmc.enforcer.Enforcer;
import com.stardevmc.enforcer.manager.PunishmentManager;
import com.stardevmc.enforcer.modules.base.Module;
import com.stardevmc.enforcer.modules.punishments.cmds.PunishmentCommands;
import com.stardevmc.enforcer.modules.punishments.listeners.PlayerChatListener;
import com.stardevmc.enforcer.modules.punishments.listeners.PlayerJoinListener;
import com.stardevmc.enforcer.objects.enums.Visibility;
import org.bukkit.configuration.ConfigurationSection;

public class PunishmentModule extends Module<PunishmentManager> {
    
    private boolean confirmPunishments;
    private Visibility defaultVisibiltiy;
    
    public PunishmentModule(Enforcer plugin, String... commands) {
        super(plugin, "punishments", new PunishmentManager(plugin), commands);
        this.addListenerClass(PlayerChatListener.class, PlayerJoinListener.class);
        this.confirmPunishments = true;
        this.defaultVisibiltiy = Visibility.STAFF_ONLY;
    }
    
    public void setup() {
        loadSettings();
        if (enabled) {
            manager.loadData();
        }
        PunishmentCommands puCommands = new PunishmentCommands(plugin);
        registerCommands(puCommands);
        registerListeners();
    }
    
    public void desetup() {
        manager.saveData();
        registerCommands(null);
        unregisterListeners();
        saveSettings();
    }
    
    
    
    @Override
    protected void saveSettings() {
        ConfigurationSection settingsSecton = getSection().getConfigurationSection("settings");
        if (settingsSecton == null) {
            settingsSecton = getSection().createSection("settings");
        }
        settingsSecton.set("confirmPunishments", this.confirmPunishments);
        settingsSecton.set("defaultVisibility", this.defaultVisibiltiy.name());
    }
    
    @Override
    protected void loadSettings() {
        ConfigurationSection settingsSecton = getSection().getConfigurationSection("settings");
        if (settingsSecton != null) {
            confirmPunishments = settingsSecton.getBoolean("confirmPunishments");
            defaultVisibiltiy = Visibility.valueOf(settingsSecton.getString("defaultVisibility"));
        }
    }
    
    public boolean confirmPunishments() {
        return confirmPunishments;
    }
    
    public void setConfirmPunishments(boolean confirmPunishments) {
        this.confirmPunishments = confirmPunishments;
    }
    
    public Visibility getDefaultVisibiltiy() {
        return defaultVisibiltiy;
    }
    
    public void setDefaultVisibiltiy(Visibility defaultVisibiltiy) {
        this.defaultVisibiltiy = defaultVisibiltiy;
    }
}