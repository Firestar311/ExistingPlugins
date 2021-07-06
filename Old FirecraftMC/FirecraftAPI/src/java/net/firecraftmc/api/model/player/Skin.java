package net.firecraftmc.api.model.player;

import org.bukkit.Bukkit;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.net.URL;
import java.util.UUID;
import java.util.logging.Level;

/**
 * A skin object that is retrieved from mojang
 */
public class Skin implements Serializable {
    private static final String skinUrlString = "https://sessionserver.mojang.com/session/minecraft/profile/{uuid}?unsigned=false";
    private static final long serialVersionUID = 1L;

    private UUID uuid;
    private String name;
    private String signature;
    private String value;

    public Skin(){}

    /**
     * Constructs a skin and gets the other information from Mojang
     * @param uuid The UUID of the skin
     */
    public Skin(UUID uuid) {
        this.uuid = uuid;
        String profileURL = skinUrlString.replace("{uuid}", uuid.toString().replace("-", ""));

        try {
            URL url = new URL(profileURL);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder buffer = new StringBuilder();
            int read;
            char[] chars = new char[256];
            while ((read = reader.read(chars)) != -1) {
                buffer.append(chars, 0, read);
            }

            JSONObject json = (JSONObject) new JSONParser().parse(buffer.toString());
            JSONArray properties = (JSONArray) json.get("properties");

            JSONObject property = (JSONObject) properties.get(0);
            name = (String) property.get("name");
            value = (String) property.get("value");
            signature = (String) property.get("signature");
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Invalid name/UUID provided, using default skin");
        }
    }

    /**
     * Creates a skin with pre-existing information
     * @param uuid The UUID of the skin
     * @param name The properties name
     * @param signature The skin signature
     * @param value The skin value
     */
    public Skin(UUID uuid, String name, String signature, String value) {
        this.uuid = uuid;
        this.name = name;
        this.signature = signature;
        this.value = value;
    }

    /**
     * @return The UUID of the skin
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * @return The signature of the skin
     */
    public String getSignature() {
        return signature;
    }

    /**
     * @return The value of the skin
     */
    public String getValue() {
        return value;
    }

    /**
     * @return The property name
     */
    public String getName() {
        return name;
    }

    public String toString() {
        return "name:" + name + " value:" + value + " signature:" + signature;
    }
}