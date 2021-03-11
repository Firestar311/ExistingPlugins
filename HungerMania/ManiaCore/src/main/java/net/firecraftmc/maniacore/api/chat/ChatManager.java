package net.firecraftmc.maniacore.api.chat;

import net.firecraftmc.maniacore.api.channel.Channel;
import net.firecraftmc.maniacore.plugin.ManiaPlugin;
import net.firecraftmc.manialib.util.Priority;

import java.util.*;

public class ChatManager {
    
    private Set<net.firecraftmc.maniacore.api.chat.ChatProvider> chatProviders = new HashSet<>();
    private Map<net.firecraftmc.maniacore.api.channel.Channel, net.firecraftmc.maniacore.api.chat.ChatFormatter> formatters = new HashMap<>();
    
    public static final net.firecraftmc.maniacore.api.chat.ChatFormatter DEFAULT_FORMATTER = new net.firecraftmc.maniacore.api.chat.ChatFormatter("{name}&8: {message}");
    
    public static final net.firecraftmc.maniacore.api.chat.ChatHandler DEFAULT_HANDLER = new net.firecraftmc.maniacore.api.chat.ChatHandler() {
        public Set<UUID> getAllTargets() {
            return new HashSet<>();
        }
    };
    
    public net.firecraftmc.maniacore.api.chat.ChatFormatter getChatFormatter(net.firecraftmc.maniacore.api.channel.Channel channel) {
        net.firecraftmc.maniacore.api.chat.ChatFormatter chatFormatter = this.formatters.get(channel);
        return chatFormatter != null ? chatFormatter : DEFAULT_FORMATTER;
    }
    
    public void setFormatter(Channel channel, ChatFormatter formatter) {
        this.formatters.put(channel, formatter);
    }
    
    public void registerHandler(ManiaPlugin plugin, net.firecraftmc.maniacore.api.chat.ChatHandler chatHandler, Priority priority) {
        this.chatProviders.add(new net.firecraftmc.maniacore.api.chat.ChatProvider(plugin, chatHandler, priority));
    }
    
    public ChatHandler getHandler() {
        net.firecraftmc.maniacore.api.chat.ChatProvider provider = null;
        for (ChatProvider chatProvider : chatProviders) {
            if (provider == null) {
                provider = chatProvider;
            } else {
                if (provider.getPriority().ordinal() < chatProvider.getPriority().ordinal()) {
                    provider = chatProvider;
                }
            }
        }
        return provider == null ? DEFAULT_HANDLER : provider.getHandler();
    }
}