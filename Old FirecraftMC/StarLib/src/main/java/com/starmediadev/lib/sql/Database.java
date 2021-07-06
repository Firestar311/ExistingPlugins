package com.starmediadev.lib.sql;

import org.apache.commons.lang.StringUtils;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.LinkedBlockingQueue;

public class Database {
    protected Connection connection;
    protected String host, username, password, database;
    protected JavaPlugin plugin;
    protected int port;
    protected Queue<IRecord> recordQueue = new LinkedBlockingQueue<>();
    protected Set<IRecord> records = new HashSet<>();
    protected File sqliteFile;
    protected Map<String, Class<? extends IRecord>> tableToRecordMap = new HashMap<>();
    protected Set<Table> tables = new HashSet<>();
    protected DBType type;
    
    public Database(JavaPlugin plugin, DBType type, String host, String username, String password, int port, String database) {
        this.plugin = plugin;
        this.type = type;
        this.host = host;
        this.username = username;
        this.password = password;
        this.port = port;
        this.database = database;
    }
    
    public Database(JavaPlugin plugin, DBType type, String file) {
        this.plugin = plugin;
        this.type = type;
        this.sqliteFile = new File(plugin.getDataFolder(), file);
        if (!sqliteFile.exists()) {
            try {
                sqliteFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create the SQLite File");
            }
        }
    }
    
    public void registerTable(Table table) {
        this.tables.add(table);
    }
    
    public void addRecordToQueue(IRecord record) {
        Map<String, Object> serialized = record.serialize();
        for (Object object : serialized.values()) {
            if (object instanceof IRecord) {
                addRecordToQueue((IRecord) object);
            }
        }
        
        this.recordQueue.add(record);
    }
    
    public void loadRecords() {
        establishConnection();
        for (Table table : tables) {
            try {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM " + database + "." + table.getName());
                while (resultSet.next()) {
                    Row row = new Row(table, resultSet);
                    IRecord record = row.getRecord();
                    this.records.add(record);
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        closeConnection();
    }
    
    public Set<IRecord> getRecords() {
        return records;
    }
    
    public void pushRecord(IRecord record) {
        Table table = null;
        recordLoop:
        for (Entry<String, Class<? extends IRecord>> entry : this.tableToRecordMap.entrySet()) {
            if (entry.getValue().getName().equals(record.getClass().getName())) {
                for (Table t : tables) {
                    if (t.getName().equals(entry.getKey())) {
                        table = t;
                        break recordLoop;
                    }
                }
            }
        }
        
        if (table == null) return;
        Map<String, Object> serialized = record.serialize();
        Column unique = null;
        for (Column column : table.getColumns()) {
            if (column.isUnique()) {
                unique = column;
                break;
            }
        }
    
        String querySQL = null;
        Iterator<Entry<String, Object>> iterator = serialized.entrySet().iterator();
    
        if (unique != null) {
            String where = Statements.WHERE.replace("{column}", unique.getName()).replace("{value}", serialized.get(unique.getName()).toString());
            String selectSql = Statements.SELECT.replace("{database}", this.database).replace("{table}", table.getName()) + " " + where;
            try {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(selectSql);
                Row row = new Row(table, resultSet);
                if (!row.getDataMap().isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                
                    while (iterator.hasNext()) {
                        Entry<String, Object> entry = iterator.next();
                        DataType type = DataType.getType(entry.getValue());
                        if (type == null) continue;
                        sb.append(Statements.UPDATE_VALUE.replace("{column}", entry.getKey()).replace("{value}", entry.getValue() + ""));
                        if (iterator.hasNext()) {
                            sb.append(",");
                        }
                    }
                
                    querySQL = Statements.UPDATE.replace("{values}", sb.toString()).replace("{location}", unique.getName() + "=" + serialized.get(unique.getName()));
                    querySQL = querySQL.replace("{name}", table.getName());
                    try {
                        Statement updateStatement = connection.createStatement();
                        updateStatement.executeUpdate(querySQL);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    return;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    
        if (StringUtils.isEmpty(querySQL)) {
            StringBuilder colBuilder = new StringBuilder(), valueBuilder = new StringBuilder();
            Iterator<Column> columnIterator = table.getColumns().iterator();
            while (columnIterator.hasNext()) {
                Column column = columnIterator.next();
                if (column.isUnique()) continue;
                colBuilder.append(column.getName());
                valueBuilder.append("'").append(serialized.get(column.getName())).append("'");
                if (columnIterator.hasNext()) {
                    colBuilder.append(",");
                    valueBuilder.append(",");
                }
            }
        
            querySQL = Statements.INSERT.replace("{columns}", colBuilder.toString()).replace("{values}", valueBuilder.toString());
            querySQL = querySQL.replace("{name}", table.getName());
            try {
                PreparedStatement statement = connection.prepareStatement(querySQL, Statement.RETURN_GENERATED_KEYS);
                int affectedRows = statement.executeUpdate();
                if (affectedRows == 0) return;
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        record.setId(generatedKeys.getInt(1));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void pushQueue() {
        establishConnection();
        while (!recordQueue.isEmpty()) {
            IRecord record = recordQueue.poll();
            pushRecord(record);
        }
        closeConnection();
    }
    
    public void mapTableToRecord(Table table, Class<? extends IRecord> record) {
        try {
            record.getDeclaredConstructor(Row.class);
        } catch (NoSuchMethodException e) {
            plugin.getLogger().severe("Record class " + record.getName() + " does not have a Constructor with the Row class as a parameter.");
            return;
        }
        
        this.tableToRecordMap.put(table.getName(), record);
    }
    
    public void establishConnection() {
        closeConnection();
        String connectionURL = "";
        try {
            connectionURL = switch (type) {
                case MYSQL -> type.getUrl().replace("{hostname}", this.host).replace("{port}", this.port + "").replace("{database}", this.database);
                case SQLITE -> type.getUrl().replace("{file}", this.sqliteFile + "");
            };
        
            Class.forName(type.getDriverClass());
            switch (type) {
                case MYSQL -> connection = DriverManager.getConnection(connectionURL, this.username, this.password);
                case SQLITE -> connection = DriverManager.getConnection(connectionURL);
            }
        } catch (ClassNotFoundException e) {
            plugin.getLogger().severe("Could not find a Connecton Driver");
        } catch (SQLException e) {
            plugin.getLogger().severe("Could not connect to the database at " + connectionURL);
        }
    }
    
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                connection = null;
            }
        } catch (SQLException e) {}
    }
    
    public void generateTables() {
        establishConnection();
        for (Table table : this.tables) {
            String sql = table.generateCreationStatement();
            try {
                Statement statement = connection.createStatement();
                statement.execute(sql);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
    
            try {
                DatabaseMetaData databaseMeta = connection.getMetaData();
                ResultSet columns = databaseMeta.getColumns(null, null, table.getName(), null);
                List<String> existingColumns = new ArrayList<>();
                while (columns.next()) {
                    String name = columns.getString("COLUMN_NAME");
                    existingColumns.add(name);
                }
    
                List<String> columnSqls = new ArrayList<>();
                for (Column column : table.getColumns()) {
                    if (!existingColumns.contains(column.getName())) {
                        String columnType;
                        if (column.getType().equals(DataType.VARCHAR)) {
                            columnType = column.getType().name() + "(" + column.getLength() + ")";
                        } else {
                            columnType = column.getType().name();
                        }
                        String columnSql = Statements.ALTER_TABLE.replace("{table}", table.getName()).replace("{logic}", Statements.ADD_COLUMN.replace("{column}", column.getName()).replace("{type}", columnType));
                        columnSqls.add(columnSql);
                    }
                    existingColumns.remove(column.getName());
                }
                
                if (!existingColumns.isEmpty()) {
                    for (String existingColumn : existingColumns) {
                        String columnSql = Statements.ALTER_TABLE.replace("{table}", table.getName()).replace("{logic}", Statements.DROP_COLUMN.replace("{column}", existingColumn));
                        columnSqls.add(columnSql);
                    }
                }
                
                if (!columnSqls.isEmpty()) {
                    for (String columnSql : columnSqls) {
                        Statement statement = connection.createStatement();
                        statement.executeUpdate(columnSql);
                    }
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
    
        }
        closeConnection();
    }
    
    public Class<? extends IRecord> getRecordClass(Table table) {
        return this.tableToRecordMap.get(table.getName());
    }
}