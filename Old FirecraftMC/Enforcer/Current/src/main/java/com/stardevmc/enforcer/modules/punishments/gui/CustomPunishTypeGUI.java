package com.stardevmc.enforcer.modules.punishments.gui;

import com.stardevmc.enforcer.Enforcer;
import com.stardevmc.enforcer.objects.enums.RawType;
import com.stardevmc.enforcer.modules.punishments.gui.button.TypeButton;
import com.stardevmc.enforcer.objects.target.Target;
import com.starmediadev.lib.gui.ButtonListener;
import com.starmediadev.lib.gui.PaginatedGUI;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

import static com.stardevmc.enforcer.modules.punishments.gui.CustomPunishMainGUI.SIZE;

public class CustomPunishTypeGUI extends PaginatedGUI {
    
    public CustomPunishTypeGUI(Enforcer plugin, Player player, Target target) {
        super(plugin, "Custom Punishment > " + target.getName(), false, SIZE, false);
    
        Map<RawType, Integer> slots = new HashMap<>() {{
           put(RawType.BAN, 11);
           put(RawType.MUTE, 12);
           put(RawType.KICK, 13);
           put(RawType.WARNING, 14);
           put(RawType.JAIL, 15);
           put(RawType.BLACKLIST,22);
        }};
    
        ButtonListener listener = e -> {
            TypeButton button = (TypeButton) getButton(e.getSlot());
            CustomPunishSettingsGUI.getPunishmentBuilder(e.getWhoClicked().getUniqueId()).setRawType(button.getType());
            new CustomPunishSettingsGUI(plugin, player, target).openGUI(player);
        };
        
        for (RawType type : RawType.values()) {
            TypeButton button = new TypeButton(type);
            button.setListener(listener);
            setButton(slots.get(type), button);
        }
    }
}