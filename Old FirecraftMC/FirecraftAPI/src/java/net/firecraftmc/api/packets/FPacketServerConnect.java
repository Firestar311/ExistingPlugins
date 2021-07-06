package net.firecraftmc.api.packets;

public class FPacketServerConnect extends FirecraftPacket {

    private static final long serialVersionUID = FirecraftPacket.serialVersionUID + 1L;

    public FPacketServerConnect() {}

    public FPacketServerConnect(String server) {
        this.serverid = server;
    }
}