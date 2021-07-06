package net.firecraftmc.api.packets.staffchat;

import net.firecraftmc.api.packets.FirecraftPacket;

import java.util.UUID;

public abstract class FPacketStaffChat extends FirecraftPacket {
    
    //First Number - SerialID from FirecraftPacket
    //Second Number - Version of this class
    //Third Number - # of sub-packets (7)
    protected static final long serialVersionUID = FirecraftPacket.serialVersionUID + 1 + 7;

    private UUID player;

    public FPacketStaffChat() {}

    public FPacketStaffChat(String server, UUID player) {
        this.serverid = server;
        this.player = player;
    }

    public UUID getPlayer() {
        return player;
    }
}