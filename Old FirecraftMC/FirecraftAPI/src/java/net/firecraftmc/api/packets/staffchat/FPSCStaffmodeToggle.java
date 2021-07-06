package net.firecraftmc.api.packets.staffchat;

import java.util.UUID;

public class FPSCStaffmodeToggle extends FPacketStaffChat {
    private static final long serialVersionUID = FPacketStaffChat.serialVersionUID + 1L;

    private boolean value;

    public FPSCStaffmodeToggle() {}

    public FPSCStaffmodeToggle(String server, UUID player, boolean value) {
        super(server, player);
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }
}
