package net.firecraftmc.maniacore.api.records;

import net.firecraftmc.maniacore.api.user.toggle.Toggle;
import net.firecraftmc.manialib.sql.Table;

import java.util.*;

public class ToggleRecord implements net.firecraftmc.manialib.sql.IRecord<Toggle> {
    
    /*
    private int id; //Database purposes
    private UUID uuid;
    private String name, value, defaultValue;
     */
    
    public static net.firecraftmc.manialib.sql.Table generateTable(net.firecraftmc.manialib.sql.Database database) {
        net.firecraftmc.manialib.sql.Table table = new Table(database, "toggles");
        table.addColumn("id", net.firecraftmc.manialib.sql.DataType.INT, true, true);
        table.addColumn("uuid", net.firecraftmc.manialib.sql.DataType.VARCHAR, 36);
        table.addColumn("name", net.firecraftmc.manialib.sql.DataType.VARCHAR, 50);
        table.addColumn("value", net.firecraftmc.manialib.sql.DataType.VARCHAR, 100);
        return table;
    }
    
    private Toggle object;
    
    public ToggleRecord(Toggle object) {
        this.object = object;
    }
    
    public ToggleRecord(net.firecraftmc.manialib.sql.Row row) {
        int id = row.getInt("id");
        UUID uuid = row.getUUID("uuid");
        String name = row.getString("name");
        String value = row.getString("value");
        this.object = new Toggle(id, uuid, name, value);
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
            put("uuid", object.getUuid().toString());
            put("name", object.getName());
            put("value", object.getValue());
        }};
    }
    
    public Toggle toObject() {
        return object;
    }
}