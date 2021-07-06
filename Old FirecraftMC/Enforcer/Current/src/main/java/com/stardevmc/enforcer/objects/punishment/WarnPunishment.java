package com.stardevmc.enforcer.objects.punishment;

import com.stardevmc.enforcer.Enforcer;
import com.stardevmc.enforcer.objects.enums.ExecuteOptions;
import com.stardevmc.enforcer.objects.enums.Visibility;
import com.stardevmc.enforcer.objects.actor.Actor;
import com.stardevmc.enforcer.objects.target.Target;
import com.starmediadev.lib.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;

import java.util.Map;

public class WarnPunishment extends Punishment implements Acknowledgeable {
    
    private boolean acknowledged;
    
    private Prompt prompt;
    
    public WarnPunishment(String server, Actor punisher, Target target, String reason, long date) {
        super(Type.WARN, server, punisher, target, reason, date);
    }
    
    public WarnPunishment(String server, Actor punisher, Target target, String reason, long date, Visibility visibility) {
        super(Type.WARN, server, punisher, target, reason, date, visibility);
    }
    
    public WarnPunishment(String id, String server, Actor punisher, Target target, String reason, long date, boolean active, Visibility visibility) {
        super(id, Type.WARN, server, punisher, target, reason, date, active, visibility);
    }
    
    public Prompt createPrompt() {
        Player player = target.getPlayer();
        String code = Enforcer.getInstance().getPunishmentModule().getManager().generateAckCode(this.id);
        if (player != null) {
            prompt = new ValidatingPrompt() {
                public String getPromptText(ConversationContext context) {
                    return Utils.color("&cYou have been warned by &7" + getPunisherName() + " &cfor &7" + reason
                            + "\n&cYou must acknowledge this warning before you may speak again."
                            + "\n&cPlease type the code &7" + code + " &cto acknowledge.");
                }
            
                protected boolean isInputValid(ConversationContext context, String input) {
                    return input.equals(code);
                }
            
                protected Prompt acceptValidatedInput(ConversationContext context, String input) {
                    setAcknowledged(true);
                    setActive(false);
                    context.getForWhom().sendRawMessage(Utils.color("&aYou have acknowledged your warning."));
                
                    String format = visibility.getPrefix() + "&6(" + server.toUpperCase() + ") &2" + getTargetName() + " &fhas acknowledged their warning.";
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (canSeeMessages(p, visibility)) {
                            p.sendMessage(Utils.color(format));
                        }
                    }
                    return END_OF_CONVERSATION;
                }
            };
        
            Conversation conv = new ConversationFactory(Enforcer.getInstance()).withFirstPrompt(prompt).withLocalEcho(false).buildConversation(player);
            conv.begin();
        } else {
            setOffline(true);
        }
        return prompt;
    }
    
    public void executePunishment(ExecuteOptions... options) {
        Player player = target.getPlayer();
        this.sendPunishMessage();
        if (player != null) {
            createPrompt();
        } else {
            setOffline(true);
        }
    }
    
    public void reversePunishment(Actor remover, long removedDate, String removedReason) {
    
    }
    
    public boolean isAcknowledged() {
        return acknowledged;
    }
    
    public void setAcknowledged(boolean value) {
        this.acknowledged = value;
    }
    
    public void onAcknowledge() {
        this.acknowledged = true;
    }
    
    public Prompt getPrompt() {
        if (this.prompt == null) {
            return createPrompt();
        }
        return prompt;
    }
    
    public Map<String, Object> serialize() {
        Map<String, Object> serialized = super.serializeBase();
        serialized.put("acknowledged", this.acknowledged);
        return serialized;
    }
    
    public static WarnPunishment deserialize(Map<String, Object> serialized) {
        boolean acknowledged = (boolean) serialized.get("acknowledged");
        WarnPunishment warning = (WarnPunishment) deserializeBase(serialized).build();
        warning.acknowledged = acknowledged;
        return warning;
    }
}