package com.stardevmc.enforcer.modules.punishments.gui;

import com.stardevmc.enforcer.Enforcer;
import com.stardevmc.enforcer.objects.enums.Visibility;
import com.stardevmc.enforcer.modules.punishments.gui.button.VisibilityButton;
import com.stardevmc.enforcer.objects.target.Target;
import com.starmediadev.lib.gui.*;
import org.bukkit.entity.Player;

import static com.stardevmc.enforcer.modules.punishments.gui.CustomPunishMainGUI.SIZE;

public class CustomPunishVisibilityGUI extends PaginatedGUI {
    public CustomPunishVisibilityGUI(Enforcer plugin, Player player, Target target) {
        super(plugin, "Custom Punishment > " + target.getName(), false, SIZE, false);
    
        ButtonListener listener = e -> {
            VisibilityButton button = (VisibilityButton) getButton(e.getSlot());
            CustomPunishSettingsGUI.getPunishmentBuilder(e.getWhoClicked().getUniqueId()).setVisibility(button.getVisibility());
            new CustomPunishSettingsGUI(plugin, player, target).openGUI(player);
        };
        
        int slot = 11;
        for (Visibility visibility : Visibility.values()) {
            VisibilityButton button = new VisibilityButton(visibility);
            button.setListener(listener);
            setButton(slot, button);
            slot += 2;
        }
    }
}