package com.firestar311.lib.sql;

public interface IRecord<T> {
    
    String replaceStatementValues(String statement);
}