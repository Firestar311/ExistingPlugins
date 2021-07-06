package net.firecraftmc.api.packets.staffchat;

import java.util.UUID;

public class FPStaffChatMessage extends FPacketStaffChat {

    private static final long serialVersionUID = FPacketStaffChat.serialVersionUID + 1L;

    private String message;

    public FPStaffChatMessage() {}

    public FPStaffChatMessage(String server, UUID player, String message) {
        super(server, player);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}