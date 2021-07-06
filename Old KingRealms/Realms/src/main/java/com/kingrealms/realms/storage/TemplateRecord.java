package com.kingrealms.realms.storage;

import com.starmediadev.lib.sql.IRecord;
import com.starmediadev.lib.sql.Row;

import java.util.HashMap;
import java.util.Map;

public class TemplateRecord implements IRecord {
    
    private int id;
    
    public TemplateRecord(Row row) {
        this.id = row.getInt("id");
    }
    
    @Override
    public int getId() {
        return id;
    }
    
    @Override
    public void setId(int id) {
        this.id = id;
    }
    
    @Override
    public Map<String, Object> serialize() {
        return new HashMap<>() {{
            put("id", id);
        }};
    }
}