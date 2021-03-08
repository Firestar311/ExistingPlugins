package net.firecraftmc.hungergames.game.team;

import net.firecraftmc.hungergames.game.enums.PlayerType;
import net.firecraftmc.hungergames.game.Game;
import org.bukkit.GameMode;

public class HiddenStaffTeam extends SpectatorsTeam {
    public HiddenStaffTeam(Game game) {
        super("Hidden Staff", "&b", PlayerType.HIDDEN_STAFF, game);
        setGameMode(GameMode.ADVENTURE);
    }
}
