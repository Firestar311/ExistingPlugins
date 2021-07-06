package net.firecraftmc.api.packets;

public class FPacketAcknowledgeWarning extends FirecraftPacket {
    private static final long serialVersionUID = FirecraftPacket.serialVersionUID + 1;
    
    private final String warnedName;
    public FPacketAcknowledgeWarning(String server, String name) {
        super(server);
        this.warnedName = name;
    }
    
    public String getWarnedName() {
        return warnedName;
    }
}