package com.kingrealms.realms.channel.enums;

public enum Role {
    OWNER(0), MANAGER(10), MEMBER(20), INVITED(30), BANNED(40), MUTED(50), SERVER_STAFF(-1);
    
    private int order;
    Role() {}
    
    Role(int order) {
        this.order = order;
    }
    
    public int getOrder() {
        return order;
    }
}