package net.firecraftmc.maniacore.api.records;

import net.firecraftmc.maniacore.api.user.IgnoreInfo;
import net.firecraftmc.manialib.sql.Column;
import net.firecraftmc.manialib.sql.DataType;
import net.firecraftmc.manialib.sql.Database;
import net.firecraftmc.manialib.sql.IRecord;
import net.firecraftmc.manialib.sql.Row;
import net.firecraftmc.manialib.sql.Table;

import java.util.*;

public class IgnoreInfoRecord implements IRecord<IgnoreInfo> {
    
    public static Table generateTable(Database database) {
        Table table = new Table(database, "ignoredPlayers");
        table.addColumn(new Column("id", DataType.INT, true, true));
        table.addColumn(new Column("player", DataType.VARCHAR, 36));
        table.addColumn(new Column("ignored", DataType.VARCHAR, 36));
        table.addColumn(new Column("timestamp", DataType.BIGINT));
        table.addColumn(new Column("ignoredName", DataType.VARCHAR, 32));
        return table;
    }
    
    private IgnoreInfo ignoreInfo;
    
    public IgnoreInfoRecord(IgnoreInfo ignoreInfo) {
        this.ignoreInfo = ignoreInfo;
    }
    
    public IgnoreInfoRecord(Row row) {
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