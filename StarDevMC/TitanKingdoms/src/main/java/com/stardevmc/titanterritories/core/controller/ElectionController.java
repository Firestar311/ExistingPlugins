package com.stardevmc.titanterritories.core.controller;

import com.firestar311.lib.util.Utils;
import com.stardevmc.titanterritories.core.TitanTerritories;
import com.stardevmc.titanterritories.core.objects.enums.ElectionReason;
import com.stardevmc.titanterritories.core.objects.holder.Colony;
import com.stardevmc.titanterritories.core.objects.holder.Town;
import com.stardevmc.titanterritories.core.objects.interfaces.IHolder;
import com.stardevmc.titanterritories.core.objects.interfaces.IUser;
import com.stardevmc.titanterritories.core.objects.kingdom.Election;
import com.stardevmc.titanterritories.core.objects.kingdom.Vote;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;

import java.util.*;
import java.util.Map.Entry;

public class ElectionController<T extends IHolder> extends Controller<T> {
    
    private Election currentElection;
    private List<Election> pastElections = new ArrayList<>();
    
    public ElectionController(T holder) {
        super(holder);
    }
    
    private ElectionController() {
    }
    
    public void handleCommand(Command cmd, T holder, IUser user, String[] args) {
        if (!(args.length > 1)) {
            user.sendMessage("&cYou must provide a sub command.");
            return;
        }
        
        if (Utils.checkCmdAliases(args, 1, "vote")) {
            if (currentElection == null) {
                user.sendMessage("&cThere is no election running.");
                return;
            }
            
            if (!(args.length > 2)) {
                user.sendMessage("&cYou must provide a name.");
                return;
            }
            
            if (args[2].equalsIgnoreCase(user.getName())) {
                user.sendMessage("&cYou cannot vote for yourself.");
                return;
            }
            
            IUser target = holder.getUserController().get(args[2]);
            if (target == null) {
                user.sendMessage("&cThat player either does not exist or is not a member of your " + holder.getClass().getSimpleName());
                return;
            }
            
            this.currentElection.addVote(new Vote(user.getUniqueId(), target.getUniqueId()));
            user.sendMessage("&aRegistered your vote for " + target.getName() + ", they now have " + currentElection.getTotalCandidateVotes(target.getUniqueId()));
            
            if (this.currentElection.getTotalVotes() == this.currentElection.getVotes().size()) {
                Entry<UUID, Integer> winningCandidate = this.currentElection.getWinningCandidate();
                IUser candidate = holder.getUserController().get(winningCandidate.getKey());
                holder.sendMemberMessage("&6&lThe election for a new leader for your " + holder.getClass().getSimpleName() + " has finished!");
                sendDelayedMessage(holder, 2, "&6&lThe new Leader is " + candidate.getName() + " by a vote of " + winningCandidate.getValue());
                holder.setLeader(candidate);
                if (holder instanceof Town || holder instanceof Colony) {
                    sendDelayedMessage(holder, 3, "&6&lIf you disagree with this decision, please contact your Kingdom Monarch.");
                } else {
                    sendDelayedMessage(holder, 3, "&6&lIf you disagree with this decision, the new leader must be banned before a new one can be elected.");
                }
                
                this.pastElections.add(currentElection);
                this.currentElection = null;
                holder.getUserController().setLocked(false, null);
            }
        } else if (Utils.checkCmdAliases(args, 1, "leaderboard")) {
            if (currentElection == null) {
                user.sendMessage("&cThere is no election running.");
                return;
            }
            
            user.sendMessage("&6Current Election Leaderboard");
            Map<UUID, Integer> leaderboard = currentElection.getLeaderboard();
            leaderboard.forEach(((uuid, integer) -> {
                IUser target = holder.getUserController().get(uuid);
                user.sendMessage("&7" + target.getName() + " has " + integer + " vote(s)");
            }));
        } else if (Utils.checkCmdAliases(args, 1, "create")) {
            if (!user.getPlayer().hasPermission("titankingdoms.admin.election.create")) {
                user.sendMessage("&cYou do not have permission to start elections");
                return;
            }
            
            if (!(args.length > 2)) {
                user.sendMessage("&cYou must provide an election reason.");
                return;
            }
            
            ElectionReason reason = ElectionReason.valueOf(args[2].toUpperCase());
            this.startElection(reason);
        }
    }
    
    @SuppressWarnings("SameParameterValue")
    private void sendDelayedMessage(T holder, int seconds, String message) {
        Bukkit.getScheduler().runTaskLater(TitanTerritories.getInstance(), () ->  holder.sendMemberMessage(message), seconds * 20L);
    }
    
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("currentElection", this.currentElection);
        serialized.put("pastAmount", this.pastElections.size() + "");
        for (int i = 0; i < pastElections.size(); i++) {
            serialized.put("past" + i, pastElections.get(i));
        }
        return serialized;
    }
    
    public static ElectionController deserialize(Map<String, Object> serialized) {
        Election currentElection = (Election) serialized.get("currentElection");
        int pastAmount = Integer.parseInt((String) serialized.get("pastAmount"));
        List<Election> pastElections = new ArrayList<>();
        for (int i = 0; i < pastAmount; i++) {
            Election pastElection = (Election) serialized.get("past" + i);
            pastElections.add(pastElection);
        }
        ElectionController controller = new ElectionController();
        controller.currentElection = currentElection;
        controller.pastElections = pastElections;
        return controller;
    }
    
    public void startElection(ElectionReason reason) {
        if (currentElection != null) return;
        
        getHolder().getUserController().remove(getHolder().getLeader().getUser().getUniqueId());
        getHolder().setLeader(null);
        Election election = new Election(reason, getHolder().getUserController().getUsers().size());
        getHolder().getUserController().setLocked(true, null);
        
        String holderName = getHolder().getClass().getSimpleName();
        this.currentElection = election;
        String message = "&6";
        switch (reason) {
            case ADMIN: message += "A Server Admin has started an election for a new leader.";
                break;
            case LEADER_SERVER_BANNED: message += "Your " + holderName + "'s leader has been banned from the server by a staff member, an election has started to determine the new leader.";
                break;
            case LEADER_KINGDOM_BANNED: message += "Your " + holderName + "'s leader has been banned from the kingdom, an election has started to determine the new leader.";
                break;
            case KINGDOM_STAFF: message += "Your Kingdom's Staff has started an election for a new leader.";
                break;
        }
        message += "\nPlease use /" + getHolder().getClass().getSimpleName().toLowerCase() + " election vote <name> to vote for a new leader.";
        holder.sendMemberMessage(message);
    }
    
    public Election getCurrentElection() {
        return currentElection;
    }
    
    public void setCurrentElection(Election currentElection) {
        this.currentElection = currentElection;
    }
    
    public List<Election> getPastElections() {
        return pastElections;
    }
}