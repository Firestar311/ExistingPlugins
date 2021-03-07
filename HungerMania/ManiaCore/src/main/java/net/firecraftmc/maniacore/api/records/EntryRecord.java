package net.firecraftmc.maniacore.api.records;

import net.firecraftmc.maniacore.api.logging.entry.Entry;
import net.firecraftmc.manialib.sql.Table;

import java.util.HashMap;
import java.util.Map;

public abstract class EntryRecord implements net.firecraftmc.manialib.sql.IRecord<Entry> {
    
    protected Entry entry;
    
    public static net.firecraftmc.manialib.sql.Table generateTable(net.firecraftmc.manialib.sql.Database database, String name) {
        net.firecraftmc.manialib.sql.Table table = new Table(database, name);
        net.firecraftmc.manialib.sql.Column id = new net.firecraftmc.manialib.sql.Column("id", net.firecraftmc.manialib.sql.DataType.INT, true, true);
        net.firecraftmc.manialib.sql.Column date = new net.firecraftmc.manialib.sql.Column("date", net.firecraftmc.manialib.sql.DataType.BIGINT, false, false);
        net.firecraftmc.manialib.sql.Column server = new net.firecraftmc.manialib.sql.Column("server", net.firecraftmc.manialib.sql.DataType.VARCHAR, 64, false, false);
        table.addColumns(id, date, server);
        return table;
    }
    
    public EntryRecord(Entry entry) {
        this.entry = entry;
    }
    
    @SuppressWarnings("unused")
    public EntryRecord(net.firecraftmc.manialib.sql.Row row) {}
    
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
