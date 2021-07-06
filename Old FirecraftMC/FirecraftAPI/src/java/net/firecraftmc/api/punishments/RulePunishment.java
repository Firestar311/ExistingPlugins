package net.firecraftmc.api.punishments;

import net.firecraftmc.api.punishments.Punishment.Type;
import net.firecraftmc.api.util.Utils;
import org.bukkit.ChatColor;

public class RulePunishment {
    
    private final int offenseNumber;
    private final Punishment.Type type;
    private final long length;
    
    public RulePunishment(int oN, Type type, long length) {
        this.offenseNumber = oN;
        this.type = type;
        this.length = length;
    }
    
    public Type getType() {
        return type;
    }
    
    public long getLength() {
        return length;
    }
    
    public int getOffenseNumber() {
        return offenseNumber;
    }
    
    public String toString() {
        return ChatColor.BLUE + "" + type.toString().replace("_", " ") + " " + Utils.Time.formatTime(length);
    }
}
