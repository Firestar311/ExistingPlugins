package net.brutuspvp.core.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.UUID;

import org.bukkit.Location;

import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;

public class Jail {
	
	private String jailName;
	private TreeMap<Integer, UUID> players = new TreeMap<Integer, UUID>();
	private ProtectedCuboidRegion region;
	private Location teleportPoint;
	
	public Jail(String jailName, ProtectedCuboidRegion region) {
		this.jailName = jailName;
		this.region = region;
	}
	
	public String getJailName() {
		return jailName;
	}
	
	public ProtectedCuboidRegion getRegion() {
		return region;
	}
	
	public Location getTeleportPoint() {
		return teleportPoint;
	}
	
	public void setTeleportPoint(Location location) {
		teleportPoint = location;
	}
	
	public List<UUID> getPlayers() {
		List<UUID> ps = new ArrayList<UUID>();
		for(UUID uuid : players.values()) {
			ps.add(uuid);
		}
		return ps;
	}
	
	public void addPlayer(UUID uuid) {
		int order = 1;
		while (players.get(order) != null) {
			order++;
		}
		players.put(order, uuid);
	}
	
	public void removePlayer(UUID uuid) {
		int key = 0;
		for(Entry<Integer, UUID> entry : players.entrySet()) {
			if(entry.getValue().equals(uuid)) {
				key = entry.getKey();
				break;
			}
		}
		if(key == 0) return;
		players.remove(key);
	}
	
	//TODO add a way to remove a player by the order id
}