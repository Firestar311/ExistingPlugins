package net.firecraftmc.api.packets;

import java.util.UUID;

public class FPacketServerPlayerLeave extends FirecraftPacket {

    private static final long serialVersionUID = FirecraftPacket.serialVersionUID + 1L;

    private UUID player;

    public FPacketServerPlayerLeave() {}

    public FPacketServerPlayerLeave(String server, UUID player) {
        this.serverid = server;
        this.player = player;
    }
    
    public UUID getPlayer() {
        return player;
    }
}
