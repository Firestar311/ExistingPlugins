package com.kingrealms.realms.period;

import com.kingrealms.realms.territory.base.*;
import com.kingrealms.realms.territory.medievil.*;
import com.kingrealms.realms.territory.middle.*;
import com.kingrealms.realms.territory.modern.*;

public class TimePeriod {
    public enum Type {
        MEDEVIL, MIDDLE, MODERN
    }
    
    public static final TimePeriod MEDEVIL = new TimePeriod(Type.MEDEVIL, Kingdom.class, Hamlet.class, Colony.class);
    public static final TimePeriod MIDDLE = new TimePeriod(Type.MIDDLE, Nation.class, Town.class, Protectorate.class);
    public static final TimePeriod MODERN = new TimePeriod(Type.MODERN, Country.class, City.class, Province.class);
    
    protected Class<? extends Government> governmentType;
    protected Class<? extends Settlement> settlementType;
    protected Class<? extends Outpost> outpostType;
    protected Type type;
    
    private TimePeriod(Type type, Class<? extends Government> governmentType, Class<? extends Settlement> settlementType, Class<? extends Outpost> outpostType) {
        this.governmentType = governmentType;
        this.settlementType = settlementType;
        this.outpostType = outpostType;
        this.type = type;
    }
    
    public Government createGovernment(String name) {
        try {
            return this.governmentType.getConstructor(String.class).newInstance(name);
        } catch (Exception e) {
            //Realms.getInstance().getLogger().severe("Error while trying to create a government for the period " + this.type.name());
        }
        return null;
    }
    
    public Settlement createSettlement(String name) {
        try {
            return this.settlementType.getConstructor(String.class).newInstance(name);
        } catch (Exception e) {
            //Realms.getInstance().getLogger().severe("Error while trying to create a settlement for the period " + this.type.name());
        }
        return null;
    }
    
    public Outpost createOutpost(String name) {
        try {
            return this.outpostType.getConstructor(String.class).newInstance(name);
        } catch (Exception e) {
            //Realms.getInstance().getLogger().severe("Error while trying to create a settlement for the period " + this.type.name());
        }
        return null;
    }
    
    public Type getType() {
        return type;
    }
}