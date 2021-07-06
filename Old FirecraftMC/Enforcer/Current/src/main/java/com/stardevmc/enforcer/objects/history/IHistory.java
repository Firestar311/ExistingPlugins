package com.stardevmc.enforcer.objects.history;

import com.stardevmc.enforcer.objects.punishment.Punishment;
import com.starmediadev.lib.pagination.Paginator;

import java.util.Set;

public interface IHistory {
    
    Set<Punishment> getPunishments();
    Paginator<Punishment> getPaginator();
    
    void retrievePunishments();
    void generatePaginator();
}