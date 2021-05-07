package net.firecraftmc.hungergames.game.bounty;

import net.firecraftmc.hungergames.game.Game;
import net.firecraftmc.hungergames.game.GamePlayer;
import net.firecraftmc.maniacore.api.stats.Statistic;
import net.firecraftmc.maniacore.api.stats.Stats;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BountyManager {

    private List<Bounty> bounties = new ArrayList<>();

    public void addBounty(Bounty bounty) {
        this.bounties.add(bounty);
    }

    public int getTotalBounty(UUID target) {
        int totalBounty = 0;
        for (Bounty bounty : bounties) {
            if (bounty.getTarget().equals(target)) {
                totalBounty += bounty.getPoints();
            }
        }

        return totalBounty;
    }

    public void clearBounties(UUID target) {
        this.bounties.removeIf(bounty -> bounty.getTarget().equals(target));
    }

    public void refundBounties(Game game) {
        for (Bounty bounty : bounties) {
            GamePlayer actorPlayer = game.getPlayer(bounty.getActor());
            Statistic score = actorPlayer.getUser().getStat(Stats.HG_SCORE);
            score.setValue(score.getAsInt() + bounty.getPoints());
            actorPlayer.sendMessage("&6&l>> &cThe game as ended, so your bounty of " + bounty.getPoints() + " points on " + game.getPlayer(bounty.getTarget()).getUser().getName() + " has been refunded.");
        }
    }

    public void refundBounties(Game game, UUID actor) {
        for (Bounty bounty : bounties) {
            if (bounty.getActor().equals(actor)) {
                GamePlayer actorPlayer = game.getPlayer(bounty.getActor());
                Statistic score = actorPlayer.getUser().getStat(Stats.HG_SCORE);
                score.setValue(score.getAsInt() + bounty.getPoints());
            }
        }
    }
}
