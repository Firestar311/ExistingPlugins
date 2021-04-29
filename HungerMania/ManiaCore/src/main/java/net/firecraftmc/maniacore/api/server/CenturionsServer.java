package net.firecraftmc.maniacore.api.server;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CenturionsServer {

    private int id;
    private String name;
    private int port;
    private ServerType type = ServerType.UNKNOWN;
    private int serverNumber = 1;
    private NetworkType networkType = NetworkType.UNKNOWN;

    public CenturionsServer(String name, int port) {
        this.name = name;
        this.port = port;
    }

    public CenturionsServer(int id, String name, int port, ServerType type, int serverNumber) {
        this.id = id;
        this.name = name;
        this.port = port;
        this.type = type;
        this.serverNumber = serverNumber;
    }
}
