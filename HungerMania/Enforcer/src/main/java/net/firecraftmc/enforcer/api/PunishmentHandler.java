package net.firecraftmc.enforcer.api;

import net.firecraftmc.enforcer.api.punishment.Punishment;

public interface PunishmentHandler {

    void handlePunishment(Punishment punishment);
}
