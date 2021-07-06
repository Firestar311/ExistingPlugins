package com.stardevmc.titanterritories.core.manager;

import com.firestar311.lib.config.ConfigManager;
import com.firestar311.lib.player.User;
import com.stardevmc.titanterritories.core.TitanTerritories;
import com.stardevmc.titanterritories.core.objects.member.Member;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;

public class MemberManager {
    
    private Set<Member> members = new HashSet<>();
    
    private ConfigManager configManager;
    
    public MemberManager() {
        this.configManager = new ConfigManager(TitanTerritories.getInstance(), "members");
        this.configManager.setup();
    }
    
    public void loadData() {
        FileConfiguration config = configManager.getConfig();
        if (config.contains("members")) {
            for (String c : config.getConfigurationSection("members").getKeys(false)) {
                Member member = (Member) config.get("members." + c);
                this.members.add(member);
            }
        }
    }
    
    public void saveData() {
        FileConfiguration config = configManager.getConfig();
        List<Member> members = new ArrayList<>(this.members);
        if (!members.isEmpty()) {
            for (int i = 0; i < members.size(); i++) {
                config.set("members.member" + i, members.get(i));
            }
        }
        configManager.saveConfig();
    }
    
    public Member getMember(UUID uuid) {
        for (Member member : members) {
            if (member.getUniqueId().equals(uuid)) {
                return member;
            }
        }
        Member member = new Member(TitanTerritories.getInstance().getPlayerManager().getUser(uuid));
        this.members.add(member);
        return member;
    }
    
    public List<Member> getMembers() {
        return new ArrayList<>(members);
    }
    
    public void addMember(Member member) {
        this.members.add(member);
    }
    
    public Member getMember(String name) {
        User info = TitanTerritories.getInstance().getPlayerManager().getUser(name);
        if (info == null) return null;
        return getMember(info.getUniqueId());
    }
}