package com.stardevmc.enforcer.objects.wave;

public class ActivationStats {
    private int approved, rejected, undecided;
    
    public ActivationStats(int approved, int rejected, int undecided) {
        this.approved = approved;
        this.rejected = rejected;
        this.undecided = undecided;
    }
    
    public int getApproved() {
        return approved;
    }
    
    public int getRejected() {
        return rejected;
    }
    
    public int getUndecided() {
        return undecided;
    }
}