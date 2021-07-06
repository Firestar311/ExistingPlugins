package com.kingrealms.realms.questing.tasks;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.questing.quests.Quest;
import com.starmediadev.lib.util.ID;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.HashMap;
import java.util.Map;

@SerializableAs("TaskProgress")
public abstract class TaskProgress implements ConfigurationSerializable {
    
    private ID questId, taskId;
    private Task task;
    private Quest quest;
    
    public TaskProgress(Task task) {
        this.quest = task.getQuest();
        this.questId = task.getQuest().getId();
        this.taskId = task.getId();
        this.task = task;
    }
    
    public TaskProgress(Map<String, Object> serialized) {
        this.questId = (ID) serialized.get("questId");
        this.taskId = (ID) serialized.get("taskId");
    }
    
    public Map<String, Object> serialize() {
        return new HashMap<>() {{ 
            put("questId", questId);
            put("taskId", taskId);
        }};
    }
    
    public Quest getQuest() {
        if (quest == null) {
            quest = Realms.getInstance().getQuestManager().getQuest(questId);
        }
        return quest;
    }
    
    public Task getTask() {
        if (task == null) {
            task = getQuest().getTask(taskId);
        }
        return task;
    }
}