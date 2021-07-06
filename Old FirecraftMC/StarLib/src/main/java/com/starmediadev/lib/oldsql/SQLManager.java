package com.starmediadev.lib.oldsql;

import com.starmediadev.lib.sql.DBType;
import com.starmediadev.lib.sql.Statements;
import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.lang.reflect.*;
import java.sql.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

public class SQLManager {
    
    protected DBType type;
    protected Connection connection;
    protected JavaPlugin plugin;
    protected int port;
    protected Queue<IRecord> queue = new LinkedBlockingQueue<>();
    protected Set<Class<? extends IRecord>> tableRecords = new HashSet<>();
    protected String user, database, password, hostname;
    protected File sqliteFile, logFile;
    
    public SQLManager(JavaPlugin plugin, DBType type, ConfigurationSection dbSection) {
        this.plugin = plugin;
        this.type = type;
        
        logFile = new File(plugin.getDataFolder(), "queries.txt");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                System.out.println("Could not create the queries.txt file.");
            }
        }
        
        if (type.equals(DBType.SQLITE)) {
            String databaseName = dbSection.getString("sqlite.name");
            sqliteFile = new File(plugin.getDataFolder() + File.separator + databaseName + ".db");
            if (!sqliteFile.exists()) {
                try {
                    sqliteFile.createNewFile();
                } catch (IOException e) {
                    plugin.getLogger().severe("Could not create the database file " + sqliteFile.getName());
                }
            }
        } else if (type.equals(DBType.MYSQL)) {
            user = dbSection.getString("mysql.username");
            database = dbSection.getString("mysql.database");
            password = dbSection.getString("mysql.password");
            hostname = dbSection.getString("mysql.hostname");
            port = dbSection.getInt("mysql.port");
        }
    }
    
    public void addRecordToQueue(IRecord record) {
        this.queue.add(record);
    }
    
    public void createTables(Consumer<Boolean> consumer) {
        new Thread() {{
            List<String> statements = new ArrayList<>();
            for (Class<? extends IRecord> recordType : tableRecords) {
                StringBuilder colBuilder = new StringBuilder();
                Field[] declaredFields = recordType.getDeclaredFields();
                for (int i = 0; i < declaredFields.length; i++) {
                    Field field = declaredFields[i];
                    field.setAccessible(true);
                    if (Modifier.isStatic(field.getModifiers())) { continue; }
                    String colName = "";
                    
                    RecordField recordField;
                    if (field.isAnnotationPresent(RecordField.class)) {
                        recordField = field.getAnnotation(RecordField.class);
                    } else {
                        plugin.getLogger().info("Field " + field.getName() + " does not have record field annotation");
                        continue;
                    }
                    
                    if (recordField != null) {
                        colName = recordField.colName();
                    }
                    
                    if (StringUtils.isEmpty(colName)) {
                        colName = field.getName().toLowerCase();
                    }
                    
                    String colType;
                    if (!StringUtils.isEmpty(recordField.colType())) {
                        colType = recordField.colType();
                    } else {
                        Class<?> fieldType = field.getType();
                        //System.out.println(fieldType.getSimpleName());
                        if (fieldType.isAssignableFrom(String.class)) {
                            colType = "VARCHAR(1000)";
                        } else if (fieldType.isAssignableFrom(int.class)) {
                            colType = "INT";
                        } else if (fieldType.isAssignableFrom(long.class)) {
                            colType = "BIGINT";
                        } else if (fieldType.isAssignableFrom(boolean.class)) {
                            colType = "VARCHAR(5)";
                        } else if (fieldType.isAssignableFrom(float.class)) {
                            colType = "REAL";
                        } else if (fieldType.isAssignableFrom(byte.class)) {
                            colType = "TINYINT";
                        } else if (fieldType.isAssignableFrom(short.class)) {
                            colType = "SMALLINT";
                        } else if (fieldType.isAssignableFrom(double.class)) {
                            colType = "DOUBLE";
                        } else if (fieldType.isAssignableFrom(UUID.class)) {
                            colType = "VARCHAR(36)";
                        } else {
                            colType = "VARCHAR(1000)";
                        }
                    }
                    
                    if (StringUtils.isEmpty(colType)) {
                        plugin.getLogger().info("colType is empty for field " + field.getName());
                        continue;
                    }
                    
                    colBuilder.append(Statements.COLUMN_FORMAT.replace("{colName}", colName).replace("{colType}", colType));
                    if (recordField.unique()) {
                        colBuilder.append(" UNIQUE");
                    }
                    if (i < (declaredFields.length - 1)) {
                        colBuilder.append(",");
                    }
                }
                
                TableInfo tableInfo = null;
                if (recordType.isAnnotationPresent(TableInfo.class)) {
                    tableInfo = recordType.getAnnotation(TableInfo.class);
                }
                
                String tableName = "";
                if (tableInfo != null) {
                    tableName = tableInfo.name();
                }
                
                if (StringUtils.isEmpty(tableName)) {
                    tableName = recordType.getSimpleName().toLowerCase();
                }
                
                String sql = Statements.CREATE_TABLE.replace("{name}", tableName).replace("{columns}", colBuilder.toString());
                statements.add(sql);
            }
            
            if (establishConnection()) {
                try {
                    Statement statement = connection.createStatement();
                    for (String sql : statements) {
                        addQueryToLog(sql);
                        try {
                            //System.out.println(sql);
                            statement.execute(sql);
                        } catch (SQLException e) {
                            if (!e.getMessage().contains("already exists")) { e.printStackTrace(); }
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            consumer.accept(true);
        }}.start();
    }
    
    public boolean establishConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            connection = null;
        }
        
        String connectionURL = "";
        try {
            connectionURL = switch (type) {
                case MYSQL -> type.getUrl().replace("{hostname}", this.hostname).replace("{port}", this.port + "").replace("{database}", this.database);
                case SQLITE -> type.getUrl().replace("{file}", this.sqliteFile + "");
            };
            
            Class.forName(type.getDriverClass());
            switch (type) {
                case MYSQL -> connection = DriverManager.getConnection(connectionURL, this.user, this.password);
                case SQLITE -> connection = DriverManager.getConnection(connectionURL);
            }
            return true;
        } catch (ClassNotFoundException e) {
            plugin.getLogger().severe("Could not find a Connecton Driver");
        } catch (SQLException e) {
            plugin.getLogger().severe("Could not connect to the database at " + connectionURL);
        }
        return false;
    }
    
    public void registerTableRecord(Class<? extends IRecord> recordClass) {
        this.tableRecords.add(recordClass);
    }
    
    public void pushQueueChanges() {
        this.establishConnection();
        if (connection == null) return;
        for (IRecord record : queue) {
            String table;
            if (!record.getClass().isAnnotationPresent(TableInfo.class)) {
                table = record.getClass().getSimpleName().toLowerCase();
            } else {
                TableInfo tableInfo = record.getClass().getAnnotation(TableInfo.class);
                table = tableInfo.name();
            }
            
            Map<String, Object> data = new HashMap<>();
            boolean insert = true;
            String uniqueCol = "";
            Object uniqueValue = null;
            
            Field[] declaredFields = record.getClass().getDeclaredFields();
            for (Field field : declaredFields) { //TODO Change detection for already inserted values
                field.setAccessible(true);
                if (!field.isAnnotationPresent(RecordField.class)) { continue; }
                RecordField recordField = field.getAnnotation(RecordField.class);
                String colName = "";
                if (!StringUtils.isEmpty(recordField.colName())) {
                    colName += recordField.colName();
                } else {
                    colName += field.getName().toLowerCase();
                }
        
                try {
                    Object value = field.get(record);
                    data.put(colName, value);
                    if (StringUtils.isEmpty(uniqueCol) && uniqueValue == null) {
                        if (recordField.unique()) {
                            uniqueCol = colName;
                            uniqueValue = value;
                            Statement statement = connection.createStatement();
                            ResultSet set = statement.executeQuery("select * from " + table + " where " + colName + " = " + value);
                            if (set.next()) {
                                insert = false;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            
            String sql;
            if (insert) {
                StringBuilder colBuilder = new StringBuilder(), valueBuilder = new StringBuilder();
                Iterator<Entry<String, Object>> iterator = data.entrySet().iterator();
                while (iterator.hasNext()) {
                    Entry<String, Object> entry = iterator.next();
                    colBuilder.append("`").append(entry.getKey()).append("`");
                    valueBuilder.append("\"").append(entry.getValue()).append("\"");
                    if (iterator.hasNext()) {
                        colBuilder.append(",");
                        valueBuilder.append(",");
                    }
                }
                sql = Statements.INSERT.replace("{name}", table).replace("{columns}", colBuilder.toString()).replace("{values}", valueBuilder.toString());
            } else {
                StringBuilder builder = new StringBuilder();
                Iterator<Entry<String, Object>> iterator = data.entrySet().iterator();
                while (iterator.hasNext()) {
                    Entry<String, Object> entry = iterator.next();
                    String value = Statements.UPDATE_VALUE.replace("{column}", entry.getKey()).replace("{value}", "\"" + entry.getValue() + "\"");
                    builder.append(value);
                    if (iterator.hasNext()) {
                        builder.append(",");
                    }
                }
                
                String whereValue = Statements.UPDATE_VALUE.replace("{column}", uniqueCol).replace("{value}", uniqueValue + "");
                sql = Statements.UPDATE.replace("{name}", table).replace("{values}", builder.toString()).replace("{location}", whereValue);
            }
            
            
            try {
                if (connection == null) return;
                Statement statement = connection.createStatement();
                //addQueryToLog(sql);
                //plugin.getLogger().info(sql);
                statement.executeUpdate(sql);
            } catch (Exception e) {
            
            }
        }
    }
    
    public Set<IRecord> getRecords(String tableName, ResultSet resultSet) throws Exception {
        Class<? extends IRecord> recordClass = null;
        for (Class<? extends IRecord> registeredRecord : this.tableRecords) {
            if (registeredRecord.isAnnotationPresent(TableInfo.class)) {
                TableInfo recordTableInfo = registeredRecord.getAnnotation(TableInfo.class);
                if (!StringUtils.isEmpty(recordTableInfo.name())) {
                    if (recordTableInfo.name().equalsIgnoreCase(tableName)) {
                        recordClass = registeredRecord;
                        break;
                    }
                }
            } else if (registeredRecord.getSimpleName().equalsIgnoreCase(tableName)) {
                recordClass = registeredRecord;
                break;
            }
        }
        
        Set<IRecord> records = new HashSet<>();
        while (true) {
            try {
                if (!resultSet.next()) { break; }
            } catch (SQLException e) {
                e.printStackTrace();
            }
    
            Constructor<?> constructor = recordClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            IRecord recordObject = (IRecord) constructor.newInstance();
            for (Field field : recordClass.getDeclaredFields()) {
                field.setAccessible(true);
                String colName;
                if (field.isAnnotationPresent(RecordField.class)) {
                    RecordField recordField = field.getAnnotation(RecordField.class);
                    if (!StringUtils.isEmpty(recordField.colName())) {
                        colName = recordField.colName();
                    } else {
                        colName = field.getName();
                    }
                } else {
                    continue;
                }
                
                Object object = resultSet.getObject(colName);
                field.set(recordObject, object);
            }
            records.add(recordObject);
        }
        
        return records;
    }
    
    private void addQueryToLog(String query) {
        new Thread(() -> {
            try {
                PrintWriter printWriter = new PrintWriter(new FileOutputStream(logFile));
                printWriter.println(query);
                printWriter.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });
    }
    
    public Connection getConnection() {
        return connection;
    }
    
    /*
        This is a method from the old way of databases, putting this here so I don't forget to rewrite it
        Rewrite Tasks
        - Move the statement text to the Statements class
        - Use another thread
        - Have a consumer as an argument for the multithreading aspect
         */
    private int getNextAutoId(String table) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT `AUTO_INCREMENT` FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ?;");
            statement.setString(1, database);
            statement.setString(2, table);
            statement.closeOnCompletion();
            ResultSet set = statement.executeQuery();
            set.next();
            return set.getInt("AUTO_INCREMENT");
        } catch (Exception e) {
        }
        return 0;
    }
}