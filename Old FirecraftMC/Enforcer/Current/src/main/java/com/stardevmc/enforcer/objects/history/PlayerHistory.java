package com.stardevmc.enforcer.objects.history;

import com.stardevmc.enforcer.Enforcer;
import com.stardevmc.enforcer.objects.punishment.Punishment;
import com.stardevmc.enforcer.objects.target.Target;
import com.starmediadev.lib.pagination.*;

import java.util.*;

public class PlayerHistory implements IHistory {
    
    private Paginator<Punishment> paginator;
    private int previousSize;
    private Set<Punishment> punishments = new TreeSet<>();
    private Target target;
    
    public PlayerHistory(Target target) {
        this.target = target;
    }
    
    public Target getTarget() {
        return target;
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
        }
        if (paginator == null) {
            this.generatePaginator();
        }
        return paginator;
    }
    
    @Override
    public void retrievePunishments() {
        this.punishments.clear();
        this.paginator = null;
        Set<Punishment> punishments = Enforcer.getInstance().getPunishmentModule().getManager().getPunishments();
        this.previousSize = punishments.size();
        for (Punishment punishment : punishments) {
            if (punishment.getTarget().equals(target)) {
                this.punishments.add(punishment);
            }
        }
        
        if (!this.punishments.isEmpty()) {
            this.generatePaginator();
        }
    }
    
    @Override
    public void generatePaginator() {
        if (previousSize < Enforcer.getInstance().getPunishmentModule().getManager().getPunishments().size()) {
            this.retrievePunishments();
        }
        if (this.punishments.isEmpty()) {
            this.retrievePunishments();
        }
        this.paginator = PaginatorFactory.generatePaginator(7, punishments, new HashMap<>() {{
            put(DefaultVariables.TYPE, "Player History");
            put(DefaultVariables.COMMAND, "history page");
        }});
    }
}