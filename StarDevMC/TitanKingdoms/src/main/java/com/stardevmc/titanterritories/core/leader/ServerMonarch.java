package com.stardevmc.titanterritories.core.leader;

import com.firestar311.lib.util.Utils;
import com.stardevmc.titanterritories.core.objects.interfaces.IUser;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;

import java.util.*;

public class ServerMonarch extends Monarch<ConsoleCommandSender> {
    
    public ServerMonarch(ConsoleCommandSender object, long joinDate, UUID kingdom) {
        super(object, joinDate, kingdom, "");
    }
    
    public static ServerMonarch deserialize(Map<String, Object> serialized) {
        ConsoleCommandSender object = Bukkit.getConsoleSender();
        long joinDate = Long.parseLong((String) serialized.get("joinDate"));
        UUID kingdom = UUID.fromString((String) serialized.get("kingdom"));
        return new ServerMonarch(object, joinDate, kingdom);
    }
    
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("joinDate", this.joinDate + "");
        serialized.put("kingdom", this.kingdomUniqueId.toString());
        return serialized;
    }
    
    public IUser getUser() {
        return null;
    }
    
    public String getName() {
        return "SERVER";
    }
    
    public void sendMessage(String message) {
        getObject().sendMessage(Utils.color(message));
    }
}