package net.firecraftmc.api.database;

import net.firecraftmc.api.enums.ServerType;
import net.firecraftmc.api.model.server.FirecraftServer;
import org.bukkit.ChatColor;

import java.sql.*;
import java.util.UUID;

public class ServerDatabase extends Database {
    
    public ServerDatabase(String user, String database, String password, int port, String hostname) {
        super(user, database, password, port, hostname);
    }
    
    public void modifyDatabase() {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `{database}.servers` (`id` VARCHAR(36) NOT NULL, `name` VARCHAR(100) NOT NULL, `color` VARCHAR(2) NOT NULL, `type` VARCHAR(30) NOT NULL, `online` VARCHAR(10) NOT NULL);".replace("{database}", this.database));
            statement.close();
        } catch (Exception e) {
        }
    }
    
    public void saveServer(FirecraftServer server) {
        try {
            ResultSet set = null;
            try {
                Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                set = statement.executeQuery("SELECT * FROM `servers` WHERE `id` = '{id}';".replace("{id}", server.getId()));
                statement.closeOnCompletion();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            
            try {
                PreparedStatement insert = connection.prepareStatement("INSERT INTO `servers`(`id`, `name`, `color`, `type`, `online`) VALUES (?,?,?,?,?)");
                PreparedStatement update = connection.prepareStatement("UPDATE `servers` SET `name`= ?,`color`= ?,`type`= ?, `online`= ? WHERE `id`= ?;");
                
                insert.setString(1, server.getId());
                insert.setString(2, server.getName());
                insert.setString(3, server.getColor().toString());
                insert.setString(4, server.getType().toString());
                insert.setString(5, server.isOnline() + "");
                
                update.setString(1, server.getName());
                update.setString(2, server.getColor().toString());
                update.setString(3, server.getType().toString());
                update.setString(4, server.isOnline() + "");
                
                if (set.next()) {
                    update.executeUpdate();
                    update.close();
                } else {
                    insert.executeUpdate();
                    insert.close();
                }
            } catch (Exception e) {
            }
        } catch (Exception e) {
        }
    }
    
    public FirecraftServer getServer(UUID uuid) {
        ResultSet set;
        try {
            if (connection == null || connection.isClosed()) {
                openConnection();
            }
            Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            set = statement.executeQuery("SELECT * FROM `servers` WHERE `id` = '{id}';".replace("{id}", uuid.toString()));
            
            if (set.next()) {
                String name = set.getString("name");
                ChatColor color = ChatColor.getByChar(set.getString("color").replace("§", ""));
                ServerType type = ServerType.valueOf(set.getString("type"));
                //TODO The boolean online value
                statement.close();
                return new FirecraftServer(uuid.toString(), name, color, "", type);
            }
        } catch (Exception e) {
        }
        
        return null;
    }
}
