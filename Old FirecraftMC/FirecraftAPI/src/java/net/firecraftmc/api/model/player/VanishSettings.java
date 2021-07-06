package net.firecraftmc.api.model.player;

import net.firecraftmc.api.vanish.VanishSetting;
import org.bukkit.Bukkit;

import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;

/**
 * The class that contains values related to vanish and different permissions while vanished
 * All methods are pretty self explanatory
 */
public class VanishSettings implements Serializable {
    public static final long serialVersionUID = 1L;

    private Map<VanishSetting, Boolean> settings = new HashMap<>();
    private boolean allowFlightBeforeVanish = false;

    public boolean allowFlightBeforeVanish() {
        return allowFlightBeforeVanish;
    }

    public void setAllowFlightBeforeVanish(boolean allowFlightBeforeVanish) {
        this.allowFlightBeforeVanish = allowFlightBeforeVanish;
    }

    public VanishSettings() {
        for (VanishSetting toggle : VanishSetting.TOGGLES) {
            this.settings.put(toggle, toggle.getDefaultValue());
        }
    }
    
    public Map<VanishSetting, Boolean> getSettings() {
        return settings;
    }
    
    public void setToggle(VanishSetting toggle, boolean value) {
        this.settings.put(toggle, value);
    }
    
    public boolean getSetting(VanishSetting toggle) {
        return this.settings.get(toggle);
    }
    
    public void toggle(VanishSetting toggle) {
        this.settings.put(toggle, !this.settings.get(toggle));
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        Iterator<Entry<VanishSetting, Boolean>> iterator = settings.entrySet().iterator();
        
        while (iterator.hasNext()) {
            Entry<VanishSetting, Boolean> entry = iterator.next();
            sb.append(entry.getKey().getName()).append("=").append(entry.getValue());
            if (iterator.hasNext())  sb.append(",");
        }
        sb.append("]");
        
        return sb.toString();
    }
    
    public static VanishSettings loadFromString(String string) {
        try {
            VanishSettings settings = new VanishSettings();
            string = string.replace("[", "").replace("]", "");
            String[] vSArr = string.split(",");
            for (String vs : vSArr) {
                try {
                    String[] nv = vs.split("=");
                    String n = nv[0], v = nv[1];
            
                    VanishSetting toggle = VanishSetting.getToggle(n);
                    boolean value = Boolean.parseBoolean(v);
                    settings.setToggle(toggle, value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return settings;
        } catch (Exception e) {
            Bukkit.getLogger().severe("Could not load vanish settings from the string " + string);
        }
        return new VanishSettings();
    }
}
