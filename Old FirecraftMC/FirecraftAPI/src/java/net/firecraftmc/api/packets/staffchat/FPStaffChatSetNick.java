package net.firecraftmc.api.packets.staffchat;

import java.util.UUID;

public class FPStaffChatSetNick extends FPacketStaffChat {
    private static final long serialVersionUID = FPacketStaffChat.serialVersionUID + 1L;

    private String profile;

    public FPStaffChatSetNick() {}

    public FPStaffChatSetNick(String server, UUID player, String profile) {
        super(server, player);
        this.profile = profile;
    }

    public String getProfile() {
        return profile;
    }
}