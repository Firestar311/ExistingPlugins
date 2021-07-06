package com.kingrealms.realms.chat.groups;

import com.kingrealms.realms.chat.NameFormat;
import com.kingrealms.realms.chat.prefixes.Prefix;

public class RegularGroup extends Group {
    
    public RegularGroup(String name) {
        super(name);
        getPrefix().setBold(true);
    }
    
    public RegularGroup(int id, String name, String permissionGroup, Prefix prefix, NameFormat nameFormat) {
        super(id, name, permissionGroup, prefix, nameFormat);
    }
    
    public RegularGroup(String name, String permissionGroup, Prefix prefix, NameFormat nameFormat) {
        super(name, permissionGroup, prefix, nameFormat);
    }
    
    public RegularGroup(String name, Prefix prefix, NameFormat nameFormat) {
        super(name, prefix, nameFormat);
    }
    
    public RegularGroup(String name, Prefix prefix) {
        super(name, prefix);
    }
}