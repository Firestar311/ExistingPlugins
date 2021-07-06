package com.stardevmc.titanterritories.core.objects.holder;

import com.firestar311.lib.util.Utils;
import com.stardevmc.chat.TitanChat;
import com.stardevmc.chat.api.DefaultRoles;
import com.stardevmc.chat.api.IChatroom;
import com.stardevmc.titanterritories.core.TitanTerritories;
import com.stardevmc.titanterritories.core.chat.KingdomRoom;
import com.stardevmc.titanterritories.core.controller.*;
import com.stardevmc.titanterritories.core.leader.*;
import com.stardevmc.titanterritories.core.objects.interfaces.IHolder;
import com.stardevmc.titanterritories.core.objects.interfaces.IUser;
import com.stardevmc.titanterritories.core.objects.kingdom.*;
import com.stardevmc.titanterritories.core.objects.kingdom.Mail.MailMessage;
import com.stardevmc.titanterritories.core.objects.member.Citizen;
import com.stardevmc.titanterritories.core.objects.member.Member;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.*;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.*;

public class Kingdom implements ConfigurationSerializable, IHolder {
    private AnnouncementController<Kingdom> announcementController; //All announcements for the kingdom, will be done on interval
    private Town capitol; //Capitol of the kingdom, this will also contain the spawn point
    private ClaimController<Kingdom> claimController; //The claims of all of the kingdom including colonies
    private ColonyHandler colonyHandler; //All colonies of the kingdom, or land not connecting to the main claim
    private String description; //The description of the kingdom
    private EconomyController<Kingdom> economyController; //All transactions of the kingdom, this will determine the balance of the kingdom as well
    private ElectionController<Kingdom> electionController;
    private ExperienceController<Kingdom> experienceController; //Kingdom experience, this will control certain things about the kingdom
    private FlagController<Kingdom> flagController; //All flags for the kingdom
    private InviteController<Kingdom> inviteController; //All pending invites to the Kingdom
    private MailController<Kingdom> mailController; //All messages within the kingdom, this can be between members or towns or from higher up to lower
    private Monarch monarch; //Leader of the kingdom
    private String motd; //The MOTD of the kingdom, should change this instead of description to communicate
    private String name; //Kingdom name
    private TitanTerritories plugin = TitanTerritories.getInstance();
    private RankController<Kingdom> rankController; //All ranks of the kingdom, these also control permissions of members
    private ShopController<Kingdom> shopController;
    private Location spawnpoint; //Fallback spawnpoint
    private TownHandler townHandler; //All towns of the kingdom
    private UUID uniqueId; //Auto-assigned by the plugin, this is for internal use and will be used to identify kingdoms because names can be changed
    private UserController<Kingdom, Citizen> userController; //All kingdom members
    private WarpController<Kingdom> warpController;
    private IChatroom chatroom;
    
    public Kingdom(Monarch monarch, Location spawnpoint, String name) {
        this();
        this.monarch = monarch;
        if (monarch instanceof PlayerMonarch) {
            this.userController.add(((PlayerMonarch) monarch).getCitizen());
            System.out.println("Added the monarch to the User Controller");
        }
        this.spawnpoint = spawnpoint;
        this.name = name;
    }
    
    private Kingdom() {
        this.initControllers();
        try {
            this.chatroom = new KingdomRoom(this);
        } catch (Exception e) {}
    }
    
    private void initControllers() {
        if (this.userController == null) { this.userController = new UserController<>(this); }
        if (this.claimController == null) { this.claimController = new ClaimController<>(this); }
        if (this.economyController == null) { this.economyController = new EconomyController<>(this); }
        if (this.colonyHandler == null) { this.colonyHandler = new ColonyHandler(this); }
        if (this.flagController == null) { this.flagController = new FlagController<>(this); }
        if (this.townHandler == null) { this.townHandler = new TownHandler(this); }
        if (this.rankController == null) { this.rankController = new RankController<>(this); }
        if (this.announcementController == null) { this.announcementController = new AnnouncementController<>(this); }
        if (this.experienceController == null) { this.experienceController = new ExperienceController<>(this); }
        if (this.mailController == null) { this.mailController = new MailController<>(this); }
        if (this.inviteController == null) { this.inviteController = new InviteController<>(this); }
        if (this.warpController == null) { this.warpController = new WarpController<>(this); }
        if (this.electionController == null) { this.electionController = new ElectionController<>(this); }
        if (this.shopController == null) { this.shopController = new ShopController<>(this); }
    }
    
    public Kingdom(Member monarch, Location spawnpoint, String name, UUID uuid) {
        this();
        this.monarch = new PlayerMonarch(monarch, System.currentTimeMillis(), uuid, getRankController().getLeaderRank().getName());
        this.uniqueId = uuid;
        this.spawnpoint = spawnpoint;
        this.name = name;
        if (this.monarch instanceof PlayerMonarch) {
            this.userController.add(((PlayerMonarch) this.monarch).getCitizen());
            System.out.println("Added the monarch to the User Controller");
        }
        createChatroom();
    }
    
    public Kingdom(Map<String, Object> serialized) {
        this();
        this.monarch = (Monarch) serialized.get("monarch");
        this.spawnpoint = (Location) serialized.get("spawnpoint");
        this.name = (String) serialized.get("name");
        
        if (serialized.containsKey("capitol")) {
            this.capitol = (Town) serialized.get("capitol");
        }
        
        
        if (serialized.containsKey("colonyAmount")) {
            int chunkAmount = (int) serialized.get("colonyAmount");
            for (int i = 0; i < chunkAmount; i++) {
                Colony colony = (Colony) serialized.get("colony" + i);
                colony.setKingdom(this);
                this.colonyHandler.addColony(colony);
            }
        }
        
        
        if (serialized.containsKey("townAmount")) {
            int chunkAmount = (int) serialized.get("townAmount");
            for (int i = 0; i < chunkAmount; i++) {
                Town town = (Town) serialized.get("town" + i);
                town.setKingdom(this);
                this.townHandler.addTown((Town) serialized.get("town" + i));
            }
        }
        
        this.flagController = (FlagController<Kingdom>) serialized.get("flags");
        this.economyController = (EconomyController<Kingdom>) serialized.get("economy");
        this.rankController = (RankController<Kingdom>) serialized.get("ranks");
        this.announcementController = (AnnouncementController<Kingdom>) serialized.get("announcements");
        this.experienceController = (ExperienceController<Kingdom>) serialized.get("experience");
        this.mailController = (MailController<Kingdom>) serialized.get("mail");
        this.inviteController = (InviteController<Kingdom>) serialized.get("invites");
        this.userController = (UserController<Kingdom, Citizen>) serialized.get("citizens");
        this.warpController = (WarpController<Kingdom>) serialized.get("warps");
        this.electionController = (ElectionController<Kingdom>) serialized.get("elections");
        this.initControllers();
        if (serialized.containsKey("uniqueId")) {
            this.uniqueId = UUID.fromString((String) serialized.get("uniqueId"));
        }
    
        this.flagController.setHolder(this);
        this.economyController.setHolder(this);
        this.electionController.setHolder(this);
        this.rankController.setHolder(this);
        this.announcementController.setHolder(this);
        this.experienceController.setHolder(this);
        this.mailController.setHolder(this);
        this.inviteController.setHolder(this);
        this.userController.setHolder(this);
        this.warpController.setHolder(this);
        
        if (this.monarch instanceof PlayerMonarch) {
            this.userController.add(((PlayerMonarch) monarch).getCitizen());
        }
        createChatroom();
    }
    
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("monarch", this.monarch);
        serialized.put("spawnpoint", this.spawnpoint);
        serialized.put("name", this.name);
        serialized.put("capitol", this.capitol);
        serialized.put("economy", this.economyController);
        if (!colonyHandler.getColonies().isEmpty()) {
            serialized.put("colonyAmount", colonyHandler.getColonies().size());
            for (int i = 0; i < colonyHandler.getColonies().size(); i++) {
                serialized.put("colony" + i, colonyHandler.getColonies().get(i));
            }
        }
        serialized.put("flags", this.flagController);
        if (!townHandler.getTowns().isEmpty()) {
            serialized.put("townAmount", townHandler.getTowns().size());
            for (int i = 0; i < townHandler.getTowns().size(); i++) {
                serialized.put("town" + i, townHandler.getTowns().get(i));
            }
        }
        serialized.put("citizens", this.userController);
        serialized.put("ranks", this.rankController);
        serialized.put("announcements", this.announcementController);
        serialized.put("experience", this.experienceController);
        serialized.put("mail", this.mailController);
        serialized.put("invites", this.inviteController);
        serialized.put("uniqueId", uniqueId.toString());
        serialized.put("warps", this.warpController);
        serialized.put("elections", this.electionController);
        createChatroom();
        return serialized;
    }
    
    public void disband() {
        UUID mailSender;
        if (this.monarch instanceof PlayerMonarch) {
            mailSender = ((PlayerMonarch) monarch).getCitizen().getUniqueId();
        } else {
            mailSender = plugin.getPlayerManager().getServerUser().getUniqueId();
        }
        
        Map<UUID, MailMessage> mailMessages = new HashMap<>();
        for (Citizen citizen : this.userController.getUsers()) {
            mailMessages.put(citizen.getUniqueId(), new MailMessage(name + " disbanded", "The kingdom " + name + " was disbanded by the monarch.", "Please refer to other lines for your information regarding this action."));
        }
        for (Plot plot : this.claimController.getPlots()) {
            plot.setKingdom(null);
        }
        
        double kingdomBalance = economyController.getBalance();
        for (Citizen citizen : this.userController.getUsers()) {
            double percentage = economyController.getContributionTotal(citizen.getUniqueId()) / kingdomBalance;
            double total = kingdomBalance * percentage;
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(citizen.getUniqueId());
            EconomyResponse response = TitanTerritories.getInstance().getVaultEconomy().depositPlayer(offlinePlayer, total);
            
            if (response.transactionSuccess()) {
                mailMessages.get(citizen.getUniqueId()).addLine("&aYou were given $" + total + " because of your monetary contribution to the kingdom.");
            } else {
                mailMessages.get(citizen.getUniqueId()).addLine("&cThere was an error while processing your monetary contribution " + response.errorMessage);
            }
        }
        
        for (Town town : this.townHandler.getTowns()) {
            town.setKingdom(null);
            mailMessages.get(town.getBaron().getUser().getUniqueId()).addLine("&aYour Town has become standalone");
        }
        
        for (Colony colony : this.colonyHandler.getColonies()) {
            colony.disband();
        }
        
        for (Invite invite : this.inviteController.getInvites()) {
            Member target = plugin.getMemberManager().getMember(invite.getInvited());
            target.addMail(new Mail(mailSender, target.getUniqueId(), new MailMessage(name + " disbanded", "You were invited to the kingdom " + name, "However it has been disbanded by the monarch")));
        }
        
        for (Citizen citizen : userController.getUsers()) {
            citizen.getMember().addMail(new Mail(mailSender, citizen.getUniqueId(), mailMessages.get(citizen.getUniqueId())));
        }
    }
    
    public void createChatroom() {
        this.chatroom = new KingdomRoom(this);
        TitanChat.getInstance().getChatroomManager().registerChatroom(chatroom);
        this.chatroom.addMember(this.monarch.getUser().getUniqueId(), DefaultRoles.OWNER);
        this.userController.getUsers().forEach(citizen -> this.chatroom.addMember(citizen.getUniqueId(), DefaultRoles.MEMBER));
    }
    
    public String getName() {
        return name;
    }
    
    public Location getSpawnpoint() {
        return spawnpoint;
    }
    
    public ElectionController<Kingdom> getElectionController() {
        return electionController;
    }
    
    public ShopController<Kingdom> getShopController() {
        return shopController;
    }
    
    public void setSpawnpoint(Location spawnpoint) {
        this.spawnpoint = spawnpoint;
    }
    
    public void sendMemberMessage(String message) {
        String format = KingdomRoom.BASE_FORMAT.replace("{changeable}", "").replace("{message}", message);
        getChatroom().sendChatMessage(format);
    }
    
    public UUID getUniqueId() {
        return uniqueId;
    }
    
    public void setUniqueId(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }
    
    public Leader<?> getLeader() {
        return monarch;
    }
    
    public ClaimResponse claim(IUser user, Plot plot) {
        if (plot.getKingdom() != null) {
            if (plot.hasKingdom()) {
                return new ClaimResponse(this, user, "&cplot is already claimed", false);
            } else {
                return new ClaimResponse(this, user, "&cplot is claimed by another kingdom", false);
            }
        }
        
        boolean bordersClaim = false;
        List<Chunk> facingChunks = Utils.getFacingChunks(user.getLocation().getChunk());
        for (Chunk chunk : facingChunks) {
            Plot cPlot = plugin.getPlotManager().getPlot(chunk.getBlock(8, 1, 8).getLocation());
            if (cPlot.hasKingdom()) {
                if (cPlot.getKingdom().equals(this)) {
                    bordersClaim = true;
                }
            }
        }
        
        if (!bordersClaim) {
            return new ClaimResponse(this, user, "&cplot must border existing kingdom claim", false);
        }
        return new ClaimResponse(this, user, "", true);
    }
    
    public Class<? extends IUser> getUserClass() {
        return Citizen.class;
    }
    
    public ClaimController getClaimController() {
        return claimController;
    }
    
    public UserController<Kingdom, Citizen> getUserController() {
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
    
    public MailController getMailController() {
        return mailController;
    }
    
    public WarpController<Kingdom> getWarpController() {
        return this.warpController;
    }
    
    public EconomyController getEconomyController() {
        return economyController;
    }
    
    public void setLeader(IUser user) {
        if (user != null) {
            Citizen citizen = this.userController.get(user.getUniqueId());
            this.monarch = new PlayerMonarch(citizen.getMember(), citizen.getJoinDate(), this.uniqueId, getRankController().getLeaderRank().getName());
            if (monarch instanceof PlayerMonarch) {
                this.userController.add(((PlayerMonarch) monarch).getCitizen());
                System.out.println("Added the monarch to the User Controller");
            }
        } else {
            this.monarch = null;
        }
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int hashCode() {
        return Objects.hash(uniqueId);
    }
    
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Kingdom kingdom = (Kingdom) o;
        return uniqueId.equals(kingdom.uniqueId);
    }
    
    public boolean isMonarch(Player player) {
        return isMonarch(player.getUniqueId());
    }
    
    public boolean isMonarch(UUID uuid) {
        if (monarch instanceof PlayerMonarch) {
            return ((PlayerMonarch) monarch).getObject().getUniqueId().equals(uuid);
        }
        return false;
    }
    
    public Monarch getMonarch() {
        return monarch;
    }
    
    public void setMonarch(Monarch monarch) {
        this.monarch = monarch;
    }
    
    public Town getCapitol() {
        return capitol;
    }
    
    public void setCapitol(Town capitol) {
        this.capitol = capitol;
        this.townHandler.removeTown(capitol);
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getMotd() {
        return motd;
    }
    
    public void setMotd(String motd) {
        this.motd = motd;
    }
    
    public ColonyHandler getColonyHandler() {
        return colonyHandler;
    }
    
    public TownHandler getTownHandler() {
        return townHandler;
    }
    
    public AnnouncementController getAnnouncementController() {
        return announcementController;
    }
    
    public ExperienceController getExperienceController() {
        return experienceController;
    }
    
    public IChatroom getChatroom() {
        return chatroom;
    }
}