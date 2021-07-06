package com.stardevmc.titanterritories.core.objects;

import java.io.Serializable;

public class Lock implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private boolean value = false;

    public boolean getValue() {
        return value;
    }
    
    public void setValue(boolean value) {
        this.value = value;
    }
}