package com.kingrealms.realms.questing.gui;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.profile.RealmProfile;
import com.kingrealms.realms.questing.lines.QuestLine;
import com.kingrealms.realms.questing.quests.Quest;
import com.starmediadev.lib.util.ID;
import com.starmediadev.lib.builder.ItemBuilder;
import com.starmediadev.lib.gui.GUIButton;
import com.starmediadev.lib.gui.PaginatedGUI;
import org.bukkit.Material;

public class QuestGui extends PaginatedGUI {
    public QuestGui(QuestLine line, RealmProfile profile) {
        super(Realms.getInstance(), line.getName(), true, 54);
        
        for (ID q : line.getQuests().values()) {
            Quest quest = Realms.getInstance().getQuestManager().getQuest(q);
            GUIButton button = new GUIButton(quest.getIcon(profile));
            addButton(button);
        }
        GUIButton button = new GUIButton(ItemBuilder.start(Material.SPECTRAL_ARROW).withName("&e&l<-Main").buildItem());
        button.setListener(e -> new QuestLinesGui(profile).openGUI(e.getWhoClicked()));
        setToolbarItem(0, button);
    }
}