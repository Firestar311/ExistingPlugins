package com.kingrealms.realms.chat.prefixes;

public class Prefix {
    private String id, text, color, permission;
    private boolean bold;
    
    public Prefix() {
    }
    
    public Prefix(String id, String text) {
        this.id = id;
        this.text = text;
    }
    
    public Prefix(String id, String text, String color, String permission, boolean bold) {
        this.id = id;
        this.text = text;
        this.color = color;
        this.permission = permission;
        this.bold = bold;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public String getColor() {
        return color;
    }
    
    public void setColor(String color) {
        this.color = color;
    }
    
    public String getPermission() {
        return permission;
    }
    
    public void setPermission(String permission) {
        this.permission = permission;
    }
    
    public boolean isBold() {
        return bold;
    }
    
    public void setBold(boolean bold) {
        this.bold = bold;
    }
}