package net.firecraftmc.api.packets.staffchat;

import java.util.UUID;

public class FPSCVanishToggle extends FPacketStaffChat {
    private static final long serialVersionUID = FPacketStaffChat.serialVersionUID + 1L;
    
    public FPSCVanishToggle() {}

    public FPSCVanishToggle(String server, UUID player) {
        super(server, player);
    }
}