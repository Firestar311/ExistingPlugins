package com.stardevmc.titanterritories.core.objects.kingdom;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.*;

public class Vote implements ConfigurationSerializable {
    private UUID voter;
    private UUID candidate;
    
    public Vote(UUID voter, UUID candidate) {
        this.voter = voter;
        this.candidate = candidate;
    }
    
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("voter", this.voter.toString());
        serialized.put("candidate", this.candidate.toString());
        return serialized;
    }
    
    public static Vote deserialize(Map<String, Object> serialized) {
        UUID voter = UUID.fromString((String) serialized.get("voter"));
        UUID candidate = UUID.fromString((String) serialized.get("candidate"));
        return new Vote(voter, candidate);
    }
    
    public UUID getVoter() {
        return voter;
    }
    
    public UUID getCandidate() {
        return candidate;
    }
}