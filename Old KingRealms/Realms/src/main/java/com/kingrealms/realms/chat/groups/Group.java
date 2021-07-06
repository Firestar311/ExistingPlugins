package com.kingrealms.realms.chat.groups;

import com.kingrealms.realms.chat.*;
import com.kingrealms.realms.chat.prefixes.Prefix;
import com.kingrealms.realms.chat.suffixes.Suffix;

public abstract class Group {
    protected int id;
    protected String name, permissionGroup = "";
    protected Prefix prefix = new Prefix();
    protected NameFormat nameFormat = new NameFormat("%prefix%%name%%suffix%", false);
    protected Suffix suffix = new Suffix();
    
    public Group(String name) {
        this.name = name;
    }
    
    public Group(int id, String name, String permissionGroup, Prefix prefix, NameFormat nameFormat) {
        this.id = id;
        this.name = name;
        this.permissionGroup = permissionGroup;
        this.prefix = prefix;
        this.nameFormat = nameFormat;
    }
    
    public Group(String name, String permissionGroup, Prefix prefix, NameFormat nameFormat) {
        this.name = name;
        this.permissionGroup = permissionGroup;
        this.prefix = prefix;
        this.nameFormat = nameFormat;
    }
    
    public Group(String name, Prefix prefix, NameFormat nameFormat) {
        this.name = name;
        this.prefix = prefix;
        this.nameFormat = nameFormat;
    }
    
    public Group(String name, Prefix prefix) {
        this.name = name;
        this.prefix = prefix;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public String getPermissionGroup() {
        if (permissionGroup == null) {
            permissionGroup = "";
        }
        return permissionGroup;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setPermissionGroup(String permissionGroup) {
        this.permissionGroup = permissionGroup;
    }
    
    public int getId() {
        return id;
    }
    
    public Prefix getPrefix() {
        return prefix;
    }
    
    public NameFormat getNameFormat() {
        return nameFormat;
    }
    
    public Suffix getSuffix() {
        return suffix;
    }
}