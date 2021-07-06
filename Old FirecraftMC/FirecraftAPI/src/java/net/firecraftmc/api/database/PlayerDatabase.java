package net.firecraftmc.api.database;

import net.firecraftmc.api.model.player.FirecraftPlayer;

import java.sql.Statement;

public class PlayerDatabase extends Database {
    
    /* COLUMNS
    uniqueid
    name
    mainrank
    subranks
    channel
    online
    server
    ignored
    nick
    firstjoined
    timeplayed
    lastseen
    toggles
    vanishsettings
    reportchanges
    isflying
    gamemode
    streamurl
     */
    
    public PlayerDatabase(String user, String database, String password, int port, String hostname) {
        super(user, database, password, port, hostname);
    }
    
    public void savePlayer(FirecraftPlayer player) {
    
    }
    
    public void modifyDatabase() {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS `{database}.players` (`uniqueid` varchar(36) not null, `name` varchar(36) not null,"
                    + "`mainrank` varchar(50) not null, `subranks` varchar(2048), `channel` varchar(32) not null, `online` varchar(5), `server` varchar(36),"
                    + "`ignored` varchar(2048), `nick` varchar(100), `firstjoined` varchar(500), `lastseen` varchar(500), `timeplayed` varchar(500), "
                    + "`toggles` varchar(2048), `vanishsettings` varchar(2048), `reportchanges` varchar(1000), `isflying` varchar(5), `gamemode` varchar(20),"
                    + "`streamurl` varchar(100));");
        } catch (Exception e) {}
    }
}
