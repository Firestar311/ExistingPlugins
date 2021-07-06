package com.stardevmc.enforcer.objects.history;

import com.stardevmc.enforcer.Enforcer;
import com.stardevmc.enforcer.objects.actor.Actor;
import com.stardevmc.enforcer.objects.punishment.Punishment;
import com.starmediadev.lib.pagination.*;

import java.util.*;

public class StaffHistory implements IHistory {
    
    private Set<Punishment> punishments = new TreeSet<>();
    private int previousSize = 0;
    private Actor actor;
    
    private Paginator<Punishment> paginator;
    
    public StaffHistory(Actor actor) {
        this.actor = actor;
    }
    
    public Actor getActor() {
        return actor;
    }
    
    @Override
    public Set<Punishment> getPunishments() {
        if (previousSize < Enforcer.getInstance().getPunishmentModule().getManager().getPunishments().size()) {
            this.retrievePunishments();
        }
        return punishments;
    }
    
    @Override
    public Paginator<Punishment> getPaginator() {
        if (previousSize < Enforcer.getInstance().getPunishmentModule().getManager().getPunishments().size()) {
            this.retrievePunishments();
            this.getPaginator();
        }
        
        if (paginator == null) {
            this.generatePaginator();
        }
        return paginator;
    }
    
    @Override
    public void retrievePunishments() {
        this.punishments.clear();
        this.previousSize = Enforcer.getInstance().getPunishmentModule().getManager().getPunishments().size();
        this.paginator = null;
        Set<Punishment> punishments = Enforcer.getInstance().getPunishmentModule().getManager().getPunishments();
        if (!punishments.isEmpty()) {
            for (Punishment punishment : punishments) {
                if (punishment.getActor().equals(actor)) {
                    this.punishments.add(punishment);
                }
            }
            
            this.generatePaginator();
        }
    }
    
    @Override
    public void generatePaginator() {
        if (this.punishments.isEmpty()) {
            this.retrievePunishments();
        }
        if (previousSize < Enforcer.getInstance().getPunishmentModule().getManager().getPunishments().size()) {
            this.retrievePunishments();
        }
        this.paginator = PaginatorFactory.generatePaginator(7, punishments, new HashMap<>() {{
            put(DefaultVariables.TYPE, "Staff History");
            put(DefaultVariables.COMMAND, "staffhistory page");
        }});
    }
}