package com.kingrealms.realms.chat;

public class NameFormat {
    private String format;
    private boolean nameBold;
    
    public NameFormat() {
    }
    
    public NameFormat(String format, boolean nameBold) {
        this.format = format;
        this.nameBold = nameBold;
    }
    
    public String getFormat() {
        return format;
    }
    
    public void setFormat(String format) {
        this.format = format;
    }
    
    public boolean isNameBold() {
        return nameBold;
    }
    
    public void setNameBold(boolean nameBold) {
        this.nameBold = nameBold;
    }
}