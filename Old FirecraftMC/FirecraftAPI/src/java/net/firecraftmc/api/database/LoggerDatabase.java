package net.firecraftmc.api.database;

import net.firecraftmc.api.enums.Channel;
import net.firecraftmc.api.model.server.FirecraftServer;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.UUID;

public class LoggerDatabase extends Database {
    
    public LoggerDatabase(String user, String database, String password, int port, String hostname) {
        super(user, database, password, port, hostname);
    }
    
    public void modifyDatabase() {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `{database}.player_chat` (`id` INT NOT NULL AUTO_INCREMENT, `uuid` VARCHAR(36) NOT NULL, `server` VARCHAR(36) NOT NULL, `timestamp` VARCHAR(1000) NOT NULL, `channel` VARCHAR(20) NOT NULL, `message` VARCHAR(500) NOT NULL);".replace("{database}", database));
            statement.executeUpdate("ALTER TABLE `player_chat` ADD PRIMARY KEY(`id`);");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `{database}.player_cmd` (`id` INT NOT NULL AUTO_INCREMENT, `uuid` VARCHAR(36) NOT NULL, `server` VARCHAR(36) NOT NULL, `timestamp` VARCHAR(1000) NOT NULL, `command` VARCHAR(500) NOT NULL);".replace("{database}", database));
            statement.executeUpdate("ALTER TABLE `player_cmd` ADD PRIMARY KEY(`id`);");
            statement.close();
        } catch (Exception e) {}
    }
    
    public void saveChatMessage(UUID uuid, FirecraftServer server, long timestamp, Channel channel, String message) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO `player_chat` (`uuid`, `server`, `timestamp`, `channel`, `message`) VALUES (?, ?, ?, ?, ?);");
            statement.setString(1, uuid.toString());
            statement.setString(2, server.getId());
            statement.setString(3, timestamp + "");
            statement.setString(4, channel.name());
            statement.setString(5, message);
            
            statement.executeUpdate();
            statement.close();
        } catch (Exception e) {}
    }
    
    public void saveCommand(UUID uuid, FirecraftServer server, long timestamp, String command) {
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO `player_cmd` (`uuid`, `server`, `timestamp`, `command`) VALUES (?, ?, ?, ?);");
            statement.setString(1, uuid.toString());
            statement.setString(2, server.getId());
            statement.setString(3, timestamp + "");
            statement.setString(4, command);
            
            statement.executeUpdate();
            statement.close();
        } catch (Exception e) {}
    }
}
