package me.alonedev.ihhub.Commands;


import me.alonedev.ihhub.IHhub;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand implements CommandExecutor {

    private static IHhub main;

    public SpawnCommand(IHhub main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;
            Location spawn = new Location(Bukkit.getWorld(main.getConfig().getString("Spawn.spawnWorld")), main.getConfig().getDouble("Spawn.x"), main.getConfig().getDouble("Spawn.y"), main.getConfig().getDouble("Spawn.z"), main.getConfig().getInt("Spawn.yaw"), main.getConfig().getInt("Spawn.pitch"));
            player.teleport(spawn);
            return true;
        }
        return false;
    }
}
