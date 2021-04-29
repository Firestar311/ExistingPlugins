package net.firecraftmc.maniacore.api.records;

import net.firecraftmc.maniacore.api.logging.entry.CmdEntry;
import net.firecraftmc.manialib.sql.Column;
import net.firecraftmc.manialib.sql.DataType;
import net.firecraftmc.manialib.sql.Database;
import net.firecraftmc.manialib.sql.Row;
import net.firecraftmc.manialib.sql.Table;

import java.util.HashMap;
import java.util.Map;

public class CmdEntryRecord extends EntryRecord {
    
    public static Table generateTable(Database database) {
        Table table = EntryRecord.generateTable(database, "command");
        Column sender = new Column("sender", DataType.INT, false, false);
        Column text = new Column("text", DataType.VARCHAR, 1000, false, false);
        table.addColumns(sender, text);
        return table;
    }
    
    public CmdEntryRecord(CmdEntry entry) {
        super(entry);
    }
    
    public CmdEntryRecord(Row row) {
        super(row);
        int id = row.getInt("id");
        long date = row.getLong("date");
        String server = row.getString("server");
        int sender = row.getInt("sender");
        String text = row.getString("text");
        this.entry = new CmdEntry(id, date, server, sender, text);
    }
    
    public Map<String, Object> serialize() {
        return new HashMap<String, Object>(super.serialize()) {{
            CmdEntry cmdEntry = (CmdEntry) toObject();
            put("sender", cmdEntry.getSender());
            put("text", cmdEntry.getText());
        }};
    }
}
