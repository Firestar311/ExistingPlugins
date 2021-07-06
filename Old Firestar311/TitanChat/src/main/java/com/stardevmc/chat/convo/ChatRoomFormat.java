package com.stardevmc.chat.convo;

import com.stardevmc.chat.TitanChat;
import com.stardevmc.chat.RoomBuilder;
import com.firestar311.lib.util.Utils;
import org.bukkit.conversations.*;

public class ChatRoomFormat extends StringPrompt {
    public String getPromptText(ConversationContext conversationContext) {
        return Utils.color("&2Please provide the format for the chat room.");
    }
    
    public Prompt acceptInput(ConversationContext context, String s) {
        TitanChat plugin = (TitanChat) context.getPlugin();
        RoomBuilder builder = plugin.getBuilder(context);
        builder.setFormat(s);
        return new ChatRoomDescription();
    }
}