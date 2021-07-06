package com.kingrealms.realms.chat.groups;

import com.kingrealms.realms.chat.NameFormat;
import com.kingrealms.realms.chat.prefixes.Prefix;

public class SpecialGroup extends Group {
    
    public SpecialGroup(String name) {
        super(name);
        getPrefix().setBold(true);
    }
    
    public SpecialGroup(int id, String name, String permissionGroup, Prefix prefix, NameFormat nameFormat) {
        super(id, name, permissionGroup, prefix, nameFormat);
        getPrefix().setBold(true);
    }
    
    public SpecialGroup(String name, String permissionGroup, Prefix prefix, NameFormat nameFormat) {
        super(name, permissionGroup, prefix, nameFormat);
        getPrefix().setBold(true);
    }
    
    public SpecialGroup(String name, Prefix prefix, NameFormat nameFormat) {
        super(name, prefix, nameFormat);
        getPrefix().setBold(true);
    }
    
    public SpecialGroup(String name, Prefix prefix) {
        super(name, prefix);
        getPrefix().setBold(true);
    }
}