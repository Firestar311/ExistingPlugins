package net.firecraftmc.hungergames.chat;

import net.firecraftmc.hungergames.HungerGames;
import net.firecraftmc.hungergames.game.Game;
import net.firecraftmc.hungergames.game.GamePlayer;
import net.firecraftmc.maniacore.api.chat.ChatHandler;
import net.firecraftmc.maniacore.api.util.State;
import net.firecraftmc.maniacore.spigot.user.SpigotUser;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class HGChatHandler extends ChatHandler {
    public Set<UUID> getAllTargets() {
        Set<UUID> targets = new HashSet<>();
        Game game = HungerGames.getInstance().getGameManager().getCurrentGame();
        if (game == null) {
            Set<SpigotUser> players = HungerGames.getInstance().getLobby().getPlayers();
            players.addAll(HungerGames.getInstance().getLobby().getHiddenStaff());
            for (SpigotUser player : players) {
                targets.add(player.getUniqueId());
            }
        } else {
            for (GamePlayer player : game.getPlayers()) {
                targets.add(player.getUniqueId());
            }
        }
        return targets;
    }

    public Set<UUID> getMessageTargets(UUID sender) {
        Set<UUID> targets = super.getMessageTargets(sender);
        Game game = HungerGames.getInstance().getGameManager().getCurrentGame();
        if (game != null) {
            if (game.getState() != State.ENDING) {
                if (game.getSpectatorsTeam().isMember(sender) || game.getHiddenStaffTeam().isMember(sender)) {
                    targets.removeIf(target -> game.getTributesTeam().isMember(target) || game.getMutationsTeam().isMember(target));
                }
            }
        }
        return targets;
    }
}
