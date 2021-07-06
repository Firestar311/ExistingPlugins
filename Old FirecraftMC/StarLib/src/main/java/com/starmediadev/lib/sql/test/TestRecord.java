package com.starmediadev.lib.sql.test;

import com.starmediadev.lib.sql.IRecord;
import com.starmediadev.lib.sql.Row;

import java.util.HashMap;
import java.util.Map;

public class TestRecord implements IRecord {
    
    private int id;
    private String string;
    
    public TestRecord(Row row) {
        this.id = (int) row.getDataMap().get("id");
        this.string = (String) row.getDataMap().get("string");
    }
    
    public TestRecord(String string) {
        this.string = string;
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
            put("name", string);
        }};
    }
}