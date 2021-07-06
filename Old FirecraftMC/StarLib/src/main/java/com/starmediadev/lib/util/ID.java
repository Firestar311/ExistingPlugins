package com.starmediadev.lib.util;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.*;

@SerializableAs("ID")
public class ID implements ConfigurationSerializable, Comparable<ID> {
    
    private String value;
    public ID(String value) {
        this.value = value;
    }
    
    public ID(Map<String, Object> serialized) {
        this.value = (String) serialized.get("value");
    }
    
    public static ID randomID() {
        return new ID(Code.generateNewCode(8));
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        ID id = (ID) o;
        return Objects.equals(value, id.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
    
    @Override
    public Map<String, Object> serialize() {
        return new HashMap<>() {{
            put("value", value);
        }};
    }
    
    @Override
    public int compareTo(ID o) {
        return this.value.compareTo(o.value);
    }
    
    @Override
    public String toString() {
        return value;
    }
}