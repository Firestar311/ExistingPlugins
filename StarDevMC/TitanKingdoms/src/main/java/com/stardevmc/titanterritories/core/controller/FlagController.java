package com.stardevmc.titanterritories.core.controller;

import com.firestar311.lib.util.Utils;
import com.stardevmc.titanterritories.core.objects.enums.Flag;
import com.stardevmc.titanterritories.core.objects.enums.Permission;
import com.stardevmc.titanterritories.core.objects.interfaces.IHolder;
import com.stardevmc.titanterritories.core.objects.interfaces.IUser;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;

import java.util.*;

public class FlagController<T extends IHolder> extends Controller<T> {
    private List<Flag> flags = new ArrayList<>();
    
    public FlagController(T holder) {
        super(holder);
    }
    
    private FlagController() {
    
    }
    
    public void handleCommand(Command cmd, IHolder holder, IUser user, String[] args) {
        if (Utils.checkCmdAliases(args, 0, "flags", "f")) {
            if (!user.hasPermission(Permission.FLAGS)) {
                user.sendMessage("&cYou do not have permission to modify the flags");
                return;
            }
            if (!(args.length > 1)) {
                user.sendMessage("&cYou do not have enough arguments.");
                return;
            }
    
            if (Utils.checkCmdAliases(args, 1, "add", "a")) {
                if (!(args.length > 2)) {
                    user.sendMessage("&cYou do not have enough arguments.");
                    return;
                }
                List<Flag> flags = new ArrayList<>();
                for (int i = 2; i < args.length; i++) {
                    try {
                        flags.add(Flag.valueOf(args[i]));
                    } catch (IllegalArgumentException e) {
                        user.sendMessage("&cThe value " + args[i] + " is not a valid flag.");
                    }
                }
        
                if (flags.isEmpty()) {
                    user.sendMessage("&cThere was a problem getting all of the flags.");
                    return;
                }
        
                for (Flag flag : flags) {
                    addFlag(flag);
                    user.sendMessage("&aYou added the flag " + flag.name() + " to your kingdom.");
                }
            } else if (Utils.checkCmdAliases(args, 1, "remove", "r")) {
                if (!(args.length > 2)) {
                    user.sendMessage("&cYou do not have enough arguments.");
                    return;
                }
        
                Flag flag;
                try {
                    flag = Flag.valueOf(args[2]);
                } catch (IllegalArgumentException e) {
                    user.sendMessage("&cThe value you provided was not a valid flag.");
                    return;
                }
        
                removeFlag(flag);
                user.sendMessage("&aYou removed the flag " + flag.name() + " from " + holder.getName());
            } else if (Utils.checkCmdAliases(args, 1, "list", "l")) {
                user.sendMessage("&a" + holder.getName() +  " has the flag(s): " + getFlagString());
            }
        }
    }
    
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        List<String> stringFlags = new ArrayList<>();
        for (Flag flag : flags) {
            stringFlags.add(flag.name());
        }
        serialized.put("flags", stringFlags);
        return serialized;
    }
    
    public static FlagController deserialize(Map<String, Object> serialized) {
        List<String> rawFlags = (List<String>) serialized.get("flags");
        List<Flag> flags = new ArrayList<>();
        for (String rf : rawFlags) {
            flags.add(Flag.valueOf(rf));
        }
        FlagController controller = new FlagController();
        controller.flags = flags;
        return controller;
    }
    
    public List<Flag> getFlags() {
        return new ArrayList<>(flags);
    }
    
    public void addFlag(Flag... flags) {
        this.flags.addAll(Arrays.asList(flags));
    }
    
    public void removeFlag(Flag flag) {
        this.flags.remove(flag);
    }
    
    public String getFlagString() {
        List<String> flagStrings = new ArrayList<>();
        getFlags().forEach(flag -> flagStrings.add(flag.name()));
        return StringUtils.join(flagStrings, ", ");
    }
}