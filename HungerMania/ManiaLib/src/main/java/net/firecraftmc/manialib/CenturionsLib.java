package net.firecraftmc.manialib;

import net.firecraftmc.manialib.data.DatabaseManager;
import net.firecraftmc.manialib.data.MysqlDatabase;
import net.firecraftmc.manialib.data.model.DatabaseHandler;
import net.firecraftmc.manialib.sql.Database;

import java.util.Properties;
import java.util.logging.Logger;

public class CenturionsLib {
    private MysqlDatabase mysqlDatabase;
    private Database database;
    private static CenturionsLib instance;
    private Logger logger;
    
    private DatabaseManager databaseManager = DatabaseManager.getInstance();
    
    public CenturionsLib(Properties databaseProperties, Logger logger) {
        this.database = new Database(databaseProperties, logger);
        this.databaseManager.registerDatabase(this.mysqlDatabase = new MysqlDatabase(databaseProperties, logger, databaseManager));
        this.logger = logger;
        instance = this;
    }

    public MysqlDatabase getMysqlDatabase() {
        return mysqlDatabase;
    }

    public void addDatabaseHandler(DatabaseHandler databaseHandler) {
        this.databaseManager.addDatabaseHandler(databaseHandler);
    }
    
    public void init() {
        this.databaseManager.registerDatabases();
        this.databaseManager.registerTypeHandlers();
        this.databaseManager.registerRecordTypes();
    }
    
    public CenturionsLib(Logger logger) {
        this.logger = logger;
    }
    
    public static CenturionsLib getInstance() {
        return instance;
    }
    
    public Database getDatabase() {
        return database;
    }
    
    public Logger getLogger() {
        return logger;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
}