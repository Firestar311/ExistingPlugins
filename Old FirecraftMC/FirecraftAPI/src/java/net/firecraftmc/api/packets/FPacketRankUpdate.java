package net.firecraftmc.api.packets;

import net.firecraftmc.api.enums.Rank;

import java.util.UUID;

public class FPacketRankUpdate extends FirecraftPacket {
    private static final long serialVersionUID = FirecraftPacket.serialVersionUID + 3L;

    private UUID updater, target;
    private Rank oldRank, newRank;

    public FPacketRankUpdate() {}

    public FPacketRankUpdate(String server, UUID updater, UUID target, Rank oldRank, Rank newRank) {
        this.serverid = server;
        this.updater = updater;
        this.target = target;
        this.oldRank = oldRank;
        this.newRank = newRank;
    }

    public UUID getUpdater() {
        return updater;
    }

    public UUID getTarget() {
        return target;
    }
    
    public Rank getOldRank() {
        return oldRank;
    }
    
    public Rank getNewRank() {
        return newRank;
    }
}