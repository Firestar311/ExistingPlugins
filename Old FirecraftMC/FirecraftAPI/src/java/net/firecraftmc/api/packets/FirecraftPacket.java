package net.firecraftmc.api.packets;

import java.io.Serializable;

public abstract class FirecraftPacket implements Serializable {
    //First Number - Version of the Packet Class (5)
    //Second Number - Plugin Version Identifier Alpha=1/Beta=2/Release=3 (1)
    //Third Number - Plugin Main Version (1)
    //Fourth Number - # of sub-packets (31)
    protected static final long serialVersionUID = 5 + 1 + 1 + 31L;

    protected String serverid;

    public FirecraftPacket() {}
    
    public FirecraftPacket(String server) {
        this.serverid = server;
    }

    public final String getServerId() {
        return serverid;
    }
}