package com.stardevmc.titanterritories.core.objects.help;

public abstract class TopicEntry {
    
    protected String name;
    
    public TopicEntry(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
}