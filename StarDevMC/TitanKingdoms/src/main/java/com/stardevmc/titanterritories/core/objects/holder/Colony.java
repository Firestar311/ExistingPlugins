package com.stardevmc.titanterritories.core.objects.holder;

import com.firestar311.lib.util.Utils;
import com.stardevmc.chat.api.IChatroom;
import com.stardevmc.titanterritories.core.TitanTerritories;
import com.stardevmc.titanterritories.core.objects.kingdom.Plot;
import com.stardevmc.titanterritories.core.chat.ColonyRoom;
import com.stardevmc.titanterritories.core.controller.*;
import com.stardevmc.titanterritories.core.leader.Chief;
import com.stardevmc.titanterritories.core.leader.Leader;
import com.stardevmc.titanterritories.core.objects.interfaces.IHolder;
import com.stardevmc.titanterritories.core.objects.interfaces.IUser;
import com.stardevmc.titanterritories.core.objects.kingdom.*;
import com.stardevmc.titanterritories.core.objects.kingdom.Mail.MailMessage;
import com.stardevmc.titanterritories.core.objects.member.Colonist;
import com.stardevmc.titanterritories.core.objects.member.Member;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.*;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.util.*;

public class Colony implements ConfigurationSerializable, IHolder {
    
    static { ConfigurationSerialization.registerClass(Colony.class); }
    
    private EconomyController<Colony> economyController;
    private ElectionController electionController;
    
    private TitanTerritories plugin = TitanTerritories.getInstance();
    private UUID kingdomUUID;
    private String name; //Name of the colony
    private Location spawnpoint; //The main spawnpoint of the town
    private ClaimController<Colony> claimController; //The chunks that the colony is contained in
    private Chief chief; //The leader of the colony, must be a member of the Kingdom
    private RankController<Colony>  rankController; //The ranks of the colony, these are set by the mayor, however the mayor's own permissions govern what can be set, defaults set by monarch
    private FlagController<Colony> flagController; //The flags of the colony, mayor's perms matter and defaults are set by monarch of the kingdom
    private UserController<Colony, Colonist> userController; //Direct members of the colony, every kingdom member is given a specific rank, kingdom rank overrules permission
    private MailController<Colony> mailController;
    private UUID uuid;
    private WarpController<Colony> warpController;
    private InviteController<Colony> inviteController;
    private IChatroom chatroom;
    
    public Colony(Kingdom kingdom, String name, Location spawnpoint, Chief chief) {
        this();
        this.kingdomUUID = kingdom.getUniqueId();
        this.name = name;
        this.spawnpoint = spawnpoint;
        this.chief = chief;
    }
    
    private Colony() {
        this.name = null;
        this.spawnpoint = null;
        this.chief = null;
        this.claimController = new ClaimController<>(this);
        this.rankController = new RankController<>(this);
        this.flagController = new FlagController<>(this);
        this.userController = new UserController<>(this);
        this.mailController = new MailController<>(this);
        this.economyController = new EconomyController<>(this);
        this.warpController = new WarpController<>(this);
        this.inviteController = new InviteController<>(this);
        this.electionController = new ElectionController<>(this);
        this.chatroom = new ColonyRoom(this);
    }
    
    public Colony(Map<String, Object> serialized) {
        this();
        if (serialized.containsKey("name")) {
            this.name = (String) serialized.get("name");
        }
        
        if (serialized.containsKey("spawnpoint")) {
            this.spawnpoint = (Location) serialized.get("spawnpoint");
        }
        
        if (serialized.containsKey("chief")) {
            this.chief = ((Chief) serialized.get("chief"));
        }
        
        if (serialized.containsKey("rankAmount")) {
            int rankAmount = (int) serialized.get("rankAmount");
            for (int i = 0; i < rankAmount; i++) {
                this.rankController.addRank((Rank) serialized.get("rank" + i));
            }
        }
        
        this.flagController = (FlagController<Colony>) serialized.get("flags");
        
        this.uuid = UUID.fromString((String) serialized.get("uuid"));
        this.kingdomUUID = UUID.fromString((String) serialized.get("kingdomuuid"));
    }
    
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("name", name);
        serialized.put("spawnpoint", spawnpoint);
        if (!claimController.getPlots().isEmpty()) {
            serialized.put("plotAmount", claimController.getPlots().size());
            for (int k = 0; k < claimController.getPlots().size(); k++) {
                Plot plot = claimController.getPlots().get(k);
                serialized.put("plot" + k, plot.getMinimum());
            }
        }
        serialized.put("chief", chief);
        if (!rankController.getRanks().isEmpty()) {
            serialized.put("rankAmount", rankController.getRanks().size());
            for (int r = 0; r < rankController.getRanks().size(); r++) {
                Rank rank = rankController.getRanks().get(r);
                serialized.put("rank" + r, rank);
            }
        }
        
        serialized.put("flags", flagController);
        
        serialized.put("uuid", this.uuid.toString());
        serialized.put("kingdomuuid", this.kingdomUUID.toString());
        return serialized;
    }
    
    public Location getSpawnpoint() {
        return spawnpoint;
    }
    
    public ElectionController getElectionController() {
        return electionController;
    }
    
    public ShopController getShopController() {
        return null;
    }
    
    public Chief getChief() {
        return chief;
    }
    
    public void disband() {
        UUID mailSender = this.chief.getColonist().getUniqueId();
    
        for (Plot plot : this.claimController.getPlots()) {
            plot.setColony(null);
        }
    
        Map<UUID, MailMessage> mailMessages = new HashMap<>();
        double colonyBalance = economyController.getBalance();
        for (Colonist colonist : this.userController.getUsers()) {
            mailMessages.put(colonist.getUniqueId(), new MailMessage(name + " disbanded", "The colony " + name + " was disbanded by the chief.", "Please refer to other lines for your information regarding this action."));
            double percentage = economyController.getContributionTotal(colonist.getUniqueId()) / colonyBalance;
            double total = colonyBalance * percentage;
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(colonist.getUniqueId());
            EconomyResponse response = TitanTerritories.getInstance().getVaultEconomy().depositPlayer(offlinePlayer, total);
        
            if (response.transactionSuccess()) {
                mailMessages.get(colonist.getUniqueId()).addLine("&aYou were given $" + total + " because of your monetary contribution to the colony.");
            } else {
                mailMessages.get(colonist.getUniqueId()).addLine("&cThere was an error while processing your monetary contribution " + response.errorMessage);
            }
        }
    
        for (Invite invite : this.inviteController.getInvites()) {
            Member target = plugin.getMemberManager().getMember(invite.getInvited());
            target.addMail(new Mail(mailSender, target.getUniqueId(), new MailMessage(name + " disbanded", "You were invited to the kingdom " + name, "However it has been disbanded by the monarch")));
        }
    
        for (Colonist colonist : userController.getUsers()) {
            colonist.getMember().addMail(new Mail(mailSender, colonist.getUniqueId(), mailMessages.get(colonist.getUniqueId())));
        }
    }
    
    public void createChatroom() {
        this.chatroom = new ColonyRoom(this);
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Kingdom getKingdom() {
        return TitanTerritories.getInstance().getKingdomManager().getKingdom(kingdomUUID);
    }
    
    public void setKingdom(Kingdom kingdom) {
        this.kingdomUUID = kingdom.getUniqueId();
    }
    
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Colony colony = (Colony) o;
        return name.equals(colony.name) && spawnpoint.equals(colony.spawnpoint);
    }
    
    public int hashCode() {
        return Objects.hash(name, spawnpoint);
    }
    
    public void setSpawnpoint(Location spawnpoint) {
        this.spawnpoint = spawnpoint;
    }
    
    public void sendMemberMessage(String message) {
        List<Colonist> citizens = this.userController.getUsers();
        for (Colonist colonist : citizens) {
            colonist.sendMessage(message);
        }
    }
    
    public UUID getUniqueId() {
        return uuid;
    }
    
    public Leader<?> getLeader() {
        return chief;
    }
    
    public ClaimResponse claim(IUser user, Plot plot) {
        if (plot.hasKingdom()) {
            if (getKingdom() != null) {
                if (!plot.getKingdom().equals(getKingdom())) {
                    return new ClaimResponse(this, user, "plot is claimed by another kingdom", false);
                }
            }
        }
        
        ClaimResponse kingdomClaimResponse = getKingdom().claim(user, plot);
        if (!kingdomClaimResponse.isSuccess()) {
            return new ClaimResponse(this, user, "Plot cannot be claimed by the kingdom, reason: " + kingdomClaimResponse.getMessage(), false);
        }
    
        if (plot.hasColony()) {
            if (!plot.getColony().equals(this)) {
                return new ClaimResponse(this, user, "plot is claimed by another colony", false);
            } else {
                return new ClaimResponse(this, user, "plot is already claimed by your colony", false);
            }
        }
    
        if (plot.hasTown()) {
            return new ClaimResponse(this, user, "plot is claimed by a town", false);
        }
    
        boolean bordersClaim = false;
        List<Chunk> facingChunks = Utils.getFacingChunks(user.getLocation().getChunk());
        for (Chunk chunk : facingChunks) {
            Plot cPlot = plugin.getPlotManager().getPlot(chunk.getBlock(8, 128, 8).getLocation());
            if (cPlot.hasTown()) {
                if (cPlot.getColony().equals(this)) {
                    bordersClaim = true;
                }
            }
        }
    
        if (!bordersClaim) {
            return new ClaimResponse(this, user, "&cplot must border existing colony claim", false);
        }
        getKingdom().getClaimController().addPlot(plot);
        return new ClaimResponse(this, user, "", true);
    }
    
    public Class<? extends IUser> getUserClass() {
        return Colonist.class;
    }
    
    public ClaimController getClaimController() {
        return claimController;
    }
    
    public UserController getUserController() {
        return userController;
    }
    
    public RankController getRankController() {
        return rankController;
    }
    
    public InviteController getInviteController() {
        return inviteController;
    }
    
    public FlagController getFlagController() {
        return flagController;
    }
    
    public void setUniqueId(UUID uuid) {
        this.uuid = uuid;
    }
    
    public MailController<Colony> getMailController() {
        return mailController;
    }
    
    public WarpController getWarpController() {
        return warpController;
    }
    
    public EconomyController getEconomyController() {
        return economyController;
    }
    
    public void setLeader(IUser user) {
        Colonist colonist = this.userController.get(user.getUniqueId());
        this.chief = new Chief(colonist.getMember(), colonist.getJoinDate(), this.uuid);
    }
    
    public IChatroom getChatroom() {
        return chatroom;
    }
}