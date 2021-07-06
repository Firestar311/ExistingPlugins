package com.stardevmc.titanterritories.core.controller;

import com.firestar311.lib.pagination.*;
import com.firestar311.lib.util.Utils;
import com.stardevmc.titanterritories.core.TitanTerritories;
import com.stardevmc.titanterritories.core.objects.enums.Permission;
import com.stardevmc.titanterritories.core.objects.interfaces.IHolder;
import com.stardevmc.titanterritories.core.objects.interfaces.IUser;
import com.stardevmc.titanterritories.core.objects.kingdom.Visit;
import com.stardevmc.titanterritories.core.objects.kingdom.Warp;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import org.bukkit.Location;
import org.bukkit.command.Command;

import java.util.*;

public class WarpController<T extends IHolder> extends Controller<T> {
    private List<Warp> warps = new ArrayList<>();
    
    public WarpController(T holder) {
        super(holder);
    }
    
    public WarpController() {
    }
    
    public void handleCommand(Command cmd, T holder, IUser user, String[] args) {
        if (Utils.checkCmdAliases(args, 1, "list", "l")) {
            Paginator<Warp> paginator = PaginatorFactory.generatePaginator(7, getWarps(), new HashMap<DefaultVariables, String>() {{
                put(DefaultVariables.COMMAND, "/" + cmd.getName() + " warps");
                put(DefaultVariables.TYPE, "Warps");
            }});
            
            if (args.length > 2) {
                paginator.display(user.getPlayer(), args[2]);
            } else {
                paginator.display(user.getPlayer(), 1);
            }
        } else if (Utils.checkCmdAliases(args, 1, "add", "a", "create", "c")) {
            //kingdom warps add|create|a|c <name> [ranks...]
            if (!(args.length > 2)) {
                user.sendMessage("&cYou must provide a warp name.");
                return;
            }
            
            if (!user.hasPermission(Permission.CREATE_WARP)) {
                user.sendMessage("&cYou do not have permission to create warps for your " + holder.getClass().getSimpleName());
                return;
            }
            
            String name = args[2];
            Location location = user.getLocation();
            if (getWarp(name) != null) {
                user.sendMessage("&cA warp with that name already exists.");
                return;
            }
            
            Warp warp = new Warp(name, location, user.getUniqueId());
            addWarp(warp);
            user.sendMessage("&aYou added a warp with the name " + name + " at your current location.");
        } else if (Utils.checkCmdAliases(args, 1, "remove", "r", "delete", "d")) {
            if (!(args.length > 2)) {
                user.sendMessage("&cYou must provide the warp name.");
                return;
            }
    
            if (!user.hasPermission(Permission.REMOVE_WARP)) {
                user.sendMessage("&cYou do not have permission to remove warps for your " + holder.getClass().getSimpleName());
                return;
            }
    
            String name = args[2];
            if (getWarp(name) == null) {
                user.sendMessage("&cA warp with that name does not exist.");
                return;
            }
    
            removeWarp(name);
            user.sendMessage("&aYou removed the warp with the name " + name);
        } else if (Utils.checkCmdAliases(args, 1, "edit", "e")) {
            // kingdom warp edit {name} name {newName}
            if (!(args.length > 2)) {
                user.sendMessage("&cYou must provide the warp name.");
                return;
            }
            
            Warp warp = getWarp(args[2]);
            if (warp == null) {
                user.sendMessage("&cA warp with that name does not exist.");
                return;
            }
            
            if (Utils.checkCmdAliases(args, 3, "name", "n")) {
                if (!(args.length > 4)) {
                    user.sendMessage("&cYou must provide the new warp name.");
                    return;
                }
                
                if (!warp.getCreator().equals(user.getUniqueId())) {
                    if (!user.hasPermission(Permission.EDIT_WARPS)) {
                        user.sendMessage("&cYou do not have permisson to edit that warp.");
                        return;
                    }
                }
                
                String name = args[4];
                if (getWarp(name) != null) {
                    user.sendMessage("&cA warp with the new name already exists.");
                    return;
                }
                
                warp.setName(name);
                user.sendMessage("&aYou changed the name of that warp to " + name);
            } else if (Utils.checkCmdAliases(args, 3, "location", "loc", "l")) {
                if (!warp.getCreator().equals(user.getUniqueId())) {
                    if (!user.hasPermission(Permission.EDIT_WARPS)) {
                        user.sendMessage("&cYou do not have permisson to edit that warp.");
                        return;
                    }
                }
                
                warp.setLocation(user.getLocation());
                user.sendMessage("&aYou changed the location of that warp to your current location");
            }
        } else if (Utils.checkCmdAliases(args, 1, "info", "i", "view", "v")) {
            if (!(args.length > 2)) {
                user.sendMessage("&cYou must provide a warp name.");
                return;
            }
            
            Warp warp = getWarp(args[2]);
            if (warp == null) {
                user.sendMessage("&cA warp by that name does not exist.");
                return;
            }
            
            String creatorName = TitanTerritories.getInstance().getMemberManager().getMember(warp.getCreator()).getName();
            user.sendMessage("&6Viewing information for warp " + warp.getName());
            user.sendMessage("&7Creator: " + creatorName);
            BaseComponent[] components = new ComponentBuilder("Total Visits: " + warp.getVisitHistory().size()).event(new ClickEvent(Action.RUN_COMMAND, "/" + cmd.getName() + " warps viewvisits " + warp.getName())).color(ChatColor.GRAY).create();
            user.sendMessage(components);
        } else if (Utils.checkCmdAliases(args, 1, "viewvisits")) {
            if (!(args.length > 2)) {
                user.sendMessage("&cYou must provide a warp name");
                return;
            }
            Warp warp = getWarp(args[2]);
            if (warp == null) {
                user.sendMessage("&cA warp by that name does not exist.");
                return;
            }
            Paginator<Visit> paginator = PaginatorFactory.generatePaginator(7, warp.getVisitHistory(), new HashMap<DefaultVariables, String>() {{
                put(DefaultVariables.COMMAND, "/" + cmd.getName() + " warps viewvisits " + warp.getName());
                put(DefaultVariables.TYPE, "Warp Visits");
            }});
    
            if (args.length > 3) {
                paginator.display(user.getPlayer(), args[3]);
            } else {
                paginator.display(user.getPlayer(), 1);
            }
        } else {
            if (!(args.length > 1)) {
                user.sendMessage("&cYou must provide a warp name.");
                return;
            }
            
            Warp warp = getWarp(args[1]);
            if (warp == null) {
                user.sendMessage("&cA warp by that name does not exist");
                return;
            }
            
            user.teleport(warp.getLocation());
            warp.addVisit(new Visit(user.getUniqueId(), System.currentTimeMillis()));
            user.sendMessage("&aTeleported you to the warp " + warp.getName());
        }
    }
    
    private void removeWarp(String name) {
        this.warps.remove(getWarp(name));
    }
    
    public void addWarp(Warp warp) {
        this.warps.add(warp);
    }
    
    public void removeWarp(Warp warp) {
        this.warps.remove(warp);
    }
    
    public Warp getWarp(String name) {
        for (Warp warp : warps) {
            if (warp != null) {
                if (warp.getName().equalsIgnoreCase(name)) {
                    return warp;
                }
            }
        }
        
        return null;
    }
    
    public List<Warp> getWarps() {
        return new ArrayList<>(warps);
    }
    
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("warpAmount", getWarps().size() + "");
        for (int i = 0; i < getWarps().size(); i++) {
            serialized.put("warp" + i, getWarps().get(i));
        }
        return serialized;
    }
    
    public static WarpController deserialize(Map<String, Object> serialized) {
        List<Warp> warps = new ArrayList<>();
        if (serialized.containsKey("warpAmount")) {
            int amount = Integer.parseInt((String) serialized.get("warpAmount"));
            for (int i = 0; i < amount; i++) {
                Warp warp = (Warp) serialized.get("warp" + i);
                if (warp != null) {
                    warps.add(warp);
                }
            }
        }
        WarpController controller = new WarpController();
        controller.warps = warps;
        return controller;
    }
}