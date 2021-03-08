package net.firecraftmc.maniacore.api.records;

import net.firecraftmc.maniacore.api.friends.FriendNotification;
import net.firecraftmc.maniacore.api.friends.FriendNotification.Type;
import net.firecraftmc.manialib.sql.Table;

import java.util.HashMap;
import java.util.Map;

public class FriendNotificationRecord implements net.firecraftmc.manialib.sql.IRecord<FriendNotification> {
    
    public static net.firecraftmc.manialib.sql.Table generateTable(net.firecraftmc.manialib.sql.Database database) {
        net.firecraftmc.manialib.sql.Table table = new Table(database, "friendnotifications");
        table.addColumn("id", net.firecraftmc.manialib.sql.DataType.INT, true, true);
        table.addColumn("type", net.firecraftmc.manialib.sql.DataType.VARCHAR, 12);
        table.addColumn("sender", net.firecraftmc.manialib.sql.DataType.VARCHAR, 36);
        table.addColumn("target", net.firecraftmc.manialib.sql.DataType.VARCHAR, 36);
        table.addColumn("timestamp", net.firecraftmc.manialib.sql.DataType.BIGINT);
        return table;
    }
    
    private FriendNotification object;
    
    public FriendNotificationRecord(FriendNotification object) {
        this.object = object;
    }
    
    public FriendNotificationRecord(net.firecraftmc.manialib.sql.Row row) {
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