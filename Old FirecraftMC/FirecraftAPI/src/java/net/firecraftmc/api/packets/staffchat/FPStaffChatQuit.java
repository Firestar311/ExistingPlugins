package net.firecraftmc.api.packets.staffchat;

import java.util.UUID;

public class FPStaffChatQuit extends FPacketStaffChat {

    private static final long serialVersionUID = FPacketStaffChat.serialVersionUID + 1L;

    public FPStaffChatQuit() {}

    public FPStaffChatQuit(String server, UUID player) {
        super(server, player);
    }
}