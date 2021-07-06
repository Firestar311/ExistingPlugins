package com.kingrealms.realms.storage.sql;

import com.kingrealms.realms.channel.Channel;
import com.starmediadev.lib.sql.IRecord;
import com.starmediadev.lib.sql.Row;

import java.util.HashMap;
import java.util.Map;

public class ChannelRecord implements IRecord {
    
    protected int id;
    protected String name, owner, description, format, prefix, color, permission, symbol, type;
    protected long created;
    protected boolean autoJoin, allowInvites;
    protected String visibility;
    
    public ChannelRecord(Row row) {
        this.id = row.getInt("id");
        this.type = row.getString("type");
        this.name = row.getString("name");
        this.owner = row.getString("owner");
        this.description = row.getString("description");
        this.format = row.getString("format");
        this.prefix = row.getString("prefix");
        this.color = row.getString("color");
        this.permission = row.getString("permission");
        this.symbol = row.getString("symbol");
        this.created = row.getLong("created");
        this.autoJoin = row.getBoolean("autoJoin");
        this.allowInvites = row.getBoolean("allowInvites");
        this.visibility = row.getString("visibility");
    }
    
    public ChannelRecord(Channel channel) {
        this.id = channel.getId();
        this.name = channel.getName();
        this.owner = channel.getOwner().toString();
        this.description = channel.getDescription();
        this.prefix = channel.getPrefix();
        this.color = channel.getColor().toString(); //TODO change this to a string to support rgb
        this.permission = channel.getPermission();
        this.symbol = channel.getSymbol();
        this.created = channel.getCreated();
        this.autoJoin = channel.isAutoJoin();
        this.allowInvites = channel.getAllowInvites();
        this.visibility = channel.getVisibility().name();
    }
    
    @Override
    public int getId() {
        return id;
    }
    
    @Override
    public void setId(int id) {
        this.id = id;
    }
    
    @Override
    public Map<String, Object> serialize() {
        return new HashMap<>() {{
            put("id", id);
            put("type", type);
            put("name", name);
            put("owner", owner);
            put("description", description);
            put("format", format);
            put("prefix", prefix);
            put("color", color);
            put("permission", permission);
            put("symbol", symbol);
            put("created", created);
            put("autoJoin", autoJoin);
            put("allowInvites", allowInvites);
            put("visibility", visibility);
        }};
    }
}