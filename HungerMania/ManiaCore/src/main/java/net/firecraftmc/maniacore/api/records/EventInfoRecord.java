package net.firecraftmc.maniacore.api.records;

import net.firecraftmc.maniacore.api.events.EventInfo;
import net.firecraftmc.manialib.sql.Table;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class EventInfoRecord implements net.firecraftmc.manialib.sql.IRecord<EventInfo> {
    
    private EventInfo eventInfo;
    
    public static net.firecraftmc.manialib.sql.Table generateTable(net.firecraftmc.manialib.sql.Database database) {
        net.firecraftmc.manialib.sql.Table table = new Table(database, "events");
        table.addColumn(new net.firecraftmc.manialib.sql.Column("id", net.firecraftmc.manialib.sql.DataType.INT, true, true));
        table.addColumn(new net.firecraftmc.manialib.sql.Column("name", net.firecraftmc.manialib.sql.DataType.VARCHAR, 100));
        table.addColumn(new net.firecraftmc.manialib.sql.Column("active", net.firecraftmc.manialib.sql.DataType.VARCHAR, 5));
        table.addColumn(new net.firecraftmc.manialib.sql.Column("startTime", net.firecraftmc.manialib.sql.DataType.BIGINT));
        table.addColumn(new net.firecraftmc.manialib.sql.Column("settingsId", net.firecraftmc.manialib.sql.DataType.INT));
        table.addColumn(new net.firecraftmc.manialib.sql.Column("players", net.firecraftmc.manialib.sql.DataType.VARCHAR, 1000));
        table.addColumn(new net.firecraftmc.manialib.sql.Column("servers", net.firecraftmc.manialib.sql.DataType.VARCHAR, 1000));
        return table;
    }
    
    public EventInfoRecord(EventInfo eventInfo) {
        this.eventInfo = eventInfo;
    }
    
    public EventInfoRecord(net.firecraftmc.manialib.sql.Row row) {
        int id = row.getInt("id");
        String name = row.getString("name");
        boolean active = row.getBoolean("active");
        long startTime = row.getLong("startTime");
        int settingsId = row.getInt("settingsId");
        String rawPlayers = row.getString("players");
        String rawServers = row.getString("servers");
        Set<Integer> players = new HashSet<>();
        Set<String> servers = new HashSet<>();
        if (!StringUtils.isEmpty(rawPlayers)) {
            for (String p : rawPlayers.split(",")) {
                players.add(Integer.parseInt(p));
            }
        }
        if (!StringUtils.isEmpty(rawServers)) {
            servers.addAll(Arrays.asList(rawServers.split(",")));
        }
        
        this.eventInfo = new EventInfo(id, name, active, startTime, settingsId, players, servers);
    }
    
    @Override
    public int getId() {
        return eventInfo.getId();
    }
    
    @Override
    public void setId(int id) {
        eventInfo.setId(id);
    }
    
    @Override
    public Map<String, Object> serialize() {
        return new HashMap<String, Object>() {{
            put("id", eventInfo.getId());
            put("name", eventInfo.getName());
            put("active", eventInfo.isActive());
            put("startTime", eventInfo.getStartTime());
            put("settingsId", eventInfo.getSettingsId());
            put("players", StringUtils.join(eventInfo.getPlayers(), ","));
            put("servers", StringUtils.join(eventInfo.getServers(), ","));
        }};
    }
    
    @Override
    public EventInfo toObject() {
        return eventInfo;
    }
}