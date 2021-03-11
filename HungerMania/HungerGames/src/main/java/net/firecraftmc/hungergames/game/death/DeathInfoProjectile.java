package net.firecraftmc.hungergames.game.death;

import lombok.Getter;
import net.firecraftmc.hungergames.HungerGames;
import net.firecraftmc.hungergames.game.Game;
import net.firecraftmc.maniacore.spigot.user.SpigotUser;
import net.firecraftmc.manialib.util.Utils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter
public class DeathInfoProjectile extends DeathInfo {
    
    private Entity shooter;
    private double distance;
    private String killerTeamColor;
    
    public DeathInfoProjectile(UUID player, Entity shooter, double v, String teamColor) {
        super(player, DeathType.PROJECTILE);
        this.shooter = shooter;
        distance = v;
        this.killerTeamColor = teamColor;
    }
    
    public String getDeathMessage(Game game) {
        String killerName;
        if (shooter instanceof Player) {
            Player playerShooter = (Player) shooter;
            SpigotUser spigotUser = (SpigotUser) HungerGames.getInstance().getManiaCore().getUserManager().getUser(playerShooter.getUniqueId());
            if (spigotUser.getNickname() != null || spigotUser.getNickname().isActive()) {
                killerName = killerTeamColor + spigotUser.getNickname().getName();
            } else {
                killerName = killerTeamColor + spigotUser.getName();
            }
        } else {
            killerName = "&f" + Utils.capitalizeEveryWord(shooter.getType().name());
        }
        
        this.deathMessage = "&4&l>> %playername% &7was shot by " + killerName + " &7from &f" + Utils.formatNumber(distance) + " blocks.";
        return super.getDeathMessage(game);
    }
}
