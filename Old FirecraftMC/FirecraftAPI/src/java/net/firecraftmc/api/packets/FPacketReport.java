package net.firecraftmc.api.packets;

public class FPacketReport extends FirecraftPacket{
    private static final long serialVersionUID = FirecraftPacket.serialVersionUID + 1L;

    private int reportId = 0;

    public FPacketReport() {}

    public FPacketReport(String server, int reportId) {
        super(server);
        this.reportId = reportId;
    }

    public int getReportId() {
        return reportId;
    }
}