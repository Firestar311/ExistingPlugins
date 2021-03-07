package net.firecraftmc.maniacore.api.records;

import net.firecraftmc.maniacore.api.friends.Friendship;
import net.firecraftmc.manialib.sql.Table;

import java.util.*;

public class FriendshipRecord implements net.firecraftmc.manialib.sql.IRecord<Friendship> {
    
    public static net.firecraftmc.manialib.sql.Table generateTable(net.firecraftmc.manialib.sql.Database database) {
        net.firecraftmc.manialib.sql.Table table = new Table(database, "friendships");
        table.addColumn("id", net.firecraftmc.manialib.sql.DataType.INT, true, true);
        table.addColumn("player1", net.firecraftmc.manialib.sql.DataType.VARCHAR, 36);
        table.addColumn("player2", net.firecraftmc.manialib.sql.DataType.VARCHAR, 36);
        table.addColumn("timestamp", net.firecraftmc.manialib.sql.DataType.BIGINT);
        return table;
    }
    
    private Friendship friendship;
    
    public FriendshipRecord(Friendship friendship) {
        this.friendship = friendship;
    }
    
    public FriendshipRecord(net.firecraftmc.manialib.sql.Row row) {
        this.friendship = Friendship.builder().id(row.getInt("id")).player1(row.getUUID("player1")).player2(row.getUUID("player2")).timestamp(row.getLong("timestamp")).build();
    }
    
    public int getId() {
        return friendship.getId();
    }
    
    public void setId(int id) {
        friendship.setId(id);
    }
    
    public Map<String, Object> serialize() {
        return new HashMap<String, Object>() {{
            put("id", friendship.getId());
            put("player1", friendship.getPlayer1().toString());
            put("player2", friendship.getPlayer2().toString());
            put("timestamp", friendship.getTimestamp());
        }};
    }
    
    public Friendship toObject() {
        return friendship;
    }
}
