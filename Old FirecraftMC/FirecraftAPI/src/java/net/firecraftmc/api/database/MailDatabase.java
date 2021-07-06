package net.firecraftmc.api.database;

import net.firecraftmc.api.model.player.Mail;

import java.sql.*;
import java.util.*;

public class MailDatabase extends Database {
    
    public MailDatabase(String user, String database, String password, int port, String hostname) {
        super(user, database, password, port, hostname);
    }
    
    public void modifyDatabase() {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `{database}.mail` (`id` int NOT NULL AUTO_INCREMENT, `date` varchar(1000) NOT NULL, `sender` varchar(36) NOT NULL, `receiver` varchar(36) NOT NULL, `text` varchar(500) NOT NULL, `read` VARCHAR(5) NOT NULL);");
            statement.close();
        } catch (Exception e) {
        }
    }
    
    public Mail getMail(int id) {
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM `mail` WHERE `id` = ?;");
            statement.setString(1, id + "");
            ResultSet set = statement.executeQuery();
            if (set.next()) {
                long date = set.getLong("date");
                UUID sender = UUID.fromString(set.getString("sender"));
                UUID receiver = UUID.fromString(set.getString("receiver"));
                String text = set.getString("text");
                boolean read = set.getBoolean("read");
                statement.close();
                return new Mail(id, date, sender, receiver, text, read);
            }
        } catch (Exception e) {
        }
        return null;
    }
    
    public List<Mail> getMailBySender(UUID sender) {
        List<Mail> mail = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM `mail` WHERE `sender` = ?;");
            statement.setString(1, sender.toString());
            ResultSet set = statement.executeQuery();
            while (set.next()) {
                int id = set.getInt("id");
                long date = set.getLong("date");
                UUID receiver = UUID.fromString(set.getString("receiver"));
                String text = set.getString("text");
                boolean read = set.getBoolean("read");
                mail.add(new Mail(id, date, sender, receiver, text, read));
            }
        } catch (Exception e) {
        }
        
        return mail;
    }
    
    public List<Mail> getMailByReceiver(UUID receiver) {
        List<Mail> mail = new ArrayList<>();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM `mail` WHERE `receiver` = ?;");
            statement.setString(1, receiver.toString());
            ResultSet set = statement.executeQuery();
            net.firecraftmc.api.model.Database.retrieveMail(receiver, set, mail);
            statement.close();
        } catch (Exception e) {
        }
        
        return mail;
    }
    
    public List<Mail> getMailByUUID(UUID uuid) {
        List<Mail> mail = new ArrayList<>();
        mail.addAll(getMailByReceiver(uuid));
        mail.addAll(getMailBySender(uuid));
        return mail;
    }
    
    public List<Mail> getAllMail() {
        List<Mail> mail = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet set = statement.executeQuery("SELECT * FROM `mail` WHERE `id` = ?;");
            net.firecraftmc.api.model.Database.retrieveMail(set, mail);
            statement.close();
        } catch (Exception e) {
        }
        return mail;
    }
    
    public Mail createMail(long date, UUID sender, UUID receiver, String text, boolean read) {
        int auto_id = getNextAutoId("mail");
        
        try {
            PreparedStatement statement = connection.prepareStatement("INSERT INTO `mail` (`date`, `sender`, `receiver`, `text`, `read`) VALUES (?, ?, ?, ?, ?);");
            statement.setString(1, date + "");
            statement.setString(2, sender.toString());
            statement.setString(3, receiver.toString());
            statement.setString(4, text);
            statement.setString(5, read + "");
            statement.executeUpdate();
            statement.close();
        } catch (Exception e) {}
        return new Mail(auto_id, date, sender, receiver, text, read);
    }
    
    public void setMailRead(int id) {
        try {
            PreparedStatement statement = connection.prepareStatement("UPDATE `mail` SET `read` = `true` WHERE `id` = ?;");
            statement.setString(1, id + "");
            statement.executeUpdate();
            statement.close();
        } catch (Exception e) {}
    }
}
