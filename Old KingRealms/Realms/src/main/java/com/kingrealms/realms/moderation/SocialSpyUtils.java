package com.kingrealms.realms.moderation;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.profile.RealmProfile;
import org.bukkit.ChatColor;

public final class SocialSpyUtils {
    
    private static Realms plugin = Realms.getInstance();
    
    private SocialSpyUtils() {}
    
    public static void sendSocialSpyMessage(String message) {
        for (RealmProfile profile : plugin.getProfileManager().getProfiles()) {
            if (profile.isOnline()) {
                if (profile.getSocialSpy().isActive()) {
                    profile.sendMessage("&3&lSocialSpy &3" + ChatColor.stripColor(message));
                }
            }
        }
    }
}