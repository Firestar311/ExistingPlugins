package net.firecraftmc.api.interfaces;

import net.firecraftmc.api.model.server.FirecraftServer;
import org.bukkit.command.CommandExecutor;

public interface IServerManager extends CommandExecutor {

    FirecraftServer getServer(String id);
}