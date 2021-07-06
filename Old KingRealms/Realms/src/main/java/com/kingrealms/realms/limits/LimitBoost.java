package com.kingrealms.realms.limits;

import com.kingrealms.realms.Realms;
import com.kingrealms.realms.limits.limit.Limit;
import com.starmediadev.lib.util.Operator;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.HashMap;
import java.util.Map;

@SerializableAs("LimitBoost")
public class LimitBoost implements ConfigurationSerializable {
    
    private Limit limit; //mysql
    private String limit_id; //mysql
    private Number value; //mysql
    private Operator operator; //mysql
    
    public LimitBoost(String limit_id, double value, Operator operator) {
        this.limit_id = limit_id;
        this.value = value;
        this.operator = operator;
    }
    
    public LimitBoost(Map<String, Object> serialized) {
        this.limit_id = (String) serialized.get("limit_id");
        this.value = (double) serialized.get("value");
    }
    
    public Number getValue() {
        return value;
    }
    
    public Limit getLimit() {
        if (limit == null) {
            this.limit = Realms.getInstance().getLimitsManager().getLimit(limit_id);
        }
        
        return limit;
    }
    
    @Override
    public Map<String, Object> serialize() {
        return new HashMap<>() {{
            put("limit_id", limit_id);
            put("value", value);
        }};
    }
    
    public Operator getOperator() {
        return operator;
    }
    
    public String getLimitId() {
        return limit_id;
    }
}