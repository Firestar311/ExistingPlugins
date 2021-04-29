package net.firecraftmc.maniacore.api.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.firecraftmc.maniacore.plugin.CenturionsPlugin;
import net.firecraftmc.manialib.util.Priority;

@Getter
@AllArgsConstructor
public class ChatProvider {
    
    private CenturionsPlugin centurionsPlugin;
    private ChatHandler handler;
    private Priority priority;
}
