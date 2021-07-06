package me.firestar311.lib.sql;

import me.firestar311.lib.sql.data.DatabaseManager;

public final class StarSql {
    private static final StarSql instance = new StarSql();
    public static StarSql getInstance() { return instance; }
    private StarSql() {
        
    }
    
    private static final DatabaseManager databaseManager = new DatabaseManager();
    public static DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
}
