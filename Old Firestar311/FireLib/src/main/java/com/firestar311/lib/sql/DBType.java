package com.firestar311.lib.sql;

public enum DBType {
    MYSQL("jdbc:mysql://{hostname}:{port}/{database}", "com.mysql.jdbc.Driver"), SQLITE("jdbc:sqlite:{file}", "org.sqlite.JDBC");
    
    
    private String url, driverClass;
    
    DBType(String url, String driverClass) {
        this.url = url;
        this.driverClass = driverClass;
    }
    
    public String getUrl() {
        return url;
    }
    
    public String getDriverClass() {
        return driverClass;
    }
}