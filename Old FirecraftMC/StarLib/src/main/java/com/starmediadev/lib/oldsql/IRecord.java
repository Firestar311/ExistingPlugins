package com.starmediadev.lib.oldsql;

public interface IRecord<T> {
    
    @SuppressWarnings("SameReturnValue")
    T getObject();
    
    @SuppressWarnings({"SameReturnValue", "unused"})
    String replaceStatementValues(String statement);
}