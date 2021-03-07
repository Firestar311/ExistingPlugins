package net.firecraftmc.maniacore.api.records;

import net.firecraftmc.maniacore.api.leveling.Level;
import net.firecraftmc.manialib.sql.Table;
import net.md_5.bungee.api.ChatColor;

import java.util.HashMap;
import java.util.Map;

public class LevelRecord implements net.firecraftmc.manialib.sql.IRecord<Level> {
    
    private net.firecraftmc.maniacore.api.leveling.Level level;
    
    public static net.firecraftmc.manialib.sql.Table generateTable(net.firecraftmc.manialib.sql.Database database) {
        net.firecraftmc.manialib.sql.Table table = new Table(database, "levels");
        table.addColumn(new net.firecraftmc.manialib.sql.Column("number", net.firecraftmc.manialib.sql.DataType.INT, true, true));
        table.addColumn(new net.firecraftmc.manialib.sql.Column("totalXp", net.firecraftmc.manialib.sql.DataType.INT));
        table.addColumn(new net.firecraftmc.manialib.sql.Column("coinReward", net.firecraftmc.manialib.sql.DataType.INT));
        table.addColumn(new net.firecraftmc.manialib.sql.Column("numberColor", net.firecraftmc.manialib.sql.DataType.VARCHAR, 20));
        return table;
    }
    
    public LevelRecord(net.firecraftmc.maniacore.api.leveling.Level level) {
        this.level = level;
    }
    
    public LevelRecord(net.firecraftmc.manialib.sql.Row row) {
        this.level = net.firecraftmc.maniacore.api.leveling.Level.builder().number(row.getInt("number")).totalXp(row.getInt("totalXp")).coinReward(row.getInt("coinReward")).numberColor(ChatColor.valueOf(row.getString("numberColor"))).build();
    }
    
    @Override
    public int getId() {
        return level.getNumber();
    }
    
    @Override
    public void setId(int id) {
        level.setNumber(id);
    }
    
    @Override
    public Map<String, Object> serialize() {
        return new HashMap<String, Object>(){{
            put("number", level.getNumber());
            put("totalXp", level.getTotalXp());
            put("coinReward", level.getCoinReward());
            put("numberColor", level.getNumberColor().name());
        }};
    }
    
    @Override
    public Level toObject() {
        return level;
    }
}
