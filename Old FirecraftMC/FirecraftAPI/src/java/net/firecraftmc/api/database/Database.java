package net.firecraftmc.api.database;

import org.bukkit.Bukkit;

import java.sql.*;
import java.util.concurrent.TimeUnit;

public abstract class Database {
    
    //PlayerDatabase, ReportsDatabase, PunishmentsDatabase, FctDatabase, BroadcastDatabase
    
    protected Connection connection;
    protected final String user, database, password, hostname;
    protected final int port;
    protected long lastActivity = 0L;
    protected static final long TIMEOUT = TimeUnit.MINUTES.toMillis(5);
    
    public Database(String user, String database, String password, int port, String hostname) {
        this.user = user;
        this.database = database;
        this.password = password;
        this.port = port;
        this.hostname = hostname;
        
        Thread timeoutThread = new Thread(() -> {
            while (Bukkit.getServer() != null) {
                if (!isClosed()) {
                    if ((lastActivity + TIMEOUT) >= System.currentTimeMillis()) {
                        System.out.println("Closing idle database connection.");
                        closeConnection();
                    }
                }
            }
        });
        timeoutThread.start();
    }
    
    public abstract void modifyDatabase();
    
    public void openConnection() {
        String connectionURL = "jdbc:mysql://" + this.hostname + ":" + this.port + "/" + this.database;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(connectionURL, this.user, this.password);
            this.lastActivity = System.currentTimeMillis();
            modifyDatabase();
        } catch (Exception e) {
            Bukkit.getLogger().severe("Could not connect to the database at " + connectionURL);
        }
    }
    
    public boolean checkConnection() {
        return !isClosed();
    }
    
    public Connection getConnection() {
        return connection;
    }
    
    public void closeConnection() {
        try {
            closeConnection(connection);
            connection = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void closeConnection(Connection con) throws SQLException {
        if (con != null) {
            con.close();
        }
    }
    
    private boolean isClosed() {
        if (connection != null) {
            try {
                return this.connection.isClosed();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
    
    @SuppressWarnings("SameParameterValue")
    protected int getNextAutoId(String table) {
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