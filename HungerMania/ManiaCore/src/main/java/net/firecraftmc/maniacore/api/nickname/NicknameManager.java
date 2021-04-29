package net.firecraftmc.maniacore.api.nickname;

import net.firecraftmc.maniacore.api.CenturionsCore;
import net.firecraftmc.maniacore.api.ranks.Rank;
import net.firecraftmc.maniacore.api.user.User;

import java.util.*;

public class NicknameManager {
    
    private Map<UUID, Nickname> nicknames = new HashMap<>();
    
    private Set<String> blacklistedNames = new HashSet<>();
    
    public void addNameToBlacklist(String name) {
        this.blacklistedNames.add(name);
    }
    
    public boolean isBlacklisted(String name) {
        try {
            User user = CenturionsCore.getInstance().getUserManager().getUser(name);
            if (user != null) {
                if (user.getRank().ordinal() <= Rank.MEDIA.ordinal()) {
                    return true;
                }
            }
        } catch (Exception e) { }
        

        for (String blname : this.blacklistedNames) {
            if (name.equalsIgnoreCase(blname)) {
                return true;
            }
        }
        
        return false;
    }

    public Set<String> getBlacklistedNames() {
        return blacklistedNames;
    }
}
