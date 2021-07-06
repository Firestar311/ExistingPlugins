package com.stardevmc.enforcer.manager;

import com.stardevmc.enforcer.Enforcer;
import com.stardevmc.enforcer.modules.base.Manager;
import com.stardevmc.enforcer.objects.target.*;
import com.starmediadev.lib.user.User;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

//TODO Add saving and loading
public class TargetManager extends Manager {
    
    private Set<Target> targets = new HashSet<>();
    
    public TargetManager(Enforcer plugin) {
        super(plugin, "targets");
    }
    
    public void addTarget(Target target) {
        this.targets.add(target);
    }
    
    @Override
    public void saveData() {
    
    }
    
    @Override
    public void loadData() {
    
    }
    
    public Target getTarget(String targetArg) {
        if (!targets.isEmpty()) {
            for (Target target : this.targets) {
                if (target != null) {
                    if (!StringUtils.isEmpty(target.getName())) {
                        if (target.getName().equalsIgnoreCase(targetArg)) {
                            return target;
                        }
                    }
                }
            }
        }
        
        Target target;
        User info = Enforcer.getInstance().getPlayerManager().getUser(targetArg);
        if (info != null) {
            target = new PlayerTarget(info.getUniqueId());
        } else {
            targetArg = targetArg.toLowerCase();
            if (targetArg.startsWith("ip:")) {
                String[] ipArr = targetArg.split(":");
                User ipPlayer = Enforcer.getInstance().getPlayerManager().getUser(ipArr[1]);
                if (ipPlayer == null) {
                    return null;
                }
                
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(ipPlayer.getUniqueId());
                if (offlinePlayer.isOnline()) {
                    Player player = offlinePlayer.getPlayer();
                    String ip = player.getAddress().getAddress().toString().split(":")[0].replace("/", "");
                    target = new IPTarget(ip);
                } else {
                    if (ipPlayer.getIpAddresses().size() == 1) {
                        target = new IPTarget(ipPlayer.getIpAddresses().get(0));
                    } else {
                        target = new IPListTarget(ipPlayer.getIpAddresses());
                    }
                }
            } else {
                String[] rawIpArr = targetArg.split("\\.");
                if (rawIpArr.length != 4) {
                    return null;
                } else {
                    for (String rawPart : rawIpArr) {
                        try {
                            Integer.parseInt(rawPart);
                        } catch (NumberFormatException e) {
                            //if (!rawPart.equalsIgnoreCase("*")) {
                            return null;
                            //}
                        }
                    }
                    
                    target = new IPTarget(targetArg);
                }
            }
        }
        
        return target;
    }
}