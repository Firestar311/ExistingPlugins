package com.firestar311.lib.util;

public class Result<S, F> {
    
    private S success;
    private F fail;
    
    public Result(S success, F fail) {
        this.success = success;
        this.fail = fail;
    }
    
    public S getSuccess() {
        return success;
    }
    
    public F getFail() {
        return fail;
    }
}