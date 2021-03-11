package net.firecraftmc.maniacore.api.records;

import net.firecraftmc.maniacore.api.logging.entry.ChatEntry;
import net.firecraftmc.manialib.sql.Table;

import java.util.HashMap;
import java.util.Map;

public class ChatEntryRecord extends net.firecraftmc.maniacore.api.records.EntryRecord {
    
    public static net.firecraftmc.manialib.sql.Table generateTable(net.firecraftmc.manialib.sql.Database database) {
        Table table = EntryRecord.generateTable(database, "chat");
        net.firecraftmc.manialib.sql.Column sender = new net.firecraftmc.manialib.sql.Column("sender", net.firecraftmc.manialib.sql.DataType.INT, false, false);
        net.firecraftmc.manialib.sql.Column text = new net.firecraftmc.manialib.sql.Column("text", net.firecraftmc.manialib.sql.DataType.VARCHAR, 1000, false, false);
        net.firecraftmc.manialib.sql.Column channel = new net.firecraftmc.manialib.sql.Column("channel", net.firecraftmc.manialib.sql.DataType.VARCHAR, 32, false, false);
        table.addColumns(sender, text, channel);
        return table;
    }
    
    public ChatEntryRecord(ChatEntry entry) {
        super(entry);
    }
    
    public ChatEntryRecord(net.firecraftmc.manialib.sql.Row row) {
        super(row);
        int id = row.getInt("id");
        long date = row.getLong("date");
        String server = row.getString("server");
        int sender = row.getInt("sender");
        String text = row.getString("text");
        String channel = row.getString("channel");
        this.entry = new ChatEntry(id, date, server, sender, text, channel);
    }
    
    public Map<String, Object> serialize() {
        return new HashMap<String, Object>(super.serialize()) {{
            ChatEntry chatEntry = (ChatEntry) toObject();
            put("sender", chatEntry.getSender());
            put("text", chatEntry.getText());
            put("channel", chatEntry.getChannel());
        }};
    }
}
