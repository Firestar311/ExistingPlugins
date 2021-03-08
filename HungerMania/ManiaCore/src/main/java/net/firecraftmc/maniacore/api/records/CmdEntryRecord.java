package net.firecraftmc.maniacore.api.records;

import net.firecraftmc.maniacore.api.logging.entry.CmdEntry;
import net.firecraftmc.manialib.sql.Table;

import java.util.HashMap;
import java.util.Map;

public class CmdEntryRecord extends net.firecraftmc.maniacore.api.records.EntryRecord {
    
    public static net.firecraftmc.manialib.sql.Table generateTable(net.firecraftmc.manialib.sql.Database database) {
        Table table = EntryRecord.generateTable(database, "command");
        net.firecraftmc.manialib.sql.Column sender = new net.firecraftmc.manialib.sql.Column("sender", net.firecraftmc.manialib.sql.DataType.INT, false, false);
        net.firecraftmc.manialib.sql.Column text = new net.firecraftmc.manialib.sql.Column("text", net.firecraftmc.manialib.sql.DataType.VARCHAR, 1000, false, false);
        table.addColumns(sender, text);
        return table;
    }
    
    public CmdEntryRecord(CmdEntry entry) {
        super(entry);
    }
    
    public CmdEntryRecord(net.firecraftmc.manialib.sql.Row row) {
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
