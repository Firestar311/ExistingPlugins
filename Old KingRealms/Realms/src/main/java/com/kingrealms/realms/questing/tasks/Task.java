package com.kingrealms.realms.questing.tasks;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.profile.RealmProfile;
import com.kingrealms.realms.questing.quests.Quest;
import com.starmediadev.lib.util.ID;
import org.apache.commons.lang.StringUtils;
import org.bukkit.GameMode;
import org.bukkit.event.Listener;

@SuppressWarnings("SameReturnValue")
public abstract class Task implements Listener {
    
    protected boolean optional;
    protected ID id, questId;
    protected String name;
    protected String description;
    
    public String getPlayerCompleteMessage() {
        return playerCompleteMessage;
    }
    
    public void setPlayerCompleteMessage(String playerCompleteMessage) {
        this.playerCompleteMessage = playerCompleteMessage;
    }
    
    protected String playerCompleteMessage;
    private Quest quest;
    
    public Task(ID id, ID questId, String name) {
        this(name);
        this.id = id;
        this.questId = questId;
    }
    
    public Task(String name) {
        this.name = name;
        Realms.getInstance().getSeason().addListener(this);
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
    
    public boolean isOptional() {
        return optional;
    }
    
    public void setOptional(boolean optional) {
        this.optional = optional;
    }
    
    public String onComplete(RealmProfile profile) {
        if (!profile.isActiveQuestLine(getQuest().getParentLine())) {
            return "";
        }
        
        if (profile.getBukkitPlayer().getGameMode() == GameMode.CREATIVE) {
            return "";
        }
        
        if (profile.isTaskComplete(questId, id)) {
            return "";
        }
        
        for (ID required : quest.getRequired()) {
            if (!profile.isQuestComplete(required)) {
                return "";
            }
        }
        
        if (profile.isQuestLocked(quest)) {
            return "";
        }
        
        profile.addCompletedTask(getQuest(), this);
        boolean complete = getQuest().checkComplete(profile);
        
        if (StringUtils.isNotEmpty(this.playerCompleteMessage)) {
            profile.sendMessage(playerCompleteMessage);
        }
        
        if (complete) {
            getQuest().onComplete(profile);
        }
        return "";
    }
    
    public String getDisplayLine() {
        return getDescription();
    }
    
    public Quest getQuest() {
        if (this.quest == null) {
            this.quest = Realms.getInstance().getQuestManager().getQuest(this.questId);
        }
        
        return this.quest;
    }
    
    public String getProgressLine(RealmProfile profile) {
        return getDisplayLine();
    }
}