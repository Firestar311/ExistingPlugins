package net.firecraftmc.api.packets;

import java.util.UUID;

public class FPacketServerPlayerJoin extends FirecraftPacket {

    private static final long serialVersionUID = FirecraftPacket.serialVersionUID + 1L;

    private UUID uuid;

    public FPacketServerPlayerJoin() {}

    public FPacketServerPlayerJoin(String server, UUID uuid) {
        this.serverid = server;
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }
}
