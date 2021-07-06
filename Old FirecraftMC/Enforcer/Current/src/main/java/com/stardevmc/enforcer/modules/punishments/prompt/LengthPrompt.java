package com.stardevmc.enforcer.modules.punishments.prompt;

import com.stardevmc.enforcer.Enforcer;
import com.stardevmc.enforcer.objects.punishment.PunishmentBuilder;
import com.stardevmc.enforcer.modules.punishments.gui.CustomPunishSettingsGUI;
import com.starmediadev.lib.util.Utils;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;

public class LengthPrompt extends StringPrompt {
    @Override
    public String getPromptText(ConversationContext context) {
        return Utils.color("&aPlease specify a punishment length.");
    }
    
    @Override
    public Prompt acceptInput(ConversationContext context, String input) {
        Player player = ((Player) context.getForWhom());
        PunishmentBuilder puBuilder = CustomPunishSettingsGUI.getPunishmentBuilder(player.getUniqueId());
        if (puBuilder == null) {
            player.sendMessage(Utils.color("&cYou are not creating a punishment through the GUI"));
            return new LengthPrompt();
        }
        
        puBuilder.setLength(Utils.parseTime(input));
        new CustomPunishSettingsGUI(((Enforcer) context.getPlugin()), player, puBuilder.getTarget()).openGUI(player);
        return END_OF_CONVERSATION;
    }
}