package com.starmediadev.lib.sql;

import java.lang.reflect.Constructor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Row {
    protected Map<String, Object> dataMap = new HashMap<>();
    protected Table table;
    
    public Row(Table table, ResultSet resultSet) {
        this.table = table;
        for (Column column : table.getColumns()) {
            try {
                Object object = resultSet.getObject(column.getName());
                this.dataMap.put(column.getName(), object);
            } catch (SQLException throwables) {}
        }
    }
    
    public Map<String, Object> getDataMap() {
        return dataMap;
    }
    
    public int getInt(String key) {
        return (int) this.dataMap.get(key);
    }
    
    public IRecord getRecord() {
        Class<? extends IRecord> recordClass = table.getDatabase().getRecordClass(table);
        try {
            Constructor<?> constructor = recordClass.getDeclaredConstructor(Row.class);
            return (IRecord) constructor.newInstance(this);
        } catch (Exception e) {}
        return null;
    }
    
    public String getString(String key) {
        return (String) this.dataMap.get(key);
    }
    
    public long getLong(String key) {
        return (long) this.dataMap.get(key);
    }
    
    public boolean getBoolean(String key) {
        return (boolean) this.dataMap.get(key);
    }
}