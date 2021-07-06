package net.firecraftmc.api.packets;

public class FPacketServerDisconnect extends FirecraftPacket {

    private static final long serialVersionUID = FirecraftPacket.serialVersionUID + 1L;

    public FPacketServerDisconnect() {}

    public FPacketServerDisconnect(String server) {
        this.serverid = server;
    }
}