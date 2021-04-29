package net.firecraftmc.maniacore.api.records;

import net.firecraftmc.maniacore.api.audit.AuditEntry;
import net.firecraftmc.manialib.sql.Database;
import net.firecraftmc.manialib.sql.IRecord;
import net.firecraftmc.manialib.sql.Row;
import net.firecraftmc.manialib.sql.Table;

import java.util.HashMap;
import java.util.Map;

public class AuditEntryRecord implements IRecord<AuditEntry> {
    
    public static Table generateTable(Database database) {
        Table table = new Table(database, "auditentry");
        
        return table;
    }
    
    private AuditEntry object;
    
    public AuditEntryRecord(AuditEntry object) {
        this.object = object;
    }
    
    public AuditEntryRecord(Row row) {
        
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
        }};
    }
    
    public AuditEntry toObject() {
        return object;
    }
}