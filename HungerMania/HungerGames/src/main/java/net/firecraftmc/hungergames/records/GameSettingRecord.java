package net.firecraftmc.hungergames.records;

import net.firecraftmc.hungergames.newsettings.GameSetting;
import net.firecraftmc.manialib.sql.*;
import net.firecraftmc.manialib.util.Unit;

import java.util.HashMap;
import java.util.Map;

public class GameSettingRecord implements IRecord<GameSetting> {

    private GameSetting object;

    public static Table generateTable(Database database) {
        Table table = new Table(database, "gamesettings");
        table.addColumn("id", DataType.INT, true, true);
        table.addColumn("groupName", DataType.VARCHAR, 100);
        table.addColumn("settingName", DataType.VARCHAR, 100);
        table.addColumn("value", DataType.VARCHAR, 100);
        table.addColumn("type", DataType.VARCHAR, 100);
        table.addColumn("unit", DataType.VARCHAR, 100);
        return table;
    }

    public GameSettingRecord(GameSetting object) {
        this.object = object;
    }

    public GameSettingRecord(Row row) {
        int id = row.getInt("id");
        String groupName = row.getString("groupName");
        String settingName = row.getString("settingName");
        String value = row.getString("value");
        GameSetting.Type type = GameSetting.Type.valueOf(row.getString("type"));
        Unit unit = Unit.valueOf(row.getString("unit"));
        this.object = new GameSetting(id, groupName, settingName, value, type, unit);
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
            put("groupName", object.getGroupName());
            put("settingName", object.getSettingName());
            put("value", object.getValue());
            put("type", object.getType().name());
            put("unit", object.getUnit().name());
        }};
    }

    public GameSetting toObject() {
        return object;
    }
}