package com.kingrealms.realms.territory.base;

import com.kingrealms.realms.IOwner;
import com.kingrealms.realms.Realms;
import com.kingrealms.realms.channel.Channel;
import com.kingrealms.realms.channel.enums.Role;
import com.kingrealms.realms.economy.account.Account;
import com.kingrealms.realms.flight.FlightInfo;
import com.kingrealms.realms.limits.LimitBoost;
import com.kingrealms.realms.limits.limit.Limit;
import com.kingrealms.realms.plot.Plot;
import com.kingrealms.realms.plot.claimed.ClaimedPlot;
import com.kingrealms.realms.profile.RealmProfile;
import com.kingrealms.realms.territory.base.member.Member;
import com.kingrealms.realms.territory.base.response.*;
import com.kingrealms.realms.territory.enums.*;
import com.starmediadev.lib.pagination.IElement;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.Map.Entry;

@SerializableAs("Territory")
public abstract class Territory implements ConfigurationSerializable, IElement, IOwner {
    
    protected final Realms plugin = Realms.getInstance(); //cache
    protected Account account; //cache
    protected long accountNumber; //mysql
    protected Channel channel; //cache
    protected int channelId; //mysql
    protected long createdDate; //mysql
    protected Set<Invite> invites = new HashSet<>(); //mysql
    protected Set<Member> members = new HashSet<>(); //mysql
    protected Set<ClaimedPlot> plots = new HashSet<>(); //mysql
    protected Privacy privacy = Privacy.PRIVATE; //mysql
    protected Location spawnpoint; //mysql
    @Deprecated
    protected String uniqueId; //Convert to mysql auto-id
    protected String name; //mysql
    protected Map<String, LimitBoost> limitBoosts = new HashMap<>(); //mysql
    protected Map<String, Relation> relationships = new HashMap<>(); //mysql
    protected FlightInfo flightInfo = new FlightInfo();
    
    public Territory(String name) {
        this.name = name;
        this.createdDate = System.currentTimeMillis();
    }
    
    public Territory(Map<String, Object> serialized) {
        this.name = (String) serialized.get("name");
        this.uniqueId = (String) serialized.get("uniqueId");
        this.privacy = Privacy.valueOf((String) serialized.get("privacy"));
        this.spawnpoint = (Location) serialized.get("spawnpoint");
        this.createdDate = Long.parseLong((String) serialized.get("createdDate"));
        if (serialized.containsKey("accountNumber")) {
            this.accountNumber = Long.parseLong((String) serialized.get("accountNumber"));
        }
        
        for (Entry<String, Object> entry : serialized.entrySet()) {
            if (entry.getKey().startsWith("member-")) {
                Member member = (Member) entry.getValue();
                this.members.add(member);
            } else if (entry.getKey().startsWith("invite-")) {
                Invite invite = (Invite) entry.getValue();
                this.invites.add(invite);
            } else if (entry.getKey().startsWith("plot-")) {
                ClaimedPlot claimedPlot = (ClaimedPlot) entry.getValue();
                this.plots.add(claimedPlot);
            } else if (entry.getKey().startsWith("limitboost-")) {
                LimitBoost limitBoost = (LimitBoost) entry.getValue();
                this.limitBoosts.put(limitBoost.getLimitId(), limitBoost);
            } else if (entry.getKey().startsWith("relationship-")) {
                String territory = entry.getKey().split("-")[1];
                Relation relation = Relation.valueOf((String) entry.getValue());
                this.relationships.put(territory, relation);
            }
        }
        
        if (serialized.containsKey("flightInfo")) {
            this.flightInfo = (FlightInfo) serialized.get("flightInfo");
        }
        
        try {
            this.channelId = Integer.parseInt((String) serialized.get("channelId"));
        } catch (Exception e) {
            this.channelId = -10;
        }
    }
    
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("uniqueId", this.uniqueId);
        serialized.put("name", this.name);
        serialized.put("spawnpoint", this.spawnpoint);
        serialized.put("privacy", this.privacy.name());
        serialized.put("createdDate", this.createdDate + "");
        serialized.put("accountNumber", this.accountNumber + "");
        for (Member member : this.members) {
            serialized.put("member-" + member.getUniqueId().toString(), member);
        }
        
        for (Invite invite : this.invites) {
            serialized.put("invite-" + invite.getTarget().toString(), invite);
        }
        
        for (ClaimedPlot plot : this.plots) {
            serialized.put("plot-" + plot.getId(), plot);
        }
        
        if (!limitBoosts.isEmpty()) {
            for (LimitBoost limitBoost : this.limitBoosts.values()) {
                serialized.put("limitboost-" + limitBoost.getLimitId(), limitBoost);
            }
        }
        
        if (!this.relationships.isEmpty()) {
            this.relationships.forEach((territory, relation) -> serialized.put("relationship-" + territory, relation.name()));
        }
        
        serialized.put("channelId", channelId + "");
        serialized.put("flightInfo", flightInfo);
        return serialized;
    }
    
    public Map<String, Relation> getRelationships() {
        return new HashMap<>(this.relationships);
    }
    
    public boolean canEnter(RealmProfile profile) {
        if (isMember(profile)) {
            return true;
        }
        
        Territory profileTerritory = Realms.getInstance().getTerritoryManager().getTerritory(profile);
        if (profileTerritory != null) {
            Relation relation = getRelationship(profileTerritory);
            Relation otherRelation = profileTerritory.getRelationship(this);
            if (relation == Relation.ALLY && otherRelation == Relation.ALLY) {
                return true;
            } else if (relation != Relation.ENEMY || otherRelation != Relation.ENEMY) {
                return getPrivacy() == Privacy.OPEN;
            }
        }
        
        return false;
    }
    
    public boolean isMember(RealmProfile profile) {
        return getMember(profile.getUniqueId()) != null;
    }
    
    public Relation getRelationship(Territory territory) {
        if (this.relationships.containsKey(territory.getUniqueId())) {
            return relationships.get(territory.getUniqueId());
        }
        
        return Relation.NEUTRAL;
    }
    
    public Privacy getPrivacy() {
        return privacy;
    }
    
    public Member getMember(UUID uuid) {
        for (Member member : this.members) {
            if (member.getUniqueId().equals(uuid)) {
                return member;
            }
        }
        return null;
    }
    
    public String getUniqueId() {
        return uniqueId;
    }
    
    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }
    
    public void unclaim(Location location) {
        ClaimedPlot plot = getPlot(location);
        this.plots.remove(plot);
    }
    
    public ClaimedPlot getPlot(Location location) {
        for (ClaimedPlot plot : this.plots) {
            if (plot.contains(location)) {
                return plot;
            }
        }
        return null;
    }
    
    public void disband() {
        if (getLeader() != null) {
            sendMemberMessage("&cThe hamlet " + this.name + " has been disbanded by " + this.getLeader().getName());
        }
        Realms.getInstance().getChannelManager().removeChannel(getChannel());
        //Other cleanup as more is implemented
    }
    
    public void sendMemberMessage(String message) {
        for (Member member : this.members) {
            member.sendMessage(message);
        }
    }
    
    public Member getLeader() {
        for (Member member : this.members) {
            if (member.getRank() == Rank.LEADER) {
                return member;
            }
        }
        
        return null;
    }
    
    public Channel getChannel() {
        if (this.channel == null) {
            channel = Realms.getInstance().getChannelManager().getChannel(this.channelId);
            if (channel == null) {
                createChannel();
            }
        }
        
        return channel;
    }
    
    public abstract void createChannel();
    
    public void removeMember(UUID uniqueId) {
        Member member = getMember(uniqueId);
        this.members.remove(member);
    }
    
    public boolean contains(Entity entity) {
        return contains(entity.getLocation());
    }
    
    public boolean contains(Location location) {
        for (ClaimedPlot plot : this.plots) {
            if (plot.contains(location)) {
                return true;
            }
        }
        return false;
    }
    
    public SpawnpointResponse changeSpawnpoint(RealmProfile player) {
        return changeSpawnpoint(player.getLocation(), player.getUniqueId());
    }
    
    public SpawnpointResponse changeSpawnpoint(Location location, UUID actor) {
        Member member = getMember(actor);
        if (member == null) { return SpawnpointResponse.NOT_A_MEMBER; }
        if (member.getRank().getOrder() > Rank.MANAGER.getOrder()) { return SpawnpointResponse.NO_PERMISSION; }
        if (!this.contains(location)) { return SpawnpointResponse.NOT_INT_CLAIM; }
        
        this.spawnpoint = location;
        return SpawnpointResponse.SUCCESS;
    }
    
    public boolean changePrivacy(Privacy privacy, RealmProfile player) {
        return changePrivacy(privacy, player.getUniqueId());
    }
    
    public boolean changePrivacy(Privacy privacy, UUID player) {
        Member member = getMember(player);
        if (member == null) { return false; }
        if (member.getRank().getOrder() > Rank.MANAGER.getOrder()) { return false; }
        
        this.privacy = privacy;
        return true;
    }
    
    public boolean changeName(String name, RealmProfile player) {
        return changeName(name, player.getUniqueId());
    }
    
    public boolean changeName(String name, UUID uuid) {
        Member member = getMember(uuid);
        if (member == null) { return false; }
        if (member.getRank().getOrder() > Rank.LEADER.getOrder()) { return false; }
        
        this.name = name;
        return true;
    }
    
    public void transfer(UUID uuid) {
        Member currentLeader = getLeader();
        Member newLeader = getMember(uuid);
        if (currentLeader != null) { currentLeader.setRank(Rank.MEMBER); }
        newLeader.setRank(Rank.LEADER);
    }
    
    public RankResponse changeMemberRank(UUID target, UUID actor, Rank rank) {
        Member targetMember = getMember(target);
        Member actorMember = getMember(actor);
        
        if (targetMember == null) {
            return RankResponse.NOT_A_MEMBER;
        }
        
        if (targetMember.getRank().getOrder() <= actorMember.getRank().getOrder()) {
            return RankResponse.TARGET_MEMBER_HIGHER;
        }
        
        if (rank.getOrder() <= actorMember.getRank().getOrder()) {
            return RankResponse.NEW_RANK_HIGHER_THAN_ACTOR;
        }
        
        targetMember.setRank(rank);
        return RankResponse.SUCCESS;
    }
    
    public ClaimResponse claim(RealmProfile player) {
        return claim(player.getLocation(), player.getUniqueId());
    }
    
    public ClaimResponse claim(Location location, UUID actor) {
        Member member = getMember(actor);
        if (member == null) { return ClaimResponse.NOT_A_MEMBER; }
        if (member.getRank().getOrder() > Rank.MEMBER.getOrder()) { return ClaimResponse.NO_PERMISSION; }
        if (this.contains(location)) { return ClaimResponse.ALREADY_CLAIMED; }
        if (plugin.getSpawn().contains(location)) { return ClaimResponse.IN_SPAWN; }
        if (plugin.getWarzone().contains(location)) { return ClaimResponse.IN_WARZONE; }
        if (this.getPlots().size() >= (Integer) plugin.getLimitsManager().getLimit("territory_claim_limit").getValue()) {
            return ClaimResponse.CLAIM_LIMIT;
        }
        
        for (Territory territory : plugin.getTerritoryManager().getTerritories()) {
            if (territory.contains(location)) {
                return ClaimResponse.CLAIMED_OTHER;
            }
        }
        
        addPlot(plugin.getPlotManager().getPlot(location), actor, System.currentTimeMillis());
        return ClaimResponse.SUCCESS;
    }
    
    public Set<ClaimedPlot> getPlots() {
        return plots;
    }
    
    public void claim(Location location, CommandSender sender) {
        if (!(sender instanceof Player)) { return; }
        Player player = (Player) sender;
        addPlot(plugin.getPlotManager().getPlot(location), player.getUniqueId(), System.currentTimeMillis());
    }
    
    public abstract void addPlot(Plot plot, UUID actor, long time);
    
    public boolean contains(Block block) {
        return contains(block.getLocation());
    }
    
    public FlightInfo getFlightInfo() {
        if (flightInfo == null) {
            this.flightInfo = new FlightInfo();
        }
        return flightInfo;
    }
    
    public ClaimResponse unclaim(RealmProfile player) {
        return unclaim(player.getLocation(), player.getUniqueId());
    }
    
    public ClaimResponse unclaim(Location location, UUID actor) {
        Member member = getMember(actor);
        if (member == null) { return ClaimResponse.NOT_A_MEMBER; }
        if (member.getRank().getOrder() > Rank.TRUSTED.getOrder()) { return ClaimResponse.NO_PERMISSION; }
        if (!this.contains(location)) { return ClaimResponse.NOT_CLAIMED; }
        
        ClaimedPlot plot = getPlot(location);
        if (plot == null) { return ClaimResponse.UNKOWN_ERROR; }
        
        Member plotClaimer = getMember(plot.getActor());
        if (plotClaimer != null) {
            if (plotClaimer.getRank().getOrder() < member.getRank().getOrder()) {
                return ClaimResponse.CLAIM_ACTOR_HIGHER;
            }
        }
        
        this.plots.remove(plot);
        return ClaimResponse.SUCCESS;
    }
    
    public boolean isMember(Player player) {
        return getMember(player.getUniqueId()) != null;
    }
    
    public MemberResponse removeMember(UUID target, UUID actor) {
        Member targetMember = getMember(target);
        Member actorMember = getMember(actor);
        
        if (targetMember == null) {
            return MemberResponse.NOT_A_MEMBER;
        }
        
        if (actorMember == null) {
            this.members.remove(targetMember);
            return MemberResponse.SUCCESS;
        }
        
        if (actorMember.getRank().getOrder() > Rank.MANAGER.getOrder()) {
            return MemberResponse.NO_PERMISSION;
        }
        
        if (targetMember.getRank().getOrder() <= actorMember.getRank().getOrder()) {
            return MemberResponse.TARGET_HIGHER;
        }
        
        this.members.remove(targetMember);
        getChannel().removeParticipant(targetMember.getUniqueId());
        return MemberResponse.SUCCESS;
    }
    
    public boolean isInvited(UUID uniqueId) {
        Invite invite = getInvite(uniqueId);
        return invite != null;
    }
    
    public Invite getInvite(UUID target) {
        for (Invite invite : invites) {
            if (invite.getTarget().equals(target)) {
                return invite;
            }
        }
        return null;
    }
    
    public InviteResponse denyInvite(UUID target) {
        Invite invite = getInvite(target);
        if (invite == null) {
            return InviteResponse.NOT_INVITED;
        }
        
        RealmProfile profile = plugin.getProfileManager().getProfile(target);
        if (profile == null) {
            return InviteResponse.NO_PROFILE;
        }
        
        this.invites.remove(invite);
        return InviteResponse.SUCCESS;
    }
    
    public InviteResponse addInvite(UUID target, UUID actor) {
        Member member = getMember(actor);
        if (member == null || member.getRank().getOrder() > Rank.MANAGER.getOrder()) {
            return InviteResponse.ACTOR_NO_PERMISSION;
        }
        
        Invite invite = getInvite(target);
        if (invite != null) {
            return InviteResponse.ALREADY_INVITED;
        }
        
        this.invites.add(new Invite(target, actor, System.currentTimeMillis()));
        return InviteResponse.SUCCESS;
    }
    
    public InviteResponse removeInvite(UUID target, UUID actor) {
        Member member = getMember(actor);
        if (member == null || member.getRank().getOrder() > Rank.MANAGER.getOrder()) {
            return InviteResponse.ACTOR_NO_PERMISSION;
        }
        
        Invite invite = getInvite(target);
        if (invite == null) {
            return InviteResponse.NOT_INVITED;
        }
        
        this.invites.remove(invite);
        return InviteResponse.SUCCESS;
    }
    
    public InviteResponse acceptInvite(UUID target) {
        Invite invite = getInvite(target);
        if (invite == null) {
            return InviteResponse.NOT_INVITED;
        }
        
        RealmProfile profile = plugin.getProfileManager().getProfile(target);
        if (profile == null) {
            return InviteResponse.NO_PROFILE;
        }
        
        Member member = new Member(profile);
        member.setRank(Rank.MEMBER);
        member.setInvite(invite);
        member.setJoinDate(System.currentTimeMillis());
        Role channelRole = Role.MEMBER;
        if (member.getRank().getOrder() <= Rank.MANAGER.getOrder()) {
            channelRole = Role.MANAGER;
        }
        getChannel().addParticipant(member.getUniqueId(), channelRole);
        this.members.add(member);
        this.invites.remove(invite);
        return InviteResponse.SUCCESS;
    }
    
    public boolean addMember(UUID uuid) {
        return addMember(uuid, Rank.MEMBER);
    }
    
    public boolean addMember(UUID uuid, Rank rank) {
        RealmProfile profile = plugin.getProfileManager().getProfile(uuid);
        if (profile == null) {
            return false;
        }
        
        Member member = new Member(profile);
        member.setRank(rank);
        Role channelRole = Role.MEMBER;
        if (member.getRank().getOrder() <= Rank.MANAGER.getOrder()) {
            channelRole = Role.MANAGER;
        }
        getChannel().addParticipant(member.getUniqueId(), channelRole);
        member.setJoinDate(System.currentTimeMillis());
        this.members.add(member);
        return true;
    }
    
    public void sendMemberMessage(String... message) {
        for (Member member : this.members) {
            member.getRealmProfile().sendMessage(message);
        }
    }
    
    @Override
    public String formatLine(String... args) {
        String privacy = this.privacy.getColor() + "[" + this.privacy.getRawDisplayName() + "]";
        int onlineMembers = 0;
        for (Member member : this.members) {
            if (member.getRealmProfile().getUser().isOnline()) {
                onlineMembers++;
            }
        }
        String members = "&6(" + onlineMembers + "/" + this.members.size() + ")";
        return " &8- &d" + this.name + " " + privacy + " " + members;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public ClaimedPlot getPlot(String uniqueId) {
        for (ClaimedPlot claimedPlot : plots) {
            if (claimedPlot.getPlot().getUniqueId().equalsIgnoreCase(uniqueId)) {
                return claimedPlot;
            }
        }
        
        return null;
    }
    
    public Location getSpawnpoint() {
        return spawnpoint;
    }
    
    public void setSpawnpoint(Location spawnpoint) {
        this.spawnpoint = spawnpoint;
    }
    
    public Set<Member> getMembers() {
        return members;
    }
    
    public Set<Invite> getInvites() {
        return invites;
    }
    
    public long getCreatedDate() {
        return createdDate;
    }
    
    public void setPrivacy(Privacy privacy, UUID actor) {
        Member member = getMember(actor);
        if (member != null || member.getRank().getOrder() < Rank.MANAGER.getOrder()) {
            this.privacy = privacy;
        }
    }
    
    public Account getAccount() {
        if (this.account == null) {
            this.account = plugin.getEconomyManager().getAccountHandler().getAccount(accountNumber);
            if (this.account == null) {
                this.createDefaultAccount();
            }
        }
        
        if (account != null) {
            if (accountNumber != this.account.getAccountNumber()) {
                this.account = plugin.getEconomyManager().getAccountHandler().getAccount(accountNumber);
                if (this.account == null) {
                    this.createDefaultAccount();
                }
            }
        } else {
            this.createDefaultAccount();
        }
        
        if (this.account == null) {
            this.account = plugin.getEconomyManager().getAccountHandler().createAccount(this, Rank.MEMBER, Rank.TRUSTED);
            this.accountNumber = this.account.getAccountNumber();
        }
        
        return account;
    }
    
    public void createDefaultAccount() {
        if (this.account == null && this.accountNumber == 0) {
            Account account = plugin.getEconomyManager().getAccountHandler().createAccount(this, Rank.MEMBER, Rank.TRUSTED);
            this.accountNumber = account.getAccountNumber();
            this.account = account;
            plugin.getEconomyManager().getTransactionHandler().initialDeposit(account);
        } else {
            this.account = plugin.getEconomyManager().getAccountHandler().getAccount(this.accountNumber);
        }
    }
    
    @Override
    public void sendMessage(String message) {
        sendMemberMessage(message);
    }
    
    @Override
    public String getIdentifier() {
        return getUniqueId();
    }
    
    @Override
    public Number getLimitValue(Limit limit) {
        LimitBoost boost = getLimitBoost(limit);
        if (boost != null) {
            return boost.getOperator().calculate(limit.getValue(), boost.getValue());
        }
        return limit.getValue();
    }
    
    @Override
    public void addLimitBoost(LimitBoost boost) {
        this.limitBoosts.put(boost.getLimit().getId(), boost);
    }
    
    @Override
    public LimitBoost getLimitBoost(Limit limit) {
        return this.limitBoosts.get(limit.getId());
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    public void setRelationship(Territory territory, Relation ally) {
        this.relationships.put(territory.getUniqueId(), ally);
    }
    
    public void removeRelationship(Territory territory) {
        this.relationships.remove(territory.getUniqueId());
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Territory territory = (Territory) o;
        return Objects.equals(uniqueId, territory.uniqueId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(uniqueId);
    }
}