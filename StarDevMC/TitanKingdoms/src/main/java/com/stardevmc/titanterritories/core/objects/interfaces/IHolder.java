package com.stardevmc.titanterritories.core.objects.interfaces;

import com.stardevmc.chat.api.IChatroom;
import com.stardevmc.titanterritories.core.controller.*;
import com.stardevmc.titanterritories.core.leader.Leader;
import com.stardevmc.titanterritories.core.objects.kingdom.ClaimResponse;
import com.stardevmc.titanterritories.core.objects.kingdom.Plot;
import org.bukkit.Location;

import java.util.UUID;

public interface IHolder {
    
    void disband();
    
    void createChatroom();
    
    String getName();
    Location getSpawnpoint();
    
    ElectionController getElectionController();
    
    ShopController getShopController();
    IChatroom getChatroom();
    
    void setName(String name);
    void setSpawnpoint(Location spawnpoint);
    void sendMemberMessage(String message);
    UUID getUniqueId();
    Leader<?> getLeader();
    ClaimResponse claim(IUser user, Plot plot);
    Class<? extends IUser> getUserClass();
    
    ClaimController getClaimController();
    UserController getUserController();
    RankController getRankController();
    InviteController getInviteController();
    FlagController getFlagController();
    MailController getMailController();
    WarpController getWarpController();
    EconomyController getEconomyController();
    
    void setLeader(IUser user);
}