package com.stardevmc.titanterritories.core.objects.member;

import com.firestar311.lib.player.User;
import com.stardevmc.titanterritories.core.TitanTerritories;
import com.stardevmc.titanterritories.core.objects.enums.Permission;
import com.stardevmc.titanterritories.core.objects.interfaces.IHolder;
import com.stardevmc.titanterritories.core.objects.interfaces.IUser;
import com.stardevmc.titanterritories.core.objects.kingdom.*;
import com.stardevmc.titanterritories.core.objects.kingdom.ExperienceAction.Type;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.*;

public class Member implements ConfigurationSerializable, IUser {
    
    private List<ExperienceAction> experience;
    private User info;
    private long joinDate;
    private List<Mail> mailbox;
    
    public Member(User info) {
        this(info, new ArrayList<>(), new ArrayList<>());
    }
    
    public Member(User info, List<ExperienceAction> experience, List<Mail> mailbox) {
        this.info = info;
        this.experience = experience;
        this.mailbox = mailbox;
    }
    
    public static Member deserialize(Map<String, Object> serialized) {
        User info = TitanTerritories.getInstance().getPlayerManager().getUser(UUID.fromString((String) serialized.get("uuid")));
        
        List<ExperienceAction> experience = new ArrayList<>();
        if (serialized.containsKey("expActionSize")) {
            int size = Integer.parseInt((String) serialized.get("expActionSize"));
            for (int i = 0; i < size; i++) {
                experience.add((ExperienceAction) serialized.get("action" + i));
            }
        }
        
        List<Mail> mailbox = new ArrayList<>();
        if (serialized.containsKey("mailboxSize")) {
            int size = Integer.parseInt((String) serialized.get("mailboxSize"));
            for (int i = 0; i < size; i++) {
                mailbox.add((Mail) serialized.get("mail" + i));
            }
        }
        
        return new Member(info, experience, mailbox);
    }
    
    public void addExperience(double experience) {
        this.experience.add(new ExperienceAction(experience, Type.GAIN, TitanTerritories.getInstance().getMemberManager().getMember(this.info.getUniqueId())));
    }
    
    public void removeExperience(double experience) {
        this.experience.add(new ExperienceAction(experience, Type.LOSS, TitanTerritories.getInstance().getMemberManager().getMember(this.info.getUniqueId())));
    }
    
    public double getExperience() {
        double totalExp = 0;
        for (ExperienceAction action : this.experience) {
            if (action.getType().equals(Type.GAIN)) {
                totalExp += action.getAmount();
            } else if (action.getType().equals(Type.LOSS)) {
                totalExp -= action.getAmount();
            }
        }
        
        return totalExp;
    }
    
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("uuid", this.info.getUniqueId().toString());
        serialized.put("experience", this.experience + "");
        if (!mailbox.isEmpty()) {
            serialized.put("mailboxSize", this.mailbox.size() + "");
            for (int i = 0; i < mailbox.size(); i++) {
                serialized.put("mail" + i, mailbox.get(i));
            }
        }
        
        if (!experience.isEmpty()) {
            serialized.put("expActionSize", this.experience.size());
            for (int i = 0; i < experience.size(); i++) {
                serialized.put("action" + i, experience.get(i));
            }
        }
        return serialized;
    }
    
    public void addMail(Mail mail) {
        this.mailbox.add(mail);
    }
    
    public User getInfo() {
        return info;
    }
    
    public boolean isOnline() {
        return info.isOnline();
    }
    
    public boolean isLeader() {
        return false;
    }
    
    public String getName() {
        return info.getLastName();
    }
    
    public UUID getUniqueId() {
        return info.getUniqueId();
    }
    
    public void sendMessage(String message) {
        info.sendMessage(message);
    }
    
    public void sendMessage(BaseComponent... components) {
        info.sendMessage(components);
    }
    
    public boolean hasPermission(Permission permission) {
        return false;
    }
    
    public Player getPlayer() {
        return Bukkit.getPlayer(info.getUniqueId());
    }
    
    public Rank getRank() {
        return null;
    }
    
    public Member getMember() {
        return this;
    }
    
    public void setAcceptedInvite(Invite invite) {
    
    }
    
    public <T extends IHolder> void setHolder(T holder) {
    
    }
    
    public void setRank(Rank rank) {
    
    }
    
    public Location getLocation() {
        if (getPlayer() != null) {
            return getPlayer().getLocation();
        }
        return null;
    }
    
    public void teleport(Location location) {
        if (getPlayer() != null) {
            getPlayer().teleport(location);
        }
    }
    
    public long getJoinDate() {
        return joinDate;
    }
    
    public List<Mail> getMailbox() {
        return mailbox;
    }
    
    public void setJoinDate(long date) {
        this.joinDate = date;
    }
    
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Member member = (Member) o;
        return Objects.equals(info, member.info);
    }
    
    public int hashCode() {
        return Objects.hash(info);
    }
}