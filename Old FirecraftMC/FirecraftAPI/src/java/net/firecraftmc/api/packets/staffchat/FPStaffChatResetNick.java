package net.firecraftmc.api.packets.staffchat;

import java.util.UUID;

public class FPStaffChatResetNick extends FPacketStaffChat {
    private static final long serialVersionUID = FPacketStaffChat.serialVersionUID + 1L;

    public FPStaffChatResetNick() {}

    public FPStaffChatResetNick(String server, UUID player) {
        super(server, player);
    }
}