package net.firecraftmc.api.packets;

public class FPacketSocketBroadcast extends FirecraftPacket {
    private static final long serialVersionUID = FirecraftPacket.serialVersionUID + 1L;

    private String message;

    public FPacketSocketBroadcast() {}

    public FPacketSocketBroadcast(String server, String message) {
        super(server);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}