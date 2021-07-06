package com.stardevmc.chat.convo;

import com.stardevmc.chat.TitanChat;
import com.stardevmc.chat.RoomBuilder;
import com.firestar311.lib.util.Utils;
import org.bukkit.conversations.*;

public class ChatRoomPermission extends StringPrompt {
    public String getPromptText(ConversationContext conversationContext) {
        return Utils.color("&2Please provide the permission for this chat room. Type &a'none'&2 for no permission.");
    }
    
    public Prompt acceptInput(ConversationContext context, String s) {
        TitanChat plugin = (TitanChat) context.getPlugin();
        RoomBuilder builder = plugin.getBuilder(context);
        if (s.equalsIgnoreCase("none")) builder.setPermission("");
        else builder.setPermission(s.toLowerCase());
        return new ChatRoomFormat();
    }
}