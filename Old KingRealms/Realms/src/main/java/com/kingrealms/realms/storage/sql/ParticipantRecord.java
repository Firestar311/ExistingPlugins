package com.kingrealms.realms.storage.sql;

import com.kingrealms.realms.channel.Participant;
import com.starmediadev.lib.sql.IRecord;
import com.starmediadev.lib.sql.Row;

import java.util.HashMap;
import java.util.Map;

public class ParticipantRecord implements IRecord {
    
    private int id, playerId, inviterId, actorId;
    private boolean read;
    private String role, previousRole;
    
    public ParticipantRecord(Row row) {
        this.id = row.getInt("id");
        this.playerId = row.getInt("playerId");
        this.inviterId = row.getInt("inviterId");
        this.actorId = row.getInt("actorId");
        this.read = row.getBoolean("read");
        this.role = row.getString("role");
        this.previousRole = row.getString("previousRole");
    }
    
    public ParticipantRecord(@SuppressWarnings("unused") Participant participant) {
        //TODO
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
            put("playerId", playerId);
            put("inviterId", inviterId);
            put("actorId", actorId);
            put("read", read);
            put("role", role);
            put("previousRole", previousRole);
        }};
    }
}