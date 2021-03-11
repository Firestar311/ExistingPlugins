package net.firecraftmc.maniacore.api.records;

import net.firecraftmc.maniacore.api.user.IgnoreInfo;
import net.firecraftmc.manialib.sql.Table;

import java.util.*;

public class IgnoreInfoRecord implements net.firecraftmc.manialib.sql.IRecord<IgnoreInfo> {
    
    public static net.firecraftmc.manialib.sql.Table generateTable(net.firecraftmc.manialib.sql.Database database) {
        net.firecraftmc.manialib.sql.Table table = new Table(database, "ignoredPlayers");
        table.addColumn(new net.firecraftmc.manialib.sql.Column("id", net.firecraftmc.manialib.sql.DataType.INT, true, true));
        table.addColumn(new net.firecraftmc.manialib.sql.Column("player", net.firecraftmc.manialib.sql.DataType.VARCHAR, 36));
        table.addColumn(new net.firecraftmc.manialib.sql.Column("ignored", net.firecraftmc.manialib.sql.DataType.VARCHAR, 36));
        table.addColumn(new net.firecraftmc.manialib.sql.Column("timestamp", net.firecraftmc.manialib.sql.DataType.BIGINT));
        table.addColumn(new net.firecraftmc.manialib.sql.Column("ignoredName", net.firecraftmc.manialib.sql.DataType.VARCHAR, 32));
        return table;
    }
    
    private IgnoreInfo ignoreInfo;
    
    public IgnoreInfoRecord(IgnoreInfo ignoreInfo) {
        this.ignoreInfo = ignoreInfo;
    }
    
    public IgnoreInfoRecord(net.firecraftmc.manialib.sql.Row row) {
        int id = row.getInt("id");
        UUID player = UUID.fromString(row.getString("player"));
        UUID ignored = UUID.fromString(row.getString("ignored"));
        long timestamp = row.getLong("timestamp");
        String ignoredName = row.getString("ignoredName");
        this.ignoreInfo = new IgnoreInfo(id, player, ignored, timestamp, ignoredName);
    }
    
    public int getId() {
        return ignoreInfo.getId();
    }
    
    public void setId(int id) {
        ignoreInfo.setId(id);
    }
    
    public Map<String, Object> serialize() {
        return new HashMap<String, Object>() {{
            put("id", ignoreInfo.getId());
            put("player", ignoreInfo.getPlayer().toString());
            put("ignored", ignoreInfo.getIgnored().toString());
            put("timestamp", ignoreInfo.getTimestamp());
            put("ignoredName", ignoreInfo.getIgnoredName());
        }};
    }
    
    public IgnoreInfo toObject() {
        return ignoreInfo;
    }
}