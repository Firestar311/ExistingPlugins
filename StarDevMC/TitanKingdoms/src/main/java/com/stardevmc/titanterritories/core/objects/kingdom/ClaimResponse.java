package com.stardevmc.titanterritories.core.objects.kingdom;

import com.stardevmc.titanterritories.core.objects.interfaces.IHolder;
import com.stardevmc.titanterritories.core.objects.interfaces.IUser;

public class ClaimResponse {
    
    private IHolder holder;
    private IUser user;
    private String message;
    private boolean success;
    
    public ClaimResponse(IHolder holder, IUser user, String message, boolean success) {
        this.holder = holder;
        this.user = user;
        this.message = message;
        this.success = success;
    }
    
    public IHolder getHolder() {
        return holder;
    }
    
    public IUser getUser() {
        return user;
    }
    
    public String getMessage() {
        return message;
    }
    
    public boolean isSuccess() {
        return success;
    }
}