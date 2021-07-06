package com.starmediadev.lib.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;

import java.io.Serializable;
import java.util.UUID;
import java.util.logging.Level;

public class Skin implements Serializable {
    private static final String skinUrlString = "https://sessionserver.mojang.com/session/minecraft/profile/{uuid}?unsigned=false";
    private static final long serialVersionUID = 1L;

    private UUID uuid;
    private String name;
    private String signature;
    private String value;

    public Skin(){}

    public Skin(UUID uuid) {
        this.uuid = uuid;
        String profileURL = skinUrlString.replace("{uuid}", uuid.toString().replace("-", ""));

        try {
            JsonObject json = Utils.getJsonObject(profileURL);
            JsonArray properties = (JsonArray) json.get("properties");
    
            JsonObject property = (JsonObject) properties.get(0);
            name = property.get("name").getAsString();
            value = property.get("value").getAsString();
            signature = property.get("signature").getAsString();
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Invalid name/UUID provided, using default skin");
        }
    }

    public Skin(UUID uuid, String name, String signature, String value) {
        this.uuid = uuid;
        this.name = name;
        this.signature = signature;
        this.value = value;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getSignature() {
        return signature;
    }

    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return "name:" + name + " value:" + value + " signature:" + signature;
    }
}