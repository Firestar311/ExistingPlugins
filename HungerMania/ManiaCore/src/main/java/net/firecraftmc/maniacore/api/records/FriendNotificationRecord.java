package net.firecraftmc.maniacore.api.records;

import net.firecraftmc.maniacore.api.friends.FriendNotification;
import net.firecraftmc.maniacore.api.friends.FriendNotification.Type;
import net.firecraftmc.manialib.sql.DataType;
import net.firecraftmc.manialib.sql.Database;
import net.firecraftmc.manialib.sql.IRecord;
import net.firecraftmc.manialib.sql.Row;
import net.firecraftmc.manialib.sql.Table;

import java.util.HashMap;
import java.util.Map;

public class FriendNotificationRecord implements IRecord<FriendNotification> {
    
    public static Table generateTable(Database database) {
        Table table = new Table(database, "friendnotifications");
        table.addColumn("id", DataType.INT, true, true);
        table.addColumn("type", DataType.VARCHAR, 12);
        table.addColumn("sender", DataType.VARCHAR, 36);
        table.addColumn("target", DataType.VARCHAR, 36);
        table.addColumn("timestamp", DataType.BIGINT);
        return table;
    }
    
    private FriendNotification object;
    
    public FriendNotificationRecord(FriendNotification object) {
        this.object = object;
    }
    
    public FriendNotificationRecord(Row row) {
        this.object = FriendNotification.builder().id(row.getInt("id")).type(Type.valueOf(row.getString("type"))).sender(row.getUUID("sender")).target(row.getUUID("target")).timestamp(row.getLong("timestamp")).build();
    }
    
    public int getId() {
        return object.getId();
    }
    
    public void setId(int id) {
        object.setId(id);
    }
    
    public Map<String, Object> serialize() {
        return new HashMap<String, Object>() {{
            put("id", object.getId());
            put("type", object.getType().name());
            put("sender", object.getSender().toString());
            put("target", object.getTarget().toString());
            put("timestamp", object.getTimestamp());
        }};
    }
    
    public FriendNotification toObject() {
        return object;
    }
}