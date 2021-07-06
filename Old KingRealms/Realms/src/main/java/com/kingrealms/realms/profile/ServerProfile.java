package com.kingrealms.realms.profile;

import com.kingrealms.realms.Realms;
import com.starmediadev.lib.user.ServerUser;
import com.starmediadev.lib.util.Utils;
import org.bukkit.Bukkit;

public class ServerProfile extends RealmProfile {
    
    public ServerProfile() {
        super(Realms.getInstance().getUserManager().getServerUser());
    }
    
    public ServerUser getServerUser() {
        return ((ServerUser) this.getUser());
    }
    
    @Override
    public String getName() {
        return "Console";
    }
    
    @Override
    public boolean isOnline() {
        return true;
    }
    
    @Override
    public boolean hasPermission(String permission) {
        return true;
    }
    
    @Override
    public void sendMessage(String message) {
        Bukkit.getConsoleSender().sendMessage(Utils.color(replaceColorVariables(message)));
    }
}