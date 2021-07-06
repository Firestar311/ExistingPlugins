package net.firecraftmc.api.packets.staffchat;

import net.firecraftmc.api.model.server.FirecraftServer;

import java.util.UUID;

public class FPSCIncognitoToggle extends FPacketStaffChat {

    private boolean value;
    
    public FPSCIncognitoToggle(FirecraftServer server, UUID staff, boolean value) {
        super(server.getId(), staff);
        this.value = value;
    }
    
    public boolean getValue() {
        return value;
    }
}
