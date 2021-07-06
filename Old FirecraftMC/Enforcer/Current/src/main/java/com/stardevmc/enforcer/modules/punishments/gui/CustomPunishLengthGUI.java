package com.stardevmc.enforcer.modules.punishments.gui;

import com.stardevmc.enforcer.Enforcer;
import com.stardevmc.enforcer.modules.punishments.prompt.LengthPrompt;
import com.stardevmc.enforcer.objects.punishment.PunishmentBuilder;
import com.stardevmc.enforcer.objects.target.Target;
import com.starmediadev.lib.builder.ItemBuilder;
import com.starmediadev.lib.gui.*;
import org.bukkit.Material;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static com.stardevmc.enforcer.modules.punishments.gui.CustomPunishMainGUI.SIZE;

public class CustomPunishLengthGUI extends PaginatedGUI {
    public CustomPunishLengthGUI(Enforcer plugin, Player player, Target target) {
        super(plugin, "Custom Punishment > " + target.getName(), false, SIZE, false);
    
        ItemBuilder permanentBuilder = ItemBuilder.start(Material.BEDROCK).withName("&aPERMANENT");
        ItemBuilder temporaryBuilder = ItemBuilder.start(Material.POPPED_CHORUS_FRUIT).withName("&aTEMPORARY");
    
        GUIButton permanentButton = new GUIButton(permanentBuilder.buildItem());
        GUIButton temporaryButton = new GUIButton(temporaryBuilder.buildItem());
    
        ButtonListener listener = e -> {
            ItemStack itemStack = e.getCurrentItem();
            PunishmentBuilder puBuilder = CustomPunishSettingsGUI.getPunishmentBuilder(e.getWhoClicked().getUniqueId());
            if (itemStack.hasItemMeta()) {
                if (itemStack.getItemMeta().getDisplayName().contains("PERMANENT")) {
                    puBuilder.setLength(-1);
                    new CustomPunishSettingsGUI(plugin, player, target).openGUI(player);
                } else if (itemStack.getItemMeta().getDisplayName().contains("TEMPORARY")) {
                    player.closeInventory();
                    new ConversationFactory(plugin).withFirstPrompt(new LengthPrompt()).withLocalEcho(false).buildConversation(player).begin();
                }
            }
        };
        
        permanentButton.setListener(listener);
        temporaryButton.setListener(listener);
        
        setButton(12, permanentButton);
        setButton(14, temporaryButton);
    }
}