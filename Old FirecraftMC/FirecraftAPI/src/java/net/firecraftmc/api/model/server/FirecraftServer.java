package net.firecraftmc.api.model.server;

import net.firecraftmc.api.enums.ServerType;
import org.bukkit.ChatColor;

import java.io.Serializable;

public class FirecraftServer implements Serializable {
    private static final long serialVersionUID = 2L;

    private String id;
    private final String name;
    private final ChatColor color;
    private String ip;
    private ServerType type;

    public FirecraftServer(String name, ChatColor color) {
        this.name = name;
        this.color = color;
    }

    public FirecraftServer(String id, String name, ChatColor color, String ip, ServerType type) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.ip = ip;
        this.type = type;
    }

    public FirecraftServer(String id, String name, ChatColor color, ServerType type) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public ChatColor getColor() {
        return color;
    }

    public String toString() {
        return color + name;
    }

    public String getId() {
        return id;
    }

    public String getIp() {
        return ip;
    }

    public ServerType getType() {
        return type;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
    
    @SuppressWarnings("SameReturnValue")
    public boolean isOnline() {
        return true;
    }
}
