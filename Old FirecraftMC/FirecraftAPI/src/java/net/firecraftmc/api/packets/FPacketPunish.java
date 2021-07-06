package net.firecraftmc.api.packets;

public class FPacketPunish extends FirecraftPacket {
    private static final long serialVersionUID = FirecraftPacket.serialVersionUID + 1L;
    
    private final int punishmentId;
    
    public FPacketPunish(String server, int punishmentId) {
        super(server);
        this.punishmentId = punishmentId;
    }
    
    public int getPunishmentId() {
        return punishmentId;
    }
}