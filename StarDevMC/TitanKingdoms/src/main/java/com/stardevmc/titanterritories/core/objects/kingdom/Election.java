package com.stardevmc.titanterritories.core.objects.kingdom;

import com.stardevmc.titanterritories.core.objects.enums.ElectionReason;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.*;
import java.util.Map.Entry;

public class Election implements ConfigurationSerializable {
    
    private UUID electedCandidate;
    private ElectionReason reason;
    private int totalVotes;
    private List<Vote> votes = new ArrayList<>();
    
    public Election(ElectionReason reason, int totalVotes) {
        this.reason = reason;
        this.totalVotes = totalVotes;
    }
    
    public Election(ElectionReason reason, int totalVotes, List<Vote> votes, UUID electedCandidate) {
        this.reason = reason;
        this.totalVotes = totalVotes;
        this.votes = votes;
        this.electedCandidate = electedCandidate;
    }
    
    public static Election deserialize(Map<String, Object> serialized) {
        ElectionReason reason = ElectionReason.valueOf((String) serialized.get("reason"));
        int totalVotes = Integer.parseInt((String) serialized.get("totalVotes"));
        UUID electedCandidate = null;
        if (serialized.containsKey("electedCandidate")) {
            electedCandidate = UUID.fromString((String) serialized.get("electedCandidate"));
        }
        List<Vote> votes = new ArrayList<>();
        for (Entry<String, Object> entry : serialized.entrySet()) {
            if (entry.getKey().contains("vote")) {
                votes.add((Vote) entry.getValue());
            }
        }
        return new Election(reason, totalVotes, votes, electedCandidate);
    }
    
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("reason", reason.name());
        serialized.put("totalVotes", this.totalVotes + "");
        if (electedCandidate != null) { serialized.put("electedCandidate", this.electedCandidate.toString()); }
        for (int i = 0; i < votes.size(); i++) {
            serialized.put("vote" + i, votes.get(i));
        }
        
        return serialized;
    }
    
    public void addVote(Vote vote) {
        this.votes.add(vote);
    }
    
    public ElectionReason getReason() {
        return reason;
    }
    
    public UUID getElectedCandidate() {
        return electedCandidate;
    }
    
    public void setElectedCandidate(UUID electedCandidate) {
        this.electedCandidate = electedCandidate;
    }
    
    public int getTotalVotes() {
        return totalVotes;
    }
    
    public int getTotalCandidateVotes(UUID candidate) {
        int votes = 0;
        for (Vote vote : this.votes) {
            if (vote.getCandidate().equals(candidate)) {
                votes++;
            }
        }
        return votes;
    }
    
    public Entry<UUID, Integer> getWinningCandidate() {
        Entry<UUID, Integer> topEntry = null;
        
        Map<UUID, Integer> leaderboard = getLeaderboard();
        for (Entry<UUID, Integer> entry : leaderboard.entrySet()) {
            if (topEntry == null) {
                topEntry = entry;
            } else {
                if (topEntry.getValue() < entry.getValue()) {
                    topEntry = entry;
                }
            }
        }
        return topEntry;
    }
    
    public Map<UUID, Integer> getLeaderboard() {
        Map<UUID, Integer> totalVotes = new HashMap<>();
        for (Vote vote : getVotes()) {
            if (totalVotes.containsKey(vote.getCandidate())) {
                totalVotes.put(vote.getCandidate(), totalVotes.get(vote.getCandidate()) + 1);
            } else {
                totalVotes.put(vote.getCandidate(), 1);
            }
        }
        return totalVotes;
    }
    
    public List<Vote> getVotes() {
        return votes;
    }
}