package net.firecraftmc.api.interfaces;

import net.firecraftmc.api.command.FirecraftCommand;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;

import java.util.Set;

public interface ICommandManager extends CommandExecutor, Listener {

    FirecraftCommand getCommand(String cmd);
    void addCommand(FirecraftCommand command);
    void addCommands(FirecraftCommand... cmds);
    void removeCommand(String name);
    void removeCommand(FirecraftCommand command);
    Set<FirecraftCommand> getCommands();
}