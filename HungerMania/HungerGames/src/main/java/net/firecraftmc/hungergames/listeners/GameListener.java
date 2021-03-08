package net.firecraftmc.hungergames.listeners;

import net.firecraftmc.hungergames.HungerGames;
import net.firecraftmc.hungergames.game.GameManager;
import net.firecraftmc.hungergames.lobby.Lobby;
import org.bukkit.event.Listener;

public abstract class GameListener implements Listener {
    protected HungerGames plugin;
    protected GameManager gameManager;
    protected Lobby lobby;
    
    public GameListener() {
        plugin = HungerGames.getInstance();
        gameManager = HungerGames.getInstance().getGameManager();
        lobby = HungerGames.getInstance().getLobby();
    }
}
