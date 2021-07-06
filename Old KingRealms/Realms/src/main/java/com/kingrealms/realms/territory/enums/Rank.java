package com.kingrealms.realms.territory.enums;

public enum Rank {
    INVITED(100, "Invited", "&7"), MEMBER(90, "Member", "&f"), TRUSTED(50, "Trusted", "&b"), MANAGER(10, "Manager", "&c"), LEADER(-1, "Mayor", "&4");
    
    private int order; //Lower is better
    private String name; //This will be temporary until I can get a utility for capitalizing all words
    private String color; //This is only going to be displayed in the chat channel
    Rank(int order, String name, String color) {
        this.order = order;
        this.name = name;
        this.color = color;
    }
    
    //TODO canInteract method
    
    public String getDisplayName() {
        return color + name;
    }
    
    public int getOrder() {
        return order;
    }
    
    public String getName() {
        return name;
    }
    
    public String getColor() {
        return color;
    }
}