package net.firecraftmc.maniacore.api.records;

import net.firecraftmc.maniacore.api.stats.Statistic;
import net.firecraftmc.manialib.sql.Table;

import java.util.*;

public class StatRecord implements net.firecraftmc.manialib.sql.IRecord<Statistic> {
    
    public static net.firecraftmc.manialib.sql.Table generateTable(net.firecraftmc.manialib.sql.Database database) {
        net.firecraftmc.manialib.sql.Table table = new Table(database, "stats");
        table.addColumn(new net.firecraftmc.manialib.sql.Column("id", net.firecraftmc.manialib.sql.DataType.INT, true, true));
        table.addColumn(new net.firecraftmc.manialib.sql.Column("uuid", net.firecraftmc.manialib.sql.DataType.VARCHAR, 36));
        table.addColumn(new net.firecraftmc.manialib.sql.Column("name", net.firecraftmc.manialib.sql.DataType.VARCHAR, 100));
        table.addColumn(new net.firecraftmc.manialib.sql.Column("value", net.firecraftmc.manialib.sql.DataType.VARCHAR, 1000));
        table.addColumn(new net.firecraftmc.manialib.sql.Column("created", net.firecraftmc.manialib.sql.DataType.BIGINT));
        table.addColumn(new net.firecraftmc.manialib.sql.Column("modified", net.firecraftmc.manialib.sql.DataType.BIGINT));
        return table;
    }
    
    private Statistic statistic;
    
    public StatRecord(Statistic statistic) {
        this.statistic = statistic;
    }
    
    public StatRecord(net.firecraftmc.manialib.sql.Row row) {
        int id = row.getInt("id");
        UUID uuid = UUID.fromString(row.getString("uuid"));
        String name = row.getString("name");
        long created = row.getLong("created");
        long modified = row.getLong("modified");
        String value = row.getString("value");
        this.statistic = new Statistic(id, uuid, name, value, created, modified);
    }
    
    public int getId() {
        return statistic.getId();
    }
    
    public void setId(int id) {
        statistic.setId(id);
    }
    
    public Map<String, Object> serialize() {
        return new HashMap<String, Object>() {{
            put("id", statistic.getId());
            put("uuid", statistic.getUuid().toString());
            put("value", statistic.getValue() + "");
            put("created", statistic.getCreated());
            put("modified", statistic.getModified());
            put("name", statistic.getName());
        }};
    }
    
    public Statistic toObject() {
        return statistic;
    }
}
