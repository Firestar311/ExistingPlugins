package net.firecraftmc.api.packets;

public class FPacketMuteExpire extends FirecraftPacket {
    private static final long serialVersionUID = FirecraftPacket.serialVersionUID + 1L;
    
    private final int muteId;
    
    public FPacketMuteExpire(String server, int muteId) {
        super(server);
        this.muteId = muteId;
    }
    
    public int getMuteId() {
        return muteId;
    }
}