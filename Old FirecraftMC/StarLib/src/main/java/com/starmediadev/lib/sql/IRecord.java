package com.starmediadev.lib.sql;

import java.util.Map;
import java.util.function.Consumer;

/**
 * All record classes need a constructor that takes in a Row object that then maps the values
 */
public interface IRecord {
    
    int getId();
    void setId(int id);
    Map<String, Object> serialize();
    
    default IRecord push(Database database) {
        database.pushRecord(this);
        return this;
    }
    
    default IRecord push(Database database, Consumer<IRecord> consumer) {
        database.pushRecord(this);
        consumer.accept(this);
        return this;
    }
}