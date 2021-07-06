package com.stardevmc.enforcer.modules.punishments.gui;

import com.stardevmc.enforcer.Enforcer;
import com.stardevmc.enforcer.objects.target.Target;
import com.starmediadev.lib.builder.ItemBuilder;
import com.starmediadev.lib.gui.*;
import com.starmediadev.lib.util.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CustomPunishMainGUI extends PaginatedGUI {
    
    public static final int SIZE = 27;
    
    public CustomPunishMainGUI(Enforcer plugin, Player player, Target target) {
        super(plugin, "Custom Punishment > " + target.getName(), false, SIZE, false);
    
        GUIButton multipleRulePunishment = new GUIButton(ItemBuilder.start(Material.OAK_FENCE).withName("&aMULTIPLE RULE PUNISHMENT").withLore("&cNot enabled").buildItem());
        GUIButton normalPunishment = new GUIButton(ItemBuilder.start(Material.STRING).withName("&aREGULAR PUNISHMENT").withLore(Utils.wrapLore(35, "Create a regular custom punishment.")).buildItem());
    
        ButtonListener listener = e -> {
            ItemStack itemStack = e.getCurrentItem();
            if (itemStack == null) return;
            if (itemStack.hasItemMeta()) {
                if (itemStack.getItemMeta().getDisplayName().contains("MULTIPLE RULE PUNISHMENT")) {
                    player.sendMessage(Utils.color("&cThat feature is not enabled."));
                } else if (itemStack.getItemMeta().getDisplayName().contains("REGULAR PUNISHMENT")) {
                    new CustomPunishSettingsGUI(plugin, player, target).openGUI(player);
                }
            }
        };
        
        multipleRulePunishment.setListener(listener);
        normalPunishment.setListener(listener);
        
        setButton(12, multipleRulePunishment);
        setButton(14, normalPunishment);
    }
}