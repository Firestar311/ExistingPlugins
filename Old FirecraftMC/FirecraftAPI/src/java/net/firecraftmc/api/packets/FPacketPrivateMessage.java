package net.firecraftmc.api.packets;

import java.util.UUID;

public class FPacketPrivateMessage extends FirecraftPacket {
    private static final long serialVersionUID = FirecraftPacket.serialVersionUID + 1;

    private UUID sender, target;
    private String message;

    public FPacketPrivateMessage() {}

    public FPacketPrivateMessage(String server, UUID sender, UUID target, String message) {
        super(server);
        this.sender = sender;
        this.target = target;
        this.message = message;
    }

    public UUID getSender() {
        return sender;
    }

    public UUID getTarget() {
        return target;
    }

    public String getMessage() {
        return message;
    }
}
