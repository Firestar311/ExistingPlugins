package net.firecraftmc.api.packets.staffchat;

import java.util.UUID;

public class FPSCTeleport extends FPacketStaffChat {
    private static final long serialVersionUID = FPacketStaffChat.serialVersionUID + 1L;
    
    private UUID target;
    
    public FPSCTeleport() {}

    public FPSCTeleport(String server, UUID player, UUID target) {
        super(server, player);
        this.target = target;
    }
    
    public UUID getTarget() {
        return target;
    }
}