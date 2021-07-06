package com.stardevmc.chat.convo;

import com.stardevmc.chat.TitanChat;
import com.stardevmc.chat.RoomBuilder;
import com.firestar311.lib.util.Utils;
import org.bukkit.conversations.*;

public class ChatRoomDisplayName extends StringPrompt {
    public String getPromptText(ConversationContext context) {
        return Utils.color("&2Starting the creation of a chat room, please provide the display name.");
    }
    
    public Prompt acceptInput(ConversationContext context, String s) {
        TitanChat plugin = (TitanChat) context.getPlugin();
        RoomBuilder builder = plugin.getBuilder(context);
        builder.setDisplayName(s);
        return new ChatRoomPermission();
    }
}
