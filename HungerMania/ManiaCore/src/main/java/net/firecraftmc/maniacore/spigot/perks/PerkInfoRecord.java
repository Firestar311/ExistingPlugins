package net.firecraftmc.maniacore.spigot.perks;

import net.firecraftmc.manialib.sql.Table;
import net.firecraftmc.manialib.util.Utils;

import java.util.*;

public class PerkInfoRecord implements net.firecraftmc.manialib.sql.IRecord<PerkInfo> {
    
    public static net.firecraftmc.manialib.sql.Table generateTable(net.firecraftmc.manialib.sql.Database database) {
        net.firecraftmc.manialib.sql.Table table = new Table(database, "perks");
        table.addColumn("id", net.firecraftmc.manialib.sql.DataType.INT, true, true);
        table.addColumn("uuid", net.firecraftmc.manialib.sql.DataType.VARCHAR, 36);
        table.addColumn("name", net.firecraftmc.manialib.sql.DataType.VARCHAR, 64);
        table.addColumn("value", net.firecraftmc.manialib.sql.DataType.VARCHAR, 5);
        table.addColumn("created", net.firecraftmc.manialib.sql.DataType.BIGINT);
        table.addColumn("modified", net.firecraftmc.manialib.sql.DataType.BIGINT);
        table.addColumn("unlockedTiers", net.firecraftmc.manialib.sql.DataType.VARCHAR, 1000);
        table.addColumn("active", net.firecraftmc.manialib.sql.DataType.VARCHAR, 5);
        return table;
    }
    
    private net.firecraftmc.maniacore.spigot.perks.PerkInfo object;
    
    public PerkInfoRecord(net.firecraftmc.maniacore.spigot.perks.PerkInfo object) {
        this.object = object;
    }
    
    public PerkInfoRecord(net.firecraftmc.manialib.sql.Row row) {
        int id = row.getInt("id");
        UUID uuid = UUID.fromString(row.getString("uuid"));
        String name = row.getString("name");
        boolean value = row.getBoolean("value");
        long created = row.getLong("created");
        long modified = row.getLong("modified");
        String rawUn = row.getString("unlockedTiers");
        Set<Integer> unlockedTiers = new HashSet<>();
        for (String s : rawUn.split(",")) {
            try {
                unlockedTiers.add(Integer.parseInt(s));
            } catch (Exception e) {}
        }
        boolean active = row.getBoolean("active");
        this.object = new net.firecraftmc.maniacore.spigot.perks.PerkInfo(id, uuid, name, value, unlockedTiers, created, modified, active);
    }
    
    public int getId() {
        return object.getId();
    }
    
    public void setId(int id) {
        object.setId(id);
    }
    
    public Map<String, Object> serialize() {
        return new HashMap<String, Object>() {{
            put("id", object.getId());
            put("uuid", object.getUuid().toString());
            put("name", object.getName());
            put("value", object.getValue());
            put("created", object.getCreated());
            put("modified", object.getModified());
            put("unlockedTiers", Utils.join(object.getUnlockedTiers(), ","));
            put("active", object.isActive());
        }};
    }
    
    public PerkInfo toObject() {
        return object;
    }
}