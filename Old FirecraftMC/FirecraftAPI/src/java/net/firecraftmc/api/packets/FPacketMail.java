package net.firecraftmc.api.packets;

public class FPacketMail extends FirecraftPacket {
    public static final long serialVersionUID = FirecraftPacket.serialVersionUID + 1L;
    
    private int id;
    
    public FPacketMail() {}
    
    public FPacketMail(String server, int id) {
        super(server);
        this.id = id;
    }
    
    public int getId() {
        return id;
    }
}