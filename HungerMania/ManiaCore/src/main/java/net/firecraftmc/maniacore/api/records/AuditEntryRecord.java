package net.firecraftmc.maniacore.api.records;

import net.firecraftmc.maniacore.api.audit.AuditEntry;
import net.firecraftmc.manialib.sql.Table;

import java.util.HashMap;
import java.util.Map;

public class AuditEntryRecord implements net.firecraftmc.manialib.sql.IRecord<AuditEntry> {
    
    public static net.firecraftmc.manialib.sql.Table generateTable(net.firecraftmc.manialib.sql.Database database) {
        net.firecraftmc.manialib.sql.Table table = new Table(database, "auditentry");
        
        return table;
    }
    
    private AuditEntry object;
    
    public AuditEntryRecord(AuditEntry object) {
        this.object = object;
    }
    
    public AuditEntryRecord(net.firecraftmc.manialib.sql.Row row) {
        
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