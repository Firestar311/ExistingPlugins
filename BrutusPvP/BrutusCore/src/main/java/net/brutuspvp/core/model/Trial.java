package net.brutuspvp.core.model;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import net.brutuspvp.core.enums.Status;

public class Trial {
	
	private int id;
	private Status status;
	private CourtCase courtCase;
	private CourtRoom courtRoom;
	
	private Player judge;
	private Player accused;
	private ArrayList<Player> staff = new ArrayList<Player>();
	private ArrayList<Player> participants = new ArrayList<Player>();
	
	public Trial(int id, CourtCase courtCase, CourtRoom courtRoom) {
		this.id = id;
		this.courtCase = courtCase;
		this.courtRoom = courtRoom;
		this.status = Status.WAITING;
	}
	
	public Player getJudge() {
		return judge;
	}
	
	public Player getAccused() {
		return accused;
	}
	
	public ArrayList<Player> getStaff() {
		return new ArrayList<Player>(staff);
	}
	
	public void addStaff(Player staff) {
		this.staff.add(staff);
		this.participants.add(staff);
	}
	
	public void setJudge(Player judge) {
		this.judge = judge;
		this.participants.add(judge);
	}
	
	public void setAccused(Player accused) {
		this.accused = accused;
		this.participants.add(accused);
	}
	
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public int getId() {
		return id;
	}
	
	public ArrayList<Player> getParticipants() {
		return new ArrayList<Player>(participants);
	}
	
	
	public CourtCase getCourtCase() {
		return courtCase;
	}
	public CourtRoom getCourtRoom() {
		return courtRoom;
	}
}