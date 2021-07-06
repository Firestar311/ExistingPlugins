package net.firecraftmc.api.packets.staffchat;

import java.util.UUID;

public class FPReportAssignSelf extends FPacketStaffChat {
    private int id;

    public FPReportAssignSelf() {}

    public FPReportAssignSelf(String server, UUID player, int id) {
        super(server, player);
        this.id = id;
    }

    public int getId() {
        return id;
    }
}