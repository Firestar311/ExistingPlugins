package com.stardevmc.titanterritories.core.objects.kingdom;

import com.firestar311.lib.pagination.IElement;
import com.stardevmc.titanterritories.core.TitanTerritories;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.text.SimpleDateFormat;
import java.util.*;

public class Visit implements IElement, ConfigurationSerializable {
    private UUID uuid;
    private long date;
    
    public Visit(UUID uuid, long date) {
        this.uuid = uuid;
        this.date = date;
    }
    
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("uuid", uuid.toString());
        serialized.put("date", date + "");
        return serialized;
    }
    
    public static Visit deserialize(Map<String, Object> serialized) {
        UUID uuid = UUID.fromString((String) serialized.get("uuid"));
        long date = Long.parseLong((String) serialized.get("date"));
        return new Visit(uuid, date);
    }
    
    public UUID getUuid() {
        return uuid;
    }
    
    public long getDate() {
        return date;
    }
    
    public String formatLine(String... args) {
        String name = TitanTerritories.getInstance().getMemberManager().getMember(uuid).getName();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a z");
        String formattedDate = dateFormat.format(new Date(date));
        return " &8- &7" + name + " visited on " + formattedDate;
    }
}