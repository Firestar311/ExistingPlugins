package com.kingrealms.realms.questing.tasks.progress;

import com.kingrealms.realms.questing.tasks.Task;
import com.kingrealms.realms.questing.tasks.TaskProgress;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.HashMap;
import java.util.Map;

@SerializableAs("IntegerTaskProgress")
public class IntegerTaskProgress extends TaskProgress {
    
    private int value; //mysql
    
    public IntegerTaskProgress(Task task) {
        super(task);
    }
    
    public IntegerTaskProgress(Map<String, Object> serialized) {
        super(serialized);
        this.value = Integer.parseInt((String) serialized.get("value"));
    }
    
    public Map<String, Object> serialize() {
        return new HashMap<>(super.serialize()) {{ 
            put("value", value + "");
        }};
    }
    
    public int getValue() {
        return value;
    }
    
    public void setValue(int value) {
        this.value = value;
    }
}