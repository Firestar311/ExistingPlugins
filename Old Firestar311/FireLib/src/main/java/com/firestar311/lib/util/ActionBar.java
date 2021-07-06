package com.firestar311.lib.util;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class ActionBar {

    private String message;

    public ActionBar(String message) {
        setText(message);
    }

    public void setText(String text) {
        message = Utils.color(text);
    }

    public void send(Player player) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
    }
}