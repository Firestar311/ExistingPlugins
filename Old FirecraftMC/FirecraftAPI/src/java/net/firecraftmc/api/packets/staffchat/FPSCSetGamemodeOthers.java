package net.firecraftmc.api.packets.staffchat;

import org.bukkit.GameMode;

import java.util.UUID;

public class FPSCSetGamemodeOthers extends FPacketStaffChat {
    private static final long serialVersionUID = FPacketStaffChat.serialVersionUID + 1L;
    
    private GameMode mode;
    private UUID target;

    public FPSCSetGamemodeOthers() {}

    public FPSCSetGamemodeOthers(String server, UUID player, GameMode mode, UUID target) {
        super(server, player);
        this.mode = mode;
        this.target = target;
    }
    
    public GameMode getMode() {
        return mode;
    }
    
    public UUID getTarget() {
        return target;
    }
}