package net.firecraftmc.api.packets.staffchat;

import net.firecraftmc.api.model.Report;

import java.util.UUID;

public class FPReportSetStatus extends FPacketStaffChat {
    private int id;
    private Report.Status status;
    public FPReportSetStatus() {
    }

    public FPReportSetStatus(String server, UUID player, int id, Report.Status status) {
        super(server, player);
        this.id = id;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public Report.Status getStatus() {
        return status;
    }
}
