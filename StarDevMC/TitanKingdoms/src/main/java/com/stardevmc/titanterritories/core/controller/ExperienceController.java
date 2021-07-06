package com.stardevmc.titanterritories.core.controller;

import com.firestar311.lib.pagination.*;
import com.firestar311.lib.util.Utils;
import com.stardevmc.titanterritories.core.objects.interfaces.IHolder;
import com.stardevmc.titanterritories.core.objects.interfaces.IUser;
import com.stardevmc.titanterritories.core.objects.kingdom.ExperienceAction;
import com.stardevmc.titanterritories.core.objects.kingdom.ExperienceAction.Type;
import org.bukkit.command.Command;

import java.util.*;

public class ExperienceController<T extends IHolder> extends Controller<T> {
    
    private List<ExperienceAction> actions = new ArrayList<>();
    
    public ExperienceController(T kingdom) {
        super(kingdom);
    }
    
    private ExperienceController() {}
    
    public void handleCommand(Command cmd, IHolder holder, IUser user, String[] args) {
        if (args.length == 1) {
            double totalExperience = 0;
            for (ExperienceAction action : this.actions) {
                if (action.getType().equals(Type.GAIN)) {
                    totalExperience += action.getAmount();
                } else if (action.getType().equals(Type.LOSS)) {
                    totalExperience -= action.getAmount();
                }
            }
            
            user.sendMessage("&aTotal kindom experience " + totalExperience);
        } else if (Utils.checkCmdAliases(args, 1, "listactions", "la")) {
            Paginator<ExperienceAction> paginator = PaginatorFactory.generatePaginator(7, getActions(), new HashMap<DefaultVariables, String>() {{
                put(DefaultVariables.COMMAND, "/" + cmd.getName() +  " " + args[0] + "  listactions");
                put(DefaultVariables.TYPE, "Experience actions");
            }});
    
            if (args.length > 2) {
                paginator.display(user.getPlayer(), args[2]);
            } else {
                paginator.display(user.getPlayer(), 1);
            }
        } else if (Utils.checkCmdAliases(args, 1, "action")) {
            if (!(args.length > 3)) {
                user.sendMessage("&cNot enough arguments.");
                return;
            }
            
            if (!user.getPlayer().hasPermission("titankingdoms.admin.expaction")) {
                user.sendMessage("&cYou do not have permission to do that.");
                return;
            }
            
            ExperienceAction.Type type = ExperienceAction.Type.valueOf(args[2].toUpperCase());
            double amount;
            try {
                amount = Double.parseDouble(args[3]);
            } catch (NumberFormatException e) {
                user.sendMessage("&cThat is not a valid number.");
                return;
            }
            
            addExperienceAction(new ExperienceAction(amount, type, user));
            user.sendMessage("&aSuccess!");
        }
    }
    
    public void addExperienceAction(ExperienceAction action) {
        this.actions.add(action);
    }
    
    public double getTotalExperience() {
        double experience = 0.0;
        for (ExperienceAction action : actions) {
            if (action.getType().equals(ExperienceAction.Type.GAIN)) {
                experience += action.getAmount();
            } else if (action.getType().equals(Type.LOSS)) {
                experience -= action.getAmount();
            }
        }
        return experience;
    }
    
    public List<ExperienceAction> getActions() {
        return new ArrayList<>(actions);
    }
    
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = new HashMap<>();
        if (!getActions().isEmpty()) {
            serialized.put("amount", getActions().size());
            for (int i = 0; i < getActions().size(); i++) {
                serialized.put("action" + i, getActions().get(i));
            }
        }
        return serialized;
    }
    
    public static ExperienceController deserialize(Map<String, Object> serialized) {
        List<ExperienceAction> actions = new ArrayList<>();
        if (serialized.containsKey("xpactionAmount")) {
            int xpactionAmount = (int) serialized.get("xpactionAmount");
            for (int i = 0; i < xpactionAmount; i++) {
                actions.add((ExperienceAction) serialized.get("xpaction" + i));
            }
        }
        ExperienceController controller = new ExperienceController();
        controller.actions = actions;
        return controller;
    }
}