package com.kingrealms.realms.chat.groups;

import com.kingrealms.realms.chat.NameFormat;
import com.kingrealms.realms.chat.prefixes.Prefix;

public class StaffGroup extends Group {
    
    public StaffGroup(String name) {
        super(name);
        getPrefix().setBold(true);
        getNameFormat().setNameBold(true);
    }
    
    public StaffGroup(int id, String name, String permissionGroup, Prefix prefix, NameFormat nameFormat) {
        super(id, name, permissionGroup, prefix, nameFormat);
        getPrefix().setBold(true);
        getNameFormat().setNameBold(true);
    }
    
    public StaffGroup(String name, String permissionGroup, Prefix prefix, NameFormat nameFormat) {
        super(name, permissionGroup, prefix, nameFormat);
        getPrefix().setBold(true);
        getNameFormat().setNameBold(true);
    }
    
    public StaffGroup(String name, Prefix prefix, NameFormat nameFormat) {
        super(name, prefix, nameFormat);
        getPrefix().setBold(true);
        getNameFormat().setNameBold(true);
    }
    
    public StaffGroup(String name, Prefix prefix) {
        super(name, prefix);
        getPrefix().setBold(true);
        getNameFormat().setNameBold(true);
    }
}