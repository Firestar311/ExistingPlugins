package net.firecraftmc.api.model.player;

import net.firecraftmc.api.util.Utils;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;

/**
 * A class that represents an ActionBar as there is no Bukkit API for it. Uses reflection
 */
public class ActionBar {

    private Object packetPlayOutChat;
    private String jsonText = "";

    /**
     * Constructor for creating an ActionBar object
     * @param message The message to be displayed in the ActionBar
     */
    public ActionBar(String message) {
        message = Utils.color(message);
        String jsonText = "{\"text\":\"" + message + "\"}";

        String version = Utils.Reflection.getVersion();
        if (version.equalsIgnoreCase("v1_12_R1") || version.equalsIgnoreCase("v1_13_R1") || version.equalsIgnoreCase("v1_13_R2")) {
            try {
                Object chatmsgtype = Utils.Reflection.getNMSClass("ChatMessageType").getField("GAME_INFO").get(null);
                Class<?> chatSerializer = Utils.Reflection.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0];
                Object chat = chatSerializer.getMethod("a", String.class).invoke(null, jsonText);
                Constructor<?> chatConstructor = Utils.Reflection.getNMSClass("PacketPlayOutChat").getConstructor(
                        Utils.Reflection.getNMSClass("IChatBaseComponent"),
                        Utils.Reflection.getNMSClass("ChatMessageType"));
                this.packetPlayOutChat = chatConstructor.newInstance(chat, chatmsgtype);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                Class<?> chatSerializer = Utils.Reflection.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0];
                Object chat = chatSerializer.getMethod("a", String.class).invoke(null, jsonText);
                Constructor<?> chatConstructor = Utils.Reflection.getNMSClass("PacketPlayOutChat")
                        .getConstructor(Utils.Reflection.getNMSClass("IChatBaseComponent"), byte.class);
                this.packetPlayOutChat = chatConstructor.newInstance(chat, (byte) 2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Allows for changing the text on an ActionBar without having to create a new one.
     * @param text The new text of which to be displayed
     */
    public void changeText(String text) {
        if (!jsonText.equals("")) {
            return;
        }
        jsonText = "{\"text\":\"" + text + "\"}";

        System.out.println(Utils.Reflection.getVersion());

        if (Utils.Reflection.getVersion().equals("v1_12_R1")) {
            try {
                Object chatmsgtype = Utils.Reflection.getNMSClass("ChatMessageType").getField("GAME_INFO").get(null);
                Class<?> chatSerializer = Utils.Reflection.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0];
                Object chat = chatSerializer.getMethod("a", String.class).invoke(null, jsonText);
                Constructor<?> chatConstructor = Utils.Reflection.getNMSClass("PacketPlayOutChat").getConstructor(
                        Utils.Reflection.getNMSClass("IChatBaseComponent"),
                        Utils.Reflection.getNMSClass("ChatMessageType"));
                this.packetPlayOutChat = chatConstructor.newInstance(chat, chatmsgtype);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                Class<?> chatSerializer = Utils.Reflection.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0];
                Object chat = chatSerializer.getMethod("a", String.class).invoke(null, jsonText);
                Constructor<?> chatConstructor = Utils.Reflection.getNMSClass("PacketPlayOutChat")
                        .getConstructor(Utils.Reflection.getNMSClass("IChatBaseComponent"), byte.class);
                this.packetPlayOutChat = chatConstructor.newInstance(chat, (byte) 2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sends the actionbar to the given player.
     * @param player The player to send the action bar to.
     */
    public void send(Player player) {
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            playerConnection.getClass().getMethod("sendPacket", Utils.Reflection.getNMSClass("Packet"))
                    .invoke(playerConnection, packetPlayOutChat);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}