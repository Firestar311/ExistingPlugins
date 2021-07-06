package com.firestar311.lib.sql.test;

import java.util.UUID;

public class TestObject {
    private String string;
    private int testInt;
    private long date;
    private UUID testUUID;
    private boolean testBoolean;
    
    public TestObject(String string, int testInt, long date, UUID testUUID, boolean testBoolean) {
        this.string = string;
        this.testInt = testInt;
        this.date = date;
        this.testUUID = testUUID;
        this.testBoolean = testBoolean;
    }
    
    public String getString() {
        return string;
    }
    
    public void setString(String string) {
        this.string = string;
    }
    
    public int getTestInt() {
        return testInt;
    }
    
    public void setTestInt(int testInt) {
        this.testInt = testInt;
    }
    
    public long getDate() {
        return date;
    }
    
    public void setDate(long date) {
        this.date = date;
    }
    
    public UUID getTestUUID() {
        return testUUID;
    }
    
    public void setTestUUID(UUID testUUID) {
        this.testUUID = testUUID;
    }
    
    public boolean isTestBoolean() {
        return testBoolean;
    }
    
    public void setTestBoolean(boolean testBoolean) {
        this.testBoolean = testBoolean;
    }
}