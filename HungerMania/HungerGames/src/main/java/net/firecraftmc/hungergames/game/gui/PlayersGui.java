package net.firecraftmc.hungergames.game.gui;

import net.firecraftmc.hungergames.HungerGames;
import net.firecraftmc.hungergames.game.team.GameTeam;
import net.firecraftmc.hungergames.game.Game;
import net.firecraftmc.maniacore.spigot.gui.GUIButton;
import net.firecraftmc.maniacore.spigot.gui.Gui;

import java.util.UUID;

public class PlayersGui extends Gui {
    public PlayersGui(Game game, GameTeam team) {
        super(HungerGames.getInstance(), team.getName(), false, 27);
    
        for (UUID p : team.getMembers()) {
            GUIButton button = new GUIButton(game.getPlayer(p).getSkull());
            button.setListener(e -> new SpectatorGui(game, game.getPlayer(e.getWhoClicked().getUniqueId()), game.getPlayer(p)).openGUI(e.getWhoClicked()));
            addButton(button);
        }
    }
}
