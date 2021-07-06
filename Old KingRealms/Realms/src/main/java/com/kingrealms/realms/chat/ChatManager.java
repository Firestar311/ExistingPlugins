package com.kingrealms.realms.chat;

import com.kingrealms.realms.chat.groups.*;
import com.kingrealms.realms.chat.prefixes.Prefix;
import com.kingrealms.realms.chat.suffixes.Suffix;
import com.starmediadev.lib.collection.IncrementalMap;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class ChatManager {
    private IncrementalMap<Group> groups = new IncrementalMap<>();
    private Map<String, Prefix> prefixes = new HashMap<>();
    private Map<String, Suffix> suffixes = new HashMap<>();
    
    public ChatManager() {
    }
    
    public void generateDefaultPrefixes() {
        addPrefix(new Prefix("default", "", "#ffffff", "realms.prefix.default", false));
        addPrefix(new Prefix("soldier", "SOLDIER", "#d4680d", "realms.prefix.soldier", true));
        addPrefix(new Prefix("knight", "KNIGHT", "#f89e81", "realms.prefix.knight", true));
        addPrefix(new Prefix("marshall", "MARSHALL", "#29ed64", "realms.prefix.marshall", true));
        addPrefix(new Prefix("god", "GOD", "#4287f5", "realms.prefix.god", true));
        addPrefix(new Prefix("beta", "BETA", "#00cf00", "realms.prefix.beta", true));
        addPrefix(new Prefix("vip", "VIP", "#ffff00", "realms.prefix.vip", true));
        addPrefix(new Prefix("builder", "BUILDER", "#00c7a2", "realms.prefix.builder", true));
        addPrefix(new Prefix("helper", "HELPER", "#a84d6f", "realms.prefix.helper", true));
        addPrefix(new Prefix("moderator", "MOD", "#8a00cf", "realms.prefix.moderator", true));
        addPrefix(new Prefix("seniormoderator", "SR MOD", "#bf00bf", "realms.prefix.seniormoderator", true));
        addPrefix(new Prefix("trialadmin", "TRIAL ADMIN", "#ed2d2d", "realms.prefix.trialadmin", true));
        addPrefix(new Prefix("admin", "ADMIN", "#ff0000", "realms.prefix.admin", true));
        addPrefix(new Prefix("headadmin", "HEAD ADMIN", "#c20000", "realms.prefix.headadmin", true));
        addPrefix(new Prefix("manager", "MANAGER", "#ffaa00", "realms.prefix.manager", true));
        addPrefix(new Prefix("owner", "OWNER", "#ffaa00", "realms.prefix.owner", true));
    }
    
    public void generateDefaultGroups() {
        addGroup(new RegularGroup("default", getPrefix("default")));
        addGroup(new RegularGroup("soldier", getPrefix("soldier")));
        addGroup(new RegularGroup("knight", getPrefix("knight")));
        addGroup(new RegularGroup("marshall", getPrefix("marshall")));
        addGroup(new RegularGroup("god", getPrefix("god")));
        addGroup(new SpecialGroup("beta", getPrefix("beta")));
        addGroup(new SpecialGroup("vip", getPrefix("vip")));
        addGroup(new StaffGroup("builder", getPrefix("builder")));
        addGroup(new StaffGroup("helper", getPrefix("helper")));
        addGroup(new StaffGroup("moderator", getPrefix("moderator")));
        addGroup(new StaffGroup("seniormoderator", getPrefix("seniormoderator")));
        addGroup(new StaffGroup("trialadmin", getPrefix("trialadmin")));
        addGroup(new StaffGroup("admin", getPrefix("admin")));
        addGroup(new StaffGroup("headadmin", getPrefix("headadmin")));
        addGroup(new StaffGroup("manager", getPrefix("manager")));
        addGroup(new StaffGroup("owner", getPrefix("owner")));
    }
    
    public Prefix getPrefix(String id) {
        return this.prefixes.get(id);
    }
    
    public void addGroup(Group group) {
        int pos = groups.add(group);
        group.setId(pos);
    }
    
    public void addPrefix(Prefix prefix) {
        prefixes.put(prefix.getId(), prefix);
    }
    
    public void loadData() {
        generateDefaultPrefixes();
        generateDefaultGroups();
    }
    
    public void saveData() {
        //TODO
    }
    
    public Group getGroup(String g) {
        for (Group group : this.groups.values()) {
            if (group.getName().equalsIgnoreCase(g) || group.getPermissionGroup().equalsIgnoreCase(g)) {
                return group;
            }
        }
        
        return null;
    }
}