package net.firecraftmc.api.interfaces;

import net.firecraftmc.api.model.player.FirecraftPlayer;
import net.firecraftmc.api.model.player.Home;

import java.util.List;
import java.util.UUID;

/**
 * The interface used for the FirecraftPlugin class.
 * Implemented in FirecraftCore
 */
public interface IHomeManager {

    List<Home> loadHomes(UUID uuid);
    void saveHomes(FirecraftPlayer player);
}
