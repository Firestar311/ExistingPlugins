package com.stardevmc.enforcer.modules.punishments.gui;

import com.stardevmc.enforcer.Enforcer;
import com.stardevmc.enforcer.objects.enums.Visibility;
import com.stardevmc.enforcer.objects.prison.Prison;
import com.stardevmc.enforcer.objects.actor.PlayerActor;
import com.stardevmc.enforcer.objects.punishment.Punishment;
import com.stardevmc.enforcer.objects.enums.RawType;
import com.stardevmc.enforcer.modules.punishments.prompt.ReasonPrompt;
import com.stardevmc.enforcer.objects.punishment.PunishmentBuilder;
import com.stardevmc.enforcer.objects.target.Target;
import com.starmediadev.lib.builder.ItemBuilder;
import com.starmediadev.lib.gui.GUIButton;
import com.starmediadev.lib.gui.PaginatedGUI;
import com.starmediadev.lib.util.Utils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;

import java.util.*;

import static com.stardevmc.enforcer.modules.punishments.gui.CustomPunishMainGUI.SIZE;

public class CustomPunishSettingsGUI extends PaginatedGUI {
    
    private static Map<UUID, PunishmentBuilder> puBuilders = new HashMap<>();
    
    public CustomPunishSettingsGUI(Enforcer plugin, Player player, Target target) {
        super(plugin, "Custom Punishment > " + target.getName(), false, SIZE, false);
    
        PunishmentBuilder puBuilder;
        if (puBuilders.containsKey(player.getUniqueId())) {
            puBuilder = puBuilders.get(player.getUniqueId());
        } else {
            puBuilder = new PunishmentBuilder(target);
            puBuilders.put(player.getUniqueId(), puBuilder);
            puBuilder.setServer(Enforcer.getInstance().getSettingsManager().getServerName()).setPunisher(new PlayerActor(player.getUniqueId()));
        }
    
        ItemBuilder reasonBuilder = ItemBuilder.start(Material.BOOK).withName("&9Reason");
        if (StringUtils.isEmpty(puBuilder.getReason())) {
            reasonBuilder.withLore("&c&oNo reason given.");
        } else {
            reasonBuilder.withLore("&7" + puBuilder.getReason());
        }
    
        GUIButton reasonButton = new GUIButton(reasonBuilder.buildItem());
        reasonButton.setListener(e -> {
            player.closeInventory();
            new ConversationFactory(plugin).withFirstPrompt(new ReasonPrompt()).withLocalEcho(false).buildConversation(player).begin();
            });
        setButton(11, reasonButton);
        
        ItemBuilder typeBuilder = ItemBuilder.start(Material.WHITE_WOOL).withName("&9Type");
        if (puBuilder.getRawType() != null) {
            RawType type = puBuilder.getRawType();
            typeBuilder.asMaterial(type.getMaterial());
            String typeName = type.getColor() + type.name();
            typeBuilder.withLore("&9Selected: " + typeName);
        }
        
        GUIButton typeButton = new GUIButton(typeBuilder.buildItem());
        typeButton.setListener(e -> new CustomPunishTypeGUI(plugin, player, target).openGUI(player));
        setButton(22, typeButton);
        
        ItemBuilder lengthBuilder = ItemBuilder.start(Material.CLOCK).withName("&9Length");
        if (puBuilder.getLength() == -1) {
            lengthBuilder.asMaterial(Material.BEDROCK);
            lengthBuilder.withLore("&9Current: &cPERMANENT");
        } else if (puBuilder.getLength() == 0) {
            lengthBuilder.withLore("&9Current: &aNone");
        } else {
            lengthBuilder.asMaterial(Material.POPPED_CHORUS_FRUIT);
            lengthBuilder.withLore("&cCurrent: " + Utils.formatTime(puBuilder.getLength()));
        }
        GUIButton lengthButton = new GUIButton(lengthBuilder.buildItem());
        lengthButton.setListener(e -> new CustomPunishLengthGUI(plugin, player, target).openGUI(player));
        setButton(4, lengthButton);
    
        Visibility defaultVisibility = plugin.getPunishmentModule().getDefaultVisibiltiy();
        if (puBuilder.getVisibility() == null) {
            puBuilder.setVisibility(defaultVisibility);
        }
        ItemBuilder visibilityBuilder = ItemBuilder.start(puBuilder.getVisibility().getMaterial()).withName("&9Visibility");
        visibilityBuilder.withLore("&9Selected: &a" + puBuilder.getVisibility().name(), "", "&dDefault: &a" + defaultVisibility.name());
        GUIButton visibilityButton = new GUIButton(visibilityBuilder.buildItem());
        visibilityButton.setListener(e -> new CustomPunishVisibilityGUI(plugin, player, target).openGUI(player));
        setButton(15, visibilityButton);
        
        ItemBuilder confirmBuilder = ItemBuilder.start(Material.EMERALD).withName("&aConfirm").withLore("&7This confirms the punishment and checks for valid settings.");
        GUIButton confirmButton = new GUIButton(confirmBuilder.buildItem());
        confirmButton.setListener(e -> {
            if (puBuilder.getRawType() == null) {
                player.sendMessage(Utils.color("&cNo type selected."));
                return;
            }
            
            if (puBuilder.getRawType().equals(RawType.JAIL)) {
                Prison prison = Enforcer.getInstance().getPrisonModule().getManager().findPrison();
                if (prison == null) {
                    player.sendMessage(Utils.color("&cCould not find a prison."));
                    return;
                }
                puBuilder.setPrisonId(prison.getId());
            }
    
            puBuilder.setDate(System.currentTimeMillis());
            Punishment IPunishment = null;
            try {
                IPunishment = puBuilder.build();
            } catch (IllegalArgumentException ex) {
                player.sendMessage(Utils.color("&c" + ex.getMessage()));
            }
            player.closeInventory();
            plugin.getPunishmentModule().getManager().addPunishment(IPunishment);
            IPunishment.executePunishment();
            puBuilders.remove(player.getUniqueId());
        });
        setButton(26, confirmButton);
    }
    
    public static PunishmentBuilder getPunishmentBuilder(UUID uuid) {
        return puBuilders.get(uuid);
    }
}