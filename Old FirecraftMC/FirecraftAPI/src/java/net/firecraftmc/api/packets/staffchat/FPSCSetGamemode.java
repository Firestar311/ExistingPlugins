package net.firecraftmc.api.packets.staffchat;

import org.bukkit.GameMode;

import java.util.UUID;

public class FPSCSetGamemode extends FPacketStaffChat {
    private static final long serialVersionUID = FPacketStaffChat.serialVersionUID + 1L;
    
    private GameMode mode;

    public FPSCSetGamemode() {}

    public FPSCSetGamemode(String server, UUID player, GameMode mode) {
        super(server, player);
        this.mode = mode;
    }
    
    public GameMode getMode() {
        return mode;
    }
}