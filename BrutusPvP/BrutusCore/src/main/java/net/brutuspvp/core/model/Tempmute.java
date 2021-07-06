package net.brutuspvp.core.model;

import java.util.UUID;

import net.brutuspvp.core.enums.Type;
import net.brutuspvp.core.model.abstraction.Punishment;

public class Tempmute extends Punishment {
	
	public Tempmute(UUID player, UUID actor, String reason, Type type, long expire) {
		super(player, actor, reason, type, expire);
	}
}