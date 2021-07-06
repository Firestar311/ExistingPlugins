package net.brutuspvp.core.managers;

import net.brutuspvp.core.BrutusCore;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

public class VanishManager implements Listener, CommandExecutor {

	private HashMap<UUID, Boolean> vanished = new HashMap<>();
	private BrutusCore plugin;

	public VanishManager(BrutusCore passedPlugin) {
		plugin = passedPlugin;
		plugin.registerListener(this);
	}

	public Set<UUID> getVanished() {
		return vanished.keySet();
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            if (args.length > 0) {
                UUID targetUUID = plugin.players().getUUID(args[0]);
                if (targetUUID == null) {
                    sender.sendMessage("§cThat player has not joined the server.");
                    return true;
                }

                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(targetUUID);
                if(offlinePlayer == null) {
                    sender.sendMessage("That player name does not exist.");
                    return true;
                }

                boolean join = false;
                if (args.length > 1) {
                    try {
                        join = Boolean.parseBoolean(args[1]);
                    } catch (Exception e) {
                        sender.sendMessage("§cInvalid boolean value. Must be true or false.");
                        return true;
                    }
                }

                if (this.vanished.containsKey(offlinePlayer.getUniqueId())) {
                    this.vanished.remove(offlinePlayer.getUniqueId());
                } else {
                    this.vanished.put(offlinePlayer.getUniqueId(), join);
                }
                sender.sendMessage("§aToggled §b" + offlinePlayer.getName() + "§a's vanish status.");

            }
        } else if (sender instanceof Player) {

        } else {
            sender.sendMessage("§cOnly Console and Players may use this command.");
            return true;
        }


		return true;
	}
}
