package com.kingrealms.realms.entities.controller;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.LookControl;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

public class LookController extends LookControl {
    public LookController(Mob var0) {
        super(var0);
    }
    
    @Override
    public void setLookAt(Vec3 var0) {
        super.setLookAt(var0);
    }
    
    @Override
    public void setLookAt(Entity var0) {
        super.setLookAt(var0);
    }
    
    @Override
    public void setLookAt(Entity var0, float var1, float var2) {
        super.setLookAt(var0, var1, var2);
    }
    
    @Override
    public void setLookAt(double var0, double var2, double var4) {
        super.setLookAt(var0, var2, var4);
    }
    
    @Override
    public void setLookAt(double var0, double var2, double var4, float var6, float var7) {
        super.setLookAt(var0, var2, var4, var6, var7);
    }
    
    @Override
    public void tick() {
        super.tick();
    }
    
    @Override
    protected void clampHeadRotationToBody() {
        super.clampHeadRotationToBody();
    }
    
    @Override
    protected boolean resetXRotOnTick() {
        return super.resetXRotOnTick();
    }
    
    @Override
    public boolean isLookingAtTarget() {
        return super.isLookingAtTarget();
    }
    
    @Override
    public double getWantedX() {
        return super.getWantedX();
    }
    
    @Override
    public double getWantedY() {
        return super.getWantedY();
    }
    
    @Override
    public double getWantedZ() {
        return super.getWantedZ();
    }
    
    @Override
    protected Optional<Float> getXRotD() {
        return super.getXRotD();
    }
    
    @Override
    protected Optional<Float> getYRotD() {
        return super.getYRotD();
    }
    
    @Override
    public float rotateTowards(float var0, float var1, float var2) {
        return super.rotateTowards(var0, var1, var2);
    }
}