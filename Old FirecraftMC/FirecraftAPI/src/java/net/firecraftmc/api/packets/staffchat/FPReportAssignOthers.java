package net.firecraftmc.api.packets.staffchat;

import java.util.UUID;

public class FPReportAssignOthers extends FPacketStaffChat {
    private int id;
    private String assignee;

    public FPReportAssignOthers() {
    }

    public FPReportAssignOthers(String server, UUID player, int id, String assignee) {
        super(server, player);
        this.id = id;
        this.assignee = assignee;
    }

    public int getId() {
        return id;
    }

    public String getAssignee() {
        return assignee;
    }
}