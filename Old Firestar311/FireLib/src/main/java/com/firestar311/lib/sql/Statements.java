package com.firestar311.lib.sql;

public final class Statements {
    private Statements() {}
    
    public static final String CREATE_DATABSE = "CREATE DATABASE {name};";
    public static final String CREATE_TABLE = "CREATE TABLE `{name}` ({columns}) ENGINE=InnoDB;";
    public static final String COLUMN_FORMAT = "`{colName}` {colType}";
    public static final String INSERT = "INSERT INTO {name}({columns}) VALUES({values});";
}
