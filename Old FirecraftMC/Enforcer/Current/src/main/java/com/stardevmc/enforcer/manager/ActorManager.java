package com.stardevmc.enforcer.manager;

import com.stardevmc.enforcer.Enforcer;
import com.stardevmc.enforcer.modules.base.Manager;
import com.stardevmc.enforcer.objects.actor.*;
import com.starmediadev.lib.user.User;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

//TODO Add saving and loading
public class ActorManager extends Manager {
    
    private ConsoleActor consoleActor;
    private Set<PlayerActor> playerActors = new HashSet<>();
    
    public ActorManager(Enforcer plugin) {
        super(plugin, "actors");
        this.consoleActor = new ConsoleActor();
    }
    
    public void addActor(Actor actor) {
        if (actor instanceof ConsoleActor) {
            this.consoleActor = (ConsoleActor) actor;
        } else {
            this.playerActors.add((PlayerActor) actor);
        }
    }
    
    @Override
    public void saveData() {
    
    }
    
    @Override
    public void loadData() {
    
    }
    
    public Actor getActor(String arg) {
        if (consoleActor == null) consoleActor = new ConsoleActor();
        if (arg.equals(consoleActor.getName())) {
            return consoleActor;
        }
        
        if (!playerActors.isEmpty()) {
            for (PlayerActor playerActor : playerActors) {
                if (playerActor != null) {
                    if (!StringUtils.isEmpty(playerActor.getName())) {
                        if (playerActor.getName().equalsIgnoreCase(arg)) {
                            return playerActor;
                        } else if (playerActor.getUniqueId().toString().equalsIgnoreCase(arg)) {
                            return playerActor;
                        }
                    }
                }
            }
        }
    
        User user = Enforcer.getInstance().getPlayerManager().getUser(arg);
        if (user != null) {
            return new PlayerActor(user.getUniqueId());
        }
        return null;
    }
    
    public Actor getActor(CommandSender sender) {
        Actor actor = null;
        if (sender instanceof Player) {
            actor = new PlayerActor(((Player) sender).getUniqueId());
            this.playerActors.add((PlayerActor) actor);
        } else if (sender instanceof ConsoleCommandSender) {
            actor = new ConsoleActor();
            this.consoleActor = (ConsoleActor) actor;
        }
        
        return actor;
    }
}