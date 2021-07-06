package net.firecraftmc.api.packets.staffchat;

import net.firecraftmc.api.model.Report;

import java.util.UUID;

public class FPReportSetOutcome extends FPacketStaffChat {
    private int id;
    private Report.Outcome outcome;
    public FPReportSetOutcome() {
    }

    public FPReportSetOutcome(String server, UUID player, int id, Report.Outcome outcome) {
        super(server, player);
        this.id = id;
        this.outcome = outcome;
    }

    public int getId() {
        return id;
    }

    public Report.Outcome getOutcome() {
        return outcome;
    }
}