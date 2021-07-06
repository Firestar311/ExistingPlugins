package com.kingrealms.realms.questing.quests;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.profile.RealmProfile;
import com.kingrealms.realms.questing.Options;
import com.kingrealms.realms.questing.lines.QuestLine;
import com.kingrealms.realms.questing.rewards.Reward;
import com.kingrealms.realms.questing.tasks.Task;
import com.starmediadev.lib.builder.ItemBuilder;
import com.starmediadev.lib.util.ID;
import com.starmediadev.lib.util.Utils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public abstract class Quest {
    
    protected ID id, parentLine;
    protected QuestLine parentQuestLine;
    protected String name, description;
    protected Options options = new Options();
    protected Set<Task> questTasks = new LinkedHashSet<>();
    protected boolean repeatable;
    protected Set<ID> required = new HashSet<>();
    protected Set<Reward> rewards = new HashSet<>();
    
    public static final Material LOCKED_MATERIAL = Material.RED_STAINED_GLASS_PANE, 
            IN_PROGRESS_MATERIAL = Material.YELLOW_STAINED_GLASS_PANE, COMPLETED_MATERIAL = Material.LIME_STAINED_GLASS_PANE;
    
    public Quest(String name) {
        this.name = name;
    }
    
    public Quest(String name, ID id) {
        this.name = name;
        this.id = id;
    }
    
    public void addReward(Reward reward) {
        this.rewards.add(reward);
    }
    
    public void setParentLine(ID parentLine) {
        this.parentLine = parentLine;
    }
    
    public QuestLine getParentLine() {
        if (parentQuestLine == null) {
            parentQuestLine = Realms.getInstance().getQuestManager().getQuestLine(parentLine);
        }
        return parentQuestLine;
    }
    
    public boolean checkComplete(RealmProfile profile) {
        if (profile.isQuestLocked(this)) {
            return false;
        }
        
        if (!profile.isActiveQuestLine(getParentLine())) {
            return false;
        }
        
        Set<ID> completedTasks = profile.getCompletedTasks(this);
        if (completedTasks.isEmpty() && this.questTasks.isEmpty()) {
            profile.addCompletedQuest(this);
            onComplete(profile);
            return true;
        } else {
            for (Task task : this.questTasks) {
                if (!completedTasks.contains(task.getId())) {
                    if (!task.isOptional()) {
                        return false;
                    }
                }
            }
        }
        
        return true;
    }
    
    public boolean onComplete(RealmProfile profile) {
        if (!checkComplete(profile)) {
            return false;
        }
        
        for (Reward reward : this.rewards) {
            reward.applyReward(profile);
        }
        
        profile.addCompletedQuest(this);
        profile.sendMessage("&eCompleted Quest: &b" + this.getName());
        try {
            Quest nextQuest = getParentLine().getNextQuest(this);
            if (nextQuest == null) {
                 profile.getActiveQuestLines().remove(getParentLine().getId());
            } else {
                for (String l : nextQuest.getDisplayMap(profile)) {
                    profile.sendMessage(l);
                }
            }
        } catch (Exception e) {}
        return true;
    }
    
    public Options getOptions() {
        return options;
    }
    
    public void addTask(Task task) {
        this.questTasks.add(task);
    }
    
    public void removeTask(Task task) {
        this.questTasks.remove(task);
    }
    
    public void addRequired(Quest quest) {
        this.required.add(quest.getId());
    }
    
    public ID getId() {
        return id;
    }
    
    public void setId(ID id) {
        this.id = id;
    }
    
    public void removeRequired(Quest quest) {
        this.required.remove(quest.getId());
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
    
    public Set<ID> getRequired() {
        return required;
    }
    
    public Set<Task> getQuestTasks() {
        return questTasks;
    }
    
    public boolean isRepeatable() {
        return repeatable;
    }
    
    public void setRepeatable(boolean repeatable) {
        this.repeatable = repeatable;
    }
    
    public Set<Reward> getRewards() {
        return rewards;
    }
    
    public ItemStack getIcon(RealmProfile profile) {
        ItemBuilder ib = null;
        boolean questComplete = profile.isQuestComplete(this);
        boolean hasAccess = !profile.isQuestLocked(this);
    
        if (!questComplete && !hasAccess) {
            ib = ItemBuilder.start(LOCKED_MATERIAL);
        } else if (!questComplete && hasAccess) {
            ib = ItemBuilder.start(IN_PROGRESS_MATERIAL);
        } else if (questComplete) {
            ib = ItemBuilder.start(COMPLETED_MATERIAL);
        }
        
        ib.withName("&b" + this.name);
    
        List<String> lore = new LinkedList<>(Utils.wrapLore(40, this.description));
        lore.add(" ");
        lore.add("&e&lTasks");
        for (Task task : questTasks) {
            boolean taskComplete = profile.isTaskComplete(id, task.getId());
            String line = "";
            if (hasAccess) {
                if (taskComplete) {
                    line += "&a";
                } else {
                    if (task.isOptional()) {
                        line += "&e";
                    } else {
                        line += "&c";
                    }
                }
                line += " • " + task.getProgressLine(profile);
            } else {
                line = "&4&l • LOCKED";
            }
            
            lore.add(line);
        }
        
        lore.add(" ");
        lore.add("&a&lRewards");
        for (Reward reward : this.rewards) {
            lore.add("&d • " + reward.getName());
        }
        
        lore.add(" ");
        lore.add("&c&lRequired");
        for (ID r : this.required) {
            Quest quest = Realms.getInstance().getQuestManager().getQuest(r);
            lore.add("&6 • " + quest.getName());
        }
        
        ib.withLore(lore);
        
        return ib.buildItem();
    }
    
    public List<String> getDisplayMap(RealmProfile profile) {
        return new LinkedList<>() {{
                add(Utils.blankLine("&d&l", 50));
                add("&b&l" + getName());
                if (StringUtils.isNotEmpty(getDescription())) {
                    add("&f" + getDescription());
                }
                add("");
                for (Task task : questTasks) {
                    boolean taskComplete = profile.isTaskComplete(id, task.getId());
                    String line = "";
                    if (taskComplete) {
                        line += "&a";
                    } else {
                        if (task.isOptional()) {
                            line += "&e";
                        } else {
                            line += "&c";
                        }
                    }
                    line += " • " + task.getProgressLine(profile);
                    add(line);
                }
                add(Utils.blankLine("&d&l", 50));
        }};
    }
    
    public Task getTask(ID taskId) {
        for (Task task : this.questTasks) {
            if (task.getId().equals(taskId)) {
                return task;
            }
        }
        
        return null;
    }
    
    public void setParentLine(QuestLine questline) {
        this.parentQuestLine = questline;
    }
}