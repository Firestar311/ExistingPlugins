package net.brutuspvp.core.model;

import org.bukkit.Location;

import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;

public class CourtRoom {
	private String name;
	private ProtectedCuboidRegion region;
	private Location judgeLocation;
	private Location accusedLocation;
	private Location staffLocation;
	private boolean inUse;
	public CourtRoom(String name, ProtectedCuboidRegion region) {
		this.name = name;
		this.region = region;
		inUse = false;
	}
	public Location getJudgeLocation() {
		return judgeLocation;
	}
	public void setJudgeLocation(Location judgeLocation) {
		this.judgeLocation = judgeLocation;
	}
	public Location getAccusedLocation() {
		return accusedLocation;
	}
	public void setAccusedLocation(Location accusedLocation) {
		this.accusedLocation = accusedLocation;
	}
	public Location getStaffLocation() {
		return staffLocation;
	}
	public void setStaffLocation(Location staffLocation) {
		this.staffLocation = staffLocation;
	}
	public String getName() {
		return name;
	}
	public ProtectedCuboidRegion getRegion() {
		return region;
	}
	public boolean isInUse() {
		return inUse;
	}
	public void setInUse(boolean inUse) {
		this.inUse = inUse;
	}
}