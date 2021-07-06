package com.starmediadev.lib.sql;

import com.starmediadev.lib.collection.IncrementalMap;

import java.util.*;

public class Table {
    protected String name;
    protected IncrementalMap<Column> columns = new IncrementalMap<>();
    protected Database database;
    
    public Table(Database database, String name) {
        this.name = name;
        this.database = database;
    }
    
    public String generateCreationStatement() {
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
    
    public Database getDatabase() {
        return database;
    }
    
    public String getName() {
        return name;
    }
    
    public Collection<Column> getColumns() {
        return columns.values();
    }
    
    public void addColumn(Column column) {
        this.columns.add(column);
    }
}