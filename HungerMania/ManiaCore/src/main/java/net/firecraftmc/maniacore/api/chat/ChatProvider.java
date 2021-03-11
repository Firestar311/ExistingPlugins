package net.firecraftmc.maniacore.api.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.firecraftmc.maniacore.plugin.ManiaPlugin;
import net.firecraftmc.manialib.util.Priority;

@Getter
@AllArgsConstructor
public class ChatProvider {
    
    private ManiaPlugin maniaPlugin;
    private ChatHandler handler;
    private Priority priority;
}
