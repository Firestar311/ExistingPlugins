package com.kingrealms.realms.questing.gui;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.profile.RealmProfile;
import com.kingrealms.realms.questing.lines.QuestLine;
import com.starmediadev.lib.util.ID;
import com.starmediadev.lib.gui.GUIButton;
import com.starmediadev.lib.gui.PaginatedGUI;
import com.starmediadev.lib.items.NBTWrapper;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class QuestLinesGui extends PaginatedGUI {
    public QuestLinesGui(RealmProfile profile) {
        super(Realms.getInstance(), "Quest Lines", true, 54);
        
        for (QuestLine questLine : Realms.getInstance().getQuestManager().getQuestLines()) {
            GUIButton button = new GUIButton(questLine.getIcon(profile));
            button.setListener(e -> {
                if (e.getClick() == ClickType.LEFT) {
                    new QuestGui(questLine, profile).openGUI(profile.getBukkitPlayer());
                } else if (e.getClick() == ClickType.RIGHT) {
                    ID old = profile.getQuestGuiDefault();
                    
                    if (old != null) {
                        for (int i = 0; i < e.getInventory().getContents().length; i++) {
                            ItemStack itemStack = e.getInventory().getContents()[i];
                            if (itemStack != null) {
                                try {
                                    String line = NBTWrapper.getNBTString(itemStack, "line");
                                    if (old.toString().equals(line)) {
                                        e.getInventory().setItem(i, Realms.getInstance().getQuestManager().getQuestLine(old).getIcon(profile));
                                    }
                                } catch (Exception ex) {}
                            }
                        }
                    }
                    
                    profile.setQuestGuiDefault(questLine.getId());
                    e.getInventory().setItem(e.getSlot(), questLine.getIcon(profile));
                } else if (e.getClick() == ClickType.SHIFT_LEFT) {
                    if (profile.isActiveQuestLine(questLine)) {
                        profile.sendMessage("&cYou have already started that quest line.");
                    } else {
                        profile.addActiveQuestLine(questLine);
                        e.getInventory().setItem(e.getSlot(), questLine.getIcon(profile));
                    }
                } else if (e.getClick() == ClickType.SHIFT_RIGHT) {
                    if (!profile.isQuestGuiDefault(questLine)) {
                        profile.sendMessage("&cThat quest line is not your default.");
                    } else {
                        profile.setQuestGuiDefault(null);
                        e.getInventory().setItem(e.getSlot(), questLine.getIcon(profile));
                    }
                }
            });
            
            addButton(button);
        }
    }
}