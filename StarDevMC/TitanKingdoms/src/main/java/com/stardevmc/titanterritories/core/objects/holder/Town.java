package com.stardevmc.titanterritories.core.objects.holder;

import com.firestar311.lib.pagination.IElement;
import com.firestar311.lib.util.Utils;
import com.stardevmc.chat.api.IChatroom;
import com.stardevmc.titanterritories.core.TitanTerritories;
import com.stardevmc.titanterritories.core.chat.TownRoom;
import com.stardevmc.titanterritories.core.controller.*;
import com.stardevmc.titanterritories.core.leader.Baron;
import com.stardevmc.titanterritories.core.leader.Leader;
import com.stardevmc.titanterritories.core.objects.interfaces.IHolder;
import com.stardevmc.titanterritories.core.objects.interfaces.IUser;
import com.stardevmc.titanterritories.core.objects.kingdom.*;
import com.stardevmc.titanterritories.core.objects.kingdom.Mail.MailMessage;
import com.stardevmc.titanterritories.core.objects.member.Member;
import com.stardevmc.titanterritories.core.objects.member.Resident;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.*;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.*;

public class Town implements ConfigurationSerializable, IElement, IHolder {
    private EconomyController<Town> economyController;
    private ElectionController<Town> electionController;
    private TitanTerritories plugin = TitanTerritories.getInstance();
    private Baron baron; //The baron of the town, must be a member of the Kingdom
    private ClaimController<Town> claimController; //The chunks that the town is contained in, must also be within the owning kingdom
    private FlagController<Town> flagController; //The flags of the town, mayor's perms matter and defaults are set by monarch of the kingdom
    private InviteController<Town> inviteController;
    private UserController<Town, Resident> userController; //Direct members of the town, every kingdom member is given a specific rank, kingdom rank overrules permission
    private String name; //The name of the town
    private RankController<Town> rankController; //The ranks of the Town, these are set by the mayor, however the mayor's own permissions govern what can be set, defaults set by monarch
    private MailController<Town> mailController;
    private Location spawnpoint; //The main spawnpoint of the town
    private UUID uuid, kingdomUUID;
    private WarpController<Town> warpController;
    private IChatroom chatroom;
    
    public Town(String name, Location spawnpoint, Baron baron) {
        this();
        this.name = name;
        this.spawnpoint = spawnpoint;
        this.baron = baron;
    }
    
    private Town() {
        this.claimController = new ClaimController<>(this);
        this.rankController = new RankController<>(this);
        this.flagController = new FlagController<>(this);
        this.userController = new UserController<>(this);
        this.inviteController = new InviteController<>(this);
        this.mailController = new MailController<>(this);
        this.userController.add(getBaron().getResident());
        this.economyController = new EconomyController<>(this);
        this.warpController = new WarpController<>(this);
        this.electionController = new ElectionController<>(this);
        this.chatroom = new TownRoom(this);
    }
    
    public Town(Map<String, Object> serialized) {
        this();
        this.name = (String) serialized.get("name");
        this.spawnpoint = (Location) serialized.get("spawnpoint");
        this.baron = (Baron) serialized.get("baron");
        
        if (serialized.containsKey("rankAmount")) {
            int rankAmount = (int) serialized.get("rankAmount");
            for (int i = 0; i < rankAmount; i++) {
                this.rankController.getRanks().add((Rank) serialized.get("rank" + i));
            }
        }
        
        this.flagController = (FlagController<Town>) serialized.get("flags");
        this.uuid = UUID.fromString((String) serialized.get("uuid"));
        
        if (serialized.containsKey("kingdomuuid")) {
            this.kingdomUUID = UUID.fromString((String) serialized.get("kingdomuuid"));
        }
    }
    
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("name", this.name);
        serialized.put("spawnpoint", this.spawnpoint);
        serialized.put("baron", this.baron);
        if (!rankController.getRanks().isEmpty()) {
            serialized.put("rankAmount", this.rankController.getRanks().size());
            for (int i = 0; i < this.rankController.getRanks().size(); i++) {
                serialized.put("rank" + i, this.rankController.getRanks().get(i));
            }
        }
        
        serialized.put("flags", this.flagController);
        
        serialized.put("uuid", uuid.toString());
        serialized.put("kingdomuuid", this.kingdomUUID.toString());
        return serialized;
    }
    
    public String formatLine(String... args) {
        return " &8- &e" + name + " &7-> &e" + this.baron.getName();
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void disband() {
        UUID mailSender = this.baron.getResident().getUniqueId();
    
        for (Plot plot : this.claimController.getPlots()) {
            plot.setColony(null);
        }
    
        Map<UUID, MailMessage> mailMessages = new HashMap<>();
        double colonyBalance = economyController.getBalance();
        for (Resident resident : this.userController.getUsers()) {
            mailMessages.put(resident.getUniqueId(), new MailMessage(name + " disbanded", "The town " + name + " was disbanded by the baron.", "Please refer to other lines for your information regarding this action."));
            double percentage = economyController.getContributionTotal(resident.getUniqueId()) / colonyBalance;
            double total = colonyBalance * percentage;
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(resident.getUniqueId());
            EconomyResponse response = TitanTerritories.getInstance().getVaultEconomy().depositPlayer(offlinePlayer, total);
        
            if (response.transactionSuccess()) {
                mailMessages.get(resident.getUniqueId()).addLine("&aYou were given $" + total + " because of your monetary contribution to the town.");
            } else {
                mailMessages.get(resident.getUniqueId()).addLine("&cThere was an error while processing your monetary contribution " + response.errorMessage);
            }
        }
    
        for (Invite invite : this.inviteController.getInvites()) {
            Member target = plugin.getMemberManager().getMember(invite.getInvited());
            target.addMail(new Mail(mailSender, target.getUniqueId(), new MailMessage(name + " disbanded", "You were invited to the kingdom " + name, "However it has been disbanded by the monarch")));
        }
    
        for (Resident resident : userController.getUsers()) {
            resident.getMember().addMail(new Mail(mailSender, resident.getUniqueId(), mailMessages.get(resident.getUniqueId())));
        }
    }
    
    public void createChatroom() {
        this.chatroom = new TownRoom(this);
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
    
    public void setSpawnpoint(Location spawnpoint) {
        this.spawnpoint = spawnpoint;
    }
    
    public void sendMemberMessage(String message) {
        List<Resident> citizens = this.userController.getUsers();
        for (Resident resident : citizens) {
            resident.sendMessage(message);
        }
    }
    
    public UUID getUniqueId() {
        return uuid;
    }
    
    public Leader<?> getLeader() {
        return baron;
    }
    
    public ClaimResponse claim(IUser user, Plot plot) {
        if (!plot.hasKingdom()) {
            if (getKingdom() != null) {
                return new ClaimResponse(this, user, "plot not claimed by your kingdom", false);
            }
        } else {
            if (getKingdom() != null) {
                if (!plot.getKingdom().equals(getKingdom())) {
                    return new ClaimResponse(this, user, "plot is claimed by another kingdom", false);
                }
            }
        }
        
        if (plot.hasTown()) {
            if (!plot.getTown().equals(this)) {
                return new ClaimResponse(this, user, "plot is claimed by another town", false);
            } else {
                return new ClaimResponse(this, user, "plot is already claimed by your town", false);
            }
        }
        
        if (plot.hasColony()) {
            return new ClaimResponse(this, user, "plot is claimed by a colony", false);
        }
    
        boolean bordersClaim = false;
        List<Chunk> facingChunks = Utils.getFacingChunks(user.getLocation().getChunk());
        for (Chunk chunk : facingChunks) {
            Plot cPlot = plugin.getPlotManager().getPlot(chunk.getBlock(8, 128, 8).getLocation());
            if (cPlot.hasTown()) {
                if (cPlot.getTown().equals(this)) {
                    bordersClaim = true;
                }
            }
        }
    
        if (!bordersClaim) {
            return new ClaimResponse(this, user, "&cplot must border existing town claim", false);
        }
        return new ClaimResponse(this, user, "", true);
    }
    
    public Class<? extends IUser> getUserClass() {
        return Resident.class;
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
    
    public InviteController<Town> getInviteController() {
        return inviteController;
    }
    
    public FlagController getFlagController() {
        return flagController;
    }
    
    public MailController getMailController() {
        return mailController;
    }
    
    public WarpController getWarpController() {
        return warpController;
    }
    
    public EconomyController getEconomyController() {
        return economyController;
    }
    
    public void setLeader(IUser user) {
        Resident resident = this.userController.get(user.getUniqueId());
        this.baron = new Baron(resident.getMember(), resident.getJoinDate(), getUniqueId());
    }
    
    public void setUniqueId(UUID uuid) {
        this.uuid = uuid;
    }
    
    public int hashCode() {
        return Objects.hash(uuid);
    }
    
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Town town = (Town) o;
        return uuid.equals(town.uuid);
    }
    
    public Baron getBaron() {
        return baron;
    }
    
    public void setBaron(Baron baron) {
        this.baron = baron;
    }
    
    public Kingdom getKingdom() {
        if (kingdomUUID == null) {
            return null;
        }
        return TitanTerritories.getInstance().getKingdomManager().getKingdom(kingdomUUID);
    }
    
    public boolean hasKingdom() {
        return kingdomUUID != null;
    }
    
    public void setKingdom(Kingdom kingdom) {
        this.kingdomUUID = kingdom.getUniqueId();
    }
    
    public IChatroom getChatroom() {
        return chatroom;
    }
}