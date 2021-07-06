package net.firecraftmc.api.packets;

public class FPacketPunishRemove extends FirecraftPacket {
    private static final long serialVersionUID = FirecraftPacket.serialVersionUID + 1L;
    
    private final int punishmentId;
    
    public FPacketPunishRemove(String server, int punishmentId) {
        super(server);
        this.punishmentId = punishmentId;
    }
    
    public int getPunishmentId() {
        return punishmentId;
    }
}