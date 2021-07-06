package com.stardevmc.enforcer.modules.punishments.gui;

import com.stardevmc.enforcer.Enforcer;
import com.stardevmc.enforcer.manager.RuleManager;
import com.stardevmc.enforcer.modules.punishments.gui.button.VisibilityButton;
import com.stardevmc.enforcer.objects.enums.Visibility;
import com.stardevmc.enforcer.objects.punishment.Punishment;
import com.stardevmc.enforcer.objects.punishment.PunishmentBuilder;
import com.stardevmc.enforcer.objects.rules.*;
import com.stardevmc.enforcer.objects.target.PlayerTarget;
import com.stardevmc.enforcer.objects.target.Target;
import com.stardevmc.enforcer.util.EnforcerUtils;
import com.starmediadev.lib.builder.ItemBuilder;
import com.starmediadev.lib.gui.*;
import com.starmediadev.lib.util.Utils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

public class PunishGUI extends PaginatedGUI {
    
    static {
        PaginatedGUI.prepare(Enforcer.getInstance());
    }
    
    public PunishGUI(Enforcer plugin, Player pu, Target t) {
        super(plugin, "Punish > " + t.getName(), false, 54);
        
        VisibilityButton publicButton = new VisibilityButton(Visibility.PUBLIC);
        VisibilityButton staffOnlyButton = new VisibilityButton(Visibility.STAFF_ONLY);
        VisibilityButton silentButton = new VisibilityButton(Visibility.SILENT);
        
        Visibility defaultVisibility = plugin.getPunishmentModule().getDefaultVisibiltiy();
        if (defaultVisibility == Visibility.PUBLIC) {
            publicButton.setSelected(true);
        } else if (defaultVisibility == Visibility.STAFF_ONLY) {
            staffOnlyButton.setSelected(true);
        } else if (defaultVisibility == Visibility.SILENT) {
            silentButton.setSelected(true);
        }
        
        GUIButton customButton = new GUIButton(ItemBuilder.start(Material.WRITABLE_BOOK).withName("&6CUSTOM PUNISHMENT").withLore(Utils.wrapLore(35, "Create a custom punishment for this player.")).buildItem());
        
        ButtonListener listener = event -> {
            VisibilityButton selectedButton = (VisibilityButton) getButton(event.getSlot());
            selectedButton.setSelected(true);
            event.getClickedInventory().setItem(event.getSlot(), selectedButton.getItem());
            Visibility selectedVisibility = selectedButton.getVisibility();
            if (selectedVisibility != Visibility.PUBLIC) {
                publicButton.setSelected(false);
                event.getClickedInventory().setItem(48, publicButton.getItem());
            }
            if (selectedVisibility != Visibility.STAFF_ONLY) {
                staffOnlyButton.setSelected(false);
                event.getClickedInventory().setItem(49, staffOnlyButton.getItem());
            }
            if (selectedVisibility != Visibility.SILENT) {
                silentButton.setSelected(false);
                event.getClickedInventory().setItem(50, silentButton.getItem());
            }
        };
        
        ButtonListener customListener = e -> {
            ItemStack itemStack = e.getCurrentItem();
            if (itemStack == null) return;
            
            if (itemStack.hasItemMeta()) {
                if (itemStack.getItemMeta().getDisplayName().contains("CUSTOM PUNISHMENT")) {
                    new CustomPunishMainGUI(plugin, pu, t).openGUI(pu);
                }
            }
        };
        
        publicButton.setListener(listener);
        staffOnlyButton.setListener(listener);
        silentButton.setListener(listener);
        customButton.setListener(customListener);
        setToolbarItem(3, publicButton);
        setToolbarItem(4, staffOnlyButton);
        setToolbarItem(5, silentButton);
        setToolbarItem(8, customButton);
    
        RuleManager ruleManager = plugin.getRuleModule().getManager();
        for (Rule r : ruleManager.getRules()) {
            if (r.hasPermission(pu)) {
                if (r.getMaterial() != null) {
                    if (!(t instanceof PlayerTarget)) {
                        continue;
                    }
                    
                    PlayerTarget target = (PlayerTarget) t;
                    Entry<Integer, Integer> oN = ruleManager.getNextViolation(pu.getUniqueId(), target.getUniqueId(), r);
        
                    final RuleViolation off = r.getViolation(oN.getKey());
                    if (off == null) {
                        return;
                    }
        
                    List<String> lore = r.getItemStack().getItemMeta().getLore();
                    lore.add("");
                    if (off.hasPermission(pu)) {
                        lore.add("&fThe next punishment for &b" + t.getName());
                        lore.add("&fWill result in the following");
                        lore.add("&fReason: &e" + r.getName() + " Violation #" + off.getViolationNumber());
                        for (RulePunishment rP : off.getPunishments().values()) {
                            lore.add(" &8- " + EnforcerUtils.getPunishString(rP.getType(), rP.getLength()));
                        }
                    } else {
                        lore.add("&4You do not have permission to punish on the next offense");
                    }
        
                    ItemStack itemStack = ItemBuilder.start(r.getItemStack()).clearLore().withLore(lore).buildItem();
                    GUIButton button = new GUIButton(itemStack);
        
                    button.setListener(e -> {
                        Player player = ((Player) e.getWhoClicked());
                        Entry<Integer, Integer> offenseNumbers = ruleManager.getNextViolation(player.getUniqueId(), target.getUniqueId(), r);
            
                        RuleViolation offense = r.getViolation(offenseNumbers.getKey());
                        if (offense == null) {
                            player.sendMessage(Utils.color("&cThere was a severe problem getting the next offense, use a manual punishment if an emergency, otherwise, contact the plugin developer"));
                            return;
                        }
                        
                        if (offense.hasPermission(player)) {
                            String server = plugin.getSettingsManager().getPrefix();
                            long currentTime = System.currentTimeMillis();
                            UUID punisher = player.getUniqueId();
                            String reason = r.getName() + " Violation #" + offenseNumbers.getValue();
                            for (RulePunishment rulePunishment : offense.getPunishments().values()) {
                                PunishmentBuilder puBuilder = new PunishmentBuilder(target);
                                puBuilder.setType(rulePunishment.getType());
                                puBuilder.setReason(reason).setPunisher(punisher).setServer(server).setDate(currentTime).setLength(rulePunishment.getLength());
                                puBuilder.setRuleId(r.getId());
                                puBuilder.setViolationNumber(offenseNumbers.getValue());
        
                                Visibility visibility = Visibility.STAFF_ONLY;
                                if (publicButton.getItem().getItemMeta().hasEnchant(Enchantment.ARROW_DAMAGE)) {
                                    visibility = Visibility.PUBLIC;
                                } else if (silentButton.getItem().getItemMeta().hasEnchant(Enchantment.ARROW_DAMAGE)) {
                                    visibility = Visibility.SILENT;
                                }
        
                                puBuilder.setVisibility(visibility);
                                Punishment IPunishment = puBuilder.build();
                                plugin.getPunishmentModule().getManager().addPunishment(IPunishment);
                                IPunishment.executePunishment();
                            }
                            player.closeInventory();
                        } else {
                            ItemStack cache = button.getItem();
                            ItemStack warning = ItemBuilder.start(Material.BARRIER).withName("&4You cannot perform that action.").withLore("&cYou are not allowed to punish", "&cthat player because you do not", "&chave the Offense Permission.").buildItem();
                        
                            e.getClickedInventory().setItem(e.getSlot(), warning);
                            new BukkitRunnable() {
                                public void run() {
                                    try {
                                        e.getClickedInventory().setItem(e.getSlot(), cache);
                                    } catch (Exception ex) {}
                                }
                            }.runTaskLater(plugin, 120L);
                        }
                    });
                    addButton(button);
                }
            }
        }
    }
}