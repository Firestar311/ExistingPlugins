package net.firecraftmc.maniacore.api.records;

import net.firecraftmc.maniacore.api.nickname.Nickname;
import net.firecraftmc.maniacore.api.ranks.Rank;
import net.firecraftmc.manialib.sql.Table;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NicknameRecord implements net.firecraftmc.manialib.sql.IRecord<Nickname> {
    
    public static net.firecraftmc.manialib.sql.Table generateTable(net.firecraftmc.manialib.sql.Database database) {
        net.firecraftmc.manialib.sql.Table table = new Table(database, "nicknames");
        table.addColumn("id", net.firecraftmc.manialib.sql.DataType.INT, true, true);
        table.addColumn("player", net.firecraftmc.manialib.sql.DataType.VARCHAR, 36);
        table.addColumn("name", net.firecraftmc.manialib.sql.DataType.VARCHAR, 16);
        table.addColumn("skinUUID", net.firecraftmc.manialib.sql.DataType.VARCHAR, 36);
        table.addColumn("active", net.firecraftmc.manialib.sql.DataType.VARCHAR, 5);
        table.addColumn("rank", net.firecraftmc.manialib.sql.DataType.VARCHAR, 50);
        return table;
    }
    
    private net.firecraftmc.maniacore.api.nickname.Nickname object;
    
    public NicknameRecord(net.firecraftmc.maniacore.api.nickname.Nickname nickname) {
        this.object = nickname;
    }
    
    public NicknameRecord(net.firecraftmc.manialib.sql.Row row) {
        int id = row.getInt("id");
        UUID player = row.getUUID("player");
        String name = row.getString("name");
        UUID skinUUID = row.getUUID("skinUUID");
        boolean active = row.getBoolean("active");
        Rank rank = Rank.valueOf(row.getString("rank"));
        this.object = new net.firecraftmc.maniacore.api.nickname.Nickname(id, player, name, skinUUID, active, rank);
    }
    
    public Map<String, Object> serialize() {
        return new HashMap<String, Object>() {{
            put("id", object.getId());
            put("player", object.getPlayer().toString());
            put("name", object.getName());
            put("skinUUID", object.getSkinUUID().toString());
            put("active", object.isActive());
            put("rank", object.getRank().name());
        }};
    }
    
    public Nickname toObject() {
        return object;
    }
    
    public void setId(int id) {
        object.setId(id);
    }
    
    public int getId() {
        return object.getId();
    }
}
