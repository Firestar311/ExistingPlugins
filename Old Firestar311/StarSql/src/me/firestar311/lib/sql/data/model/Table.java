package me.firestar311.lib.sql.data.model;

import me.firestar311.lib.sql.data.Statements;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@SuppressWarnings("DuplicatedCode") 
public class Table {
    protected String name;
    protected Map<String, Column> columns = new HashMap<>();
    
    public Table(String name) {
        this.name = name;
    }
    
    public Table(String name, Collection<Column> columns) {
        this(name);
        for (Column column : columns) {
            this.columns.put(column.getName(), column);
        }
    }
    
    public String generateCreationStatement() {
        //TODO Column sorting will also need to add something to ColumnInfo annotation as well
        StringBuilder colBuilder = new StringBuilder();
        Iterator<Column> columnIterator = columns.values().iterator();
        while (columnIterator.hasNext()) {
            Column column = columnIterator.next();
            colBuilder.append(column.getCreationString());
            if (columnIterator.hasNext()) {
                colBuilder.append(",");
            }
        }

        return Statements.CREATE_TABLE.replace("{name}", name).replace("{columns}", colBuilder.toString());
    }
    
    public void addColumns(Column... columns) {
        for (Column column : columns) {
            this.columns.put(column.getName(), column);
        }
    }
    
    public String getName() {
        return name;
    }
    
    public Collection<Column> getColumns() {
        return columns.values();
    }
    
    public void addColumn(Column column) {
        this.columns.put(column.getName(), column);
    }
    
    public Column getColumn(String columnName) {
        for (Column column : columns.values()) {
            if (column.getName().equalsIgnoreCase(columnName)) {
                return column;
            }
        }
        return null;
    }
}