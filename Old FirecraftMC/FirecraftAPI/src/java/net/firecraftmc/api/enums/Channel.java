package net.firecraftmc.api.enums;

public enum Channel {
    GLOBAL("", "§f"), STAFF("STAFF", "§a"), PRIVATE("P", "§e"), GUILD("G", "§2");

    private final String channelPrefix;
    private final String color;

    Channel(String cp, String color) {
        this.channelPrefix = cp;
        this.color = color;
    }

    public String getChannelPrefix() {
        return channelPrefix;
    }

    public String getColor() {
        return color;
    }
}