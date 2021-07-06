package net.firecraftmc.api.packets.staffchat;

import java.util.UUID;

public class FPSCTeleportOthers extends FPacketStaffChat {
    private static final long serialVersionUID = FPacketStaffChat.serialVersionUID + 1L;
    
    private UUID target1;
    private UUID target2;
    
    public FPSCTeleportOthers() {}

    public FPSCTeleportOthers(String server, UUID player, UUID target1, UUID target2) {
        super(server, player);
        this.target1 = target1;
        this.target2 = target2;
    }
    
    public UUID getTarget1() {
        return target1;
    }
    
    public UUID getTarget2() {
        return target2;
    }
}