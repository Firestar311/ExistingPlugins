package net.firecraftmc.maniacore.api.records;

import net.firecraftmc.maniacore.api.region.Region;
import net.firecraftmc.manialib.sql.Table;

import java.util.HashMap;
import java.util.Map;

public class RegionRecord implements net.firecraftmc.manialib.sql.IRecord<Region> {
    
    public static net.firecraftmc.manialib.sql.Table generateTable(net.firecraftmc.manialib.sql.Database database) {
        net.firecraftmc.manialib.sql.Table table = new Table(database, "region");
        net.firecraftmc.manialib.sql.Column id = new net.firecraftmc.manialib.sql.Column("id", net.firecraftmc.manialib.sql.DataType.INT, true, true);
        net.firecraftmc.manialib.sql.Column world = new net.firecraftmc.manialib.sql.Column("world", net.firecraftmc.manialib.sql.DataType.VARCHAR, 30, false, false);
        net.firecraftmc.manialib.sql.Column xMin = new net.firecraftmc.manialib.sql.Column("xMin", net.firecraftmc.manialib.sql.DataType.INT, false, false);
        net.firecraftmc.manialib.sql.Column yMin = new net.firecraftmc.manialib.sql.Column("yMin", net.firecraftmc.manialib.sql.DataType.INT, false, false);
        net.firecraftmc.manialib.sql.Column zMin = new net.firecraftmc.manialib.sql.Column("zMin", net.firecraftmc.manialib.sql.DataType.INT, false, false);
        net.firecraftmc.manialib.sql.Column xMax = new net.firecraftmc.manialib.sql.Column("xMax", net.firecraftmc.manialib.sql.DataType.INT, false, false);
        net.firecraftmc.manialib.sql.Column yMax = new net.firecraftmc.manialib.sql.Column("yMax", net.firecraftmc.manialib.sql.DataType.INT, false, false);
        net.firecraftmc.manialib.sql.Column zMax = new net.firecraftmc.manialib.sql.Column("zMax", net.firecraftmc.manialib.sql.DataType.INT, false, false);
        
        table.addColumns(id, world, xMin, yMin, zMin, xMax, yMax, zMax);
        return table;
    }
    
    private Region region;

    public RegionRecord(Region region) {
        this.region = region;
    }
    
    public RegionRecord(net.firecraftmc.manialib.sql.Row row) {
        int id = row.getInt("id");
        String world = row.getString("world");
        int xMin = row.getInt("xMin");
        int yMin = row.getInt("yMin");
        int zMin = row.getInt("zMin");
        int xMax = row.getInt("xMax");
        int yMax = row.getInt("yMax");
        int zMax = row.getInt("zMax");
        this.region = new Region(id, world, xMin, yMin, zMin, xMax, yMax, zMax);
    }

    public int getId() {
        return region.getId();
    }

    public void setId(int id) {
        region.setId(id);
    }

    public Map<String, Object> serialize() {
        return new HashMap<String, Object>() {{
            put("id", region.getId());
            put("world", region.getWorld().getName());
            put("xMin", region.getXMin());
            put("yMin", region.getYMin());
            put("zMin", region.getZMin());
            put("xMax", region.getXMax());
            put("yMax", region.getYMax());
            put("zMax", region.getZMax());
        }};
    }

    public Region toObject() {
        return region;
    }
}
