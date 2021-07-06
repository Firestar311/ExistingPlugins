package com.stardevmc.chat.convo;

import com.firestar311.lib.util.Utils;
import com.stardevmc.chat.RoomBuilder;
import com.stardevmc.chat.TitanChat;
import com.stardevmc.chat.api.IChatroom;
import org.bukkit.conversations.*;

public class ChatRoomDescription extends StringPrompt {
    public String getPromptText(ConversationContext conversationContext) {
        return Utils.color("&2Please provide a description for this chat room. You can put &a'none'");
    }
    
    public Prompt acceptInput(ConversationContext context, String s) {
        TitanChat plugin = (TitanChat) context.getPlugin();
        RoomBuilder builder = plugin.getBuilder(context);
        builder.setDescription(s);
        IChatroom chatroom = builder.buildChatRoom();
        plugin.getChatroomManager().registerChatroom(chatroom);
        
        return new MessagePrompt() {
            protected Prompt getNextPrompt(ConversationContext context) {
                return END_OF_CONVERSATION;
            }
    
            public String getPromptText(ConversationContext context) {
                return Utils.color("&2Thank you, the chatroom has been created.");
            }
        };
    }
}