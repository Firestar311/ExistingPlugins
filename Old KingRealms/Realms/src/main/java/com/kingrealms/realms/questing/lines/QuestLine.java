package com.kingrealms.realms.questing.lines;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.profile.RealmProfile;
import com.kingrealms.realms.questing.quests.Quest;
import com.starmediadev.lib.util.ID;
import com.starmediadev.lib.builder.ItemBuilder;
import com.starmediadev.lib.util.Utils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.Map.Entry;

public class QuestLine {
    
    private Material icon;
    private ID id;
    private String name, description;
    private Map<Integer, ID> quests = new TreeMap<>();
    
    public QuestLine(ID id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public QuestLine(String name) {
        this.name = name;
    }
    
    public void setIcon(Material icon) {
        this.icon = icon;
    }
    
    public ItemStack getIcon(RealmProfile profile) {
        List<String> lore = new ArrayList<>();
        if (StringUtils.isNotEmpty(this.description)) {
            lore.addAll(Utils.wrapLore(40, this.description));
        }
        lore.add("");
        if (profile.isActiveQuestLine(this)) {
            lore.add("&eYou have started this questline.");
            lore.add("");
        }
        lore.add("&bTotal Quests: &f" + this.quests.size());
        if (profile.isActiveQuestLine(this)) {
            int completedQuests = 0;
            for (ID q : this.quests.values()) {
                Quest quest = Realms.getInstance().getQuestManager().getQuest(q);
                if (profile.isQuestComplete(quest)) {
                    completedQuests++;
                }
            }
            lore.add("&bCompleted Quests: &f" + completedQuests);
        }
        
        lore.add(" ");
        lore.add("&6&lLeft Click &fto view quests.");
        lore.add("&6&lShift Left Click &fto start this quest line.");
        lore.add("&6&lRight Click &fto set as default line.");
        lore.add("&6&lShift Right Click &fto unset this as the default line.");
        
        boolean glow = false;
        if (profile.isQuestGuiDefault(this)) {
            glow = true;
        }
        
        return ItemBuilder.start(icon).withName("&a" + name).withLore(lore).setGlowing(glow).addNBTString("line", this.id.toString()).buildItem();
    }
    
    public ID getId() {
        return id;
    }
    
    public void setId(ID id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Map<Integer, ID> getQuests() {
        return quests;
    }
    
    public void addQuest(int pos, Quest quest) {
        this.quests.put(pos, quest.getId());
    }
    
    public Quest getNextQuest(Quest current) {
        for (Entry<Integer, ID> entry : this.quests.entrySet()) {
            Integer pos = entry.getKey();
            ID quest = entry.getValue();
            if (current.getId().equals(quest)) {
                int nextPos = pos + 1;
                ID nextId = this.quests.get(nextPos);
                if (nextId != null) {
                    return Realms.getInstance().getQuestManager().getQuest(nextId);
                }
            }
        }
        return null;
    }
    
    public Quest getPreviousQuest(Quest current) {
        for (Entry<Integer, ID> entry : this.quests.entrySet()) {
            Integer pos = entry.getKey();
            ID quest = entry.getValue();
            if (current.getId().equals(quest)) {
                int nextPos = pos - 1;
                ID nextId = this.quests.get(nextPos);
                if (nextId != null) {
                    return Realms.getInstance().getQuestManager().getQuest(nextId);
                }
            }
        }
        return null;
    }
}