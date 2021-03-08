package net.firecraftmc.maniacore.api.records;

import net.firecraftmc.manialib.sql.Table;
import net.firecraftmc.maniacore.api.skin.Skin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SkinRecord implements net.firecraftmc.manialib.sql.IRecord<Skin> {
    
    private Skin skin;
    
    public static net.firecraftmc.manialib.sql.Table generateTable(net.firecraftmc.manialib.sql.Database database) {
        net.firecraftmc.manialib.sql.Table table = new Table(database, "skins");
        table.addColumn(new net.firecraftmc.manialib.sql.Column("id", net.firecraftmc.manialib.sql.DataType.INT, true, true));
        table.addColumn(new net.firecraftmc.manialib.sql.Column("name", net.firecraftmc.manialib.sql.DataType.VARCHAR, 200));
        table.addColumn(new net.firecraftmc.manialib.sql.Column("uuid", net.firecraftmc.manialib.sql.DataType.VARCHAR, 48));
        table.addColumn(new net.firecraftmc.manialib.sql.Column("value", net.firecraftmc.manialib.sql.DataType.VARCHAR, 1000));
        table.addColumn(new net.firecraftmc.manialib.sql.Column("signature", net.firecraftmc.manialib.sql.DataType.VARCHAR, 1000));
        return table;
    }
    
    public SkinRecord(Skin skin) {
        this.skin = skin;
    }
    
    public SkinRecord(net.firecraftmc.manialib.sql.Row row) {
        int id = row.getInt("id");
        String name = row.getString("name");
        UUID uuid = UUID.fromString(row.getString("uuid"));
        String value = row.getString("value");
        String signature = row.getString("signature");
        this.skin = new Skin(id, uuid, name, value, signature);
    }
    
    @Override
    public int getId() {
        return skin.getId();
    }
    
    @Override
    public void setId(int id) {
        skin.setId(id);
    }
    
    @Override
    public Map<String, Object> serialize() {
        return new HashMap<String, Object>() {{
            put("id", skin.getId());
            put("name", skin.getName());
            put("uuid", skin.getUuid().toString());
            put("value", skin.getValue());
            put("signature", skin.getSignature());
        }};
    }
    
    @Override
    public Skin toObject() {
        return skin;
    }
}
