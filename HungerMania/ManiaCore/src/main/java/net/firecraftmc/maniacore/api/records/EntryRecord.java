package net.firecraftmc.maniacore.api.records;

import net.firecraftmc.maniacore.api.logging.entry.Entry;
import net.firecraftmc.manialib.sql.Column;
import net.firecraftmc.manialib.sql.DataType;
import net.firecraftmc.manialib.sql.Database;
import net.firecraftmc.manialib.sql.IRecord;
import net.firecraftmc.manialib.sql.Row;
import net.firecraftmc.manialib.sql.Table;

import java.util.HashMap;
import java.util.Map;

public abstract class EntryRecord implements IRecord<Entry> {
    
    protected Entry entry;
    
    public static Table generateTable(Database database, String name) {
        Table table = new Table(database, name);
        Column id = new Column("id", DataType.INT, true, true);
        Column date = new Column("date", DataType.BIGINT, false, false);
        Column server = new Column("server", DataType.VARCHAR, 64, false, false);
        table.addColumns(id, date, server);
        return table;
    }
    
    public EntryRecord(Entry entry) {
        this.entry = entry;
    }
    
    @SuppressWarnings("unused")
    public EntryRecord(Row row) {}
    
    public int getId() {
        return entry.getId();
    }
    
    public void setId(int id) {
        entry.setId(id);
    }
    
    public Map<String, Object> serialize() {
        return new HashMap<String, Object>() {{
            put("id", entry.getId());
            put("date", entry.getDate());
            put("server", entry.getServer());
        }};
    }
    
    public Entry toObject() {
        return entry;
    }
}
