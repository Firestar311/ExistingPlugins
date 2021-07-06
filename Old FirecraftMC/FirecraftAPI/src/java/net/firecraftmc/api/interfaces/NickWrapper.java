package net.firecraftmc.api.interfaces;

import net.firecraftmc.api.model.player.*;
import net.firecraftmc.api.plugin.IFirecraftCore;
import org.bukkit.entity.Player;

/**
 * Provides a base for the nicknames because in order to set the skin, NMS must be used
 */
public interface NickWrapper {

    NickInfo setNick(IFirecraftCore plugin, FirecraftPlayer player, FirecraftPlayer nick);

    void refreshOthers(IFirecraftCore plugin, Player player);
    
    void setProfileName(Player player, String name);
    
    void refreshSelf(IFirecraftCore plugin, Player nicked, String name);

    void setSkinProperties(Player player, Skin skin);
}