package net.firecraftmc.api.packets.staffchat;

import java.util.UUID;

public class FPStaffChatJoin extends FPacketStaffChat {

    private static final long serialVersionUID = FPacketStaffChat.serialVersionUID + 1L;

    public FPStaffChatJoin() {}

    public FPStaffChatJoin(String server, UUID player) {
        super(server, player);
    }
}