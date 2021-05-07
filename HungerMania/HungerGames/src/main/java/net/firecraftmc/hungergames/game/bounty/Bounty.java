package net.firecraftmc.hungergames.game.bounty;

import java.util.UUID;

public class Bounty {
    private UUID actor, target;
    private int points;

    public Bounty(UUID actor, UUID target, int points) {
        this.actor = actor;
        this.target = target;
        this.points = points;
    }

    public UUID getActor() {
        return actor;
    }

    public UUID getTarget() {
        return target;
    }

    public int getPoints() {
        return points;
    }
}
