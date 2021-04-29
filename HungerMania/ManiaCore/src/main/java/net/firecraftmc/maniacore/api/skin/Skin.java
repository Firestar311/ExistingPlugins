package net.firecraftmc.maniacore.api.skin;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import net.firecraftmc.maniacore.api.CenturionsCore;
import net.firecraftmc.maniacore.api.records.SkinRecord;
import net.firecraftmc.maniacore.api.util.CenturionsUtils;
import net.firecraftmc.manialib.data.model.IRecord;
import org.bukkit.Bukkit;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;

@Getter @Setter
public class Skin implements Serializable, IRecord {
    private static final String skinUrlString = "https://sessionserver.mojang.com/session/minecraft/profile/{uuid}?unsigned=false";
    private static final long serialVersionUID = 1L;
    
    private int id;
    private UUID uuid;
    private String name;
    private String signature;
    private String value;

    public Skin(){}

    public Skin(UUID uuid) {
        this.uuid = uuid;
        updateValues();
    }
    
    public void updateValues() {
        String profileURL = skinUrlString.replace("{uuid}", uuid.toString().replace("-", ""));

        try {
            JsonObject json = CenturionsUtils.getJsonObject(profileURL);
            JsonArray properties = (JsonArray) json.get("properties");

            JsonObject property = (JsonObject) properties.get(0);
            String newName = property.get("name").getAsString();
            boolean updated = false;
            if (this.name == null || (this.name != null && !this.name.equals(newName))) {
                this.name = newName;
                updated = true;
            }
            String newValue = property.get("value").getAsString();
            if (this.value == null || (this.value != null && !this.value.equals(newValue))) {
                this.value = newValue;
                updated = true;
            }
            String newSignature = property.get("signature").getAsString();
            if (this.signature == null || (this.signature != null && !this.signature.equals(newSignature))) {
                this.signature = newSignature;
                updated = true;
            }
            
            if (updated) {
                if (this.name != null && this.value != null && this.signature != null) {
                    new SkinRecord(this).push(CenturionsCore.getInstance().getDatabase());
                }
            }
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
    
    public Skin(int id, UUID uuid, String name, String signature, String value) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.signature = signature;
        this.value = value;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Skin skin = (Skin) o;
        return Objects.equals(uuid, skin.uuid);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
    
    public String toString() {
        return "name:" + name + " value:" + value + " signature:" + signature;
    }
}