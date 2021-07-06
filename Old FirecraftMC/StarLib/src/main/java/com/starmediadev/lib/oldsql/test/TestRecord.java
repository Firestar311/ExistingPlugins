package com.starmediadev.lib.oldsql.test;

import com.starmediadev.lib.oldsql.*;

import java.util.UUID;

@TableInfo(name = "test")
public class TestRecord implements IRecord<TestObject> {
    
    @RecordField(colType = "VARCHAR(60)")
    private String string;
    @RecordField
    private int testInt;
    @RecordField
    private long date;
    @RecordField
    private UUID testUUID;
    @RecordField
    private boolean testBoolean;
    
    public TestRecord(TestObject testObject) {
        this.string = testObject.getString();
        this.testInt = testObject.getTestInt();
        this.date = testObject.getDate();
        this.testUUID = testObject.getTestUUID();
        this.testBoolean = testObject.isTestBoolean();
    }
    
    public String getString() {
        return string;
    }
    
    public int getTestInt() {
        return testInt;
    }
    
    public long getDate() {
        return date;
    }
    
    public UUID getTestUUID() {
        return testUUID;
    }
    
    public boolean isTestBoolean() {
        return testBoolean;
    }
    
    @Override
    public TestObject getObject() {
        return null;
    }
    
    public String replaceStatementValues(String statement) {
        return null;
    }
}