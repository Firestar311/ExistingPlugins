package me.libraryaddict.disguise.utilities.mineskin;

import com.google.gson.annotations.SerializedName;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.apache.commons.lang.StringUtils;

import java.util.Map;
import java.util.UUID;

/**
 * Created by libraryaddict on 29/12/2019.
 */
public class MineSkinResponse {

    public String getIdStr() {
        return idStr;
    }

    public String getUuid() {
        return uuid;
    }

    public SkinVariant getVariant() {
        return variant;
    }

    public double getTimestamp() {
        return timestamp;
    }

    public int getDuration() {
        return duration;
    }

    public int getAccountId() {
        return accountId;
    }

    public String getServer() {
        return server;
    }

    public boolean isPrivateSkin() {
        return privateSkin;
    }

    public int getViews() {
        return views;
    }

    public boolean isDuplicate() {
        return duplicate;
    }

    public double getNextRequest() {
        return nextRequest;
    }

    public class SkinTextureUrls {
        private String skin;
        private String cape;

        public String getSkin() {
            return skin;
        }

        public String getCape() {
            return cape;
        }
    }

    public class SkinTexture {
        private String value;
        private String signature;
        private String url;
        private SkinTextureUrls urls;

        public String getValue() {
            return value;
        }

        public String getSignature() {
            return signature;
        }

        public String getUrl() {
            return url;
        }

        public SkinTextureUrls getUrls() {
            return urls;
        }
    }

    public class SkinData {
        private String name;
        private UUID uuid;
        private SkinTexture texture;

        public UUID getUUID() {
            return uuid;
        }

        public String getName() {
            return name;
        }

        public SkinTexture getTexture() {
            return texture;
        }
    }

    public enum SkinVariant {
        UNKNOWN,
        CLASSIC,
        SLIM
    }

    private int id;
    private String idStr;
    private String uuid;
    private String name;
    private SkinVariant variant;
    private SkinData data;
    private double timestamp;
    private int duration;
    @SerializedName("account")
    private int accountId;
    private String server;
    @SerializedName("private")
    private boolean privateSkin;
    private int views;
    private boolean duplicate;
    private double nextRequest;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public SkinData getData() {
        return data;
    }

    public GameProfile getGameProfile() {
        if (getData() == null) {
            return null;
        }

        GameProfile profile = new GameProfile(getData().getUUID(), StringUtils.stripToNull(getData().getName()) == null ? "Unknown" : getData().getName());

        if (getData().getTexture() != null) {
            Property property = new Property("textures", getData().getTexture().getValue(), getData().getTexture().getSignature());
            profile.getProperties().put("textures", property);
        }

        return profile;
    }
}
