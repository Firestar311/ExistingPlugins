package net.brutuspvp.core.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.UUID;

public class CourtCase {
	public enum Status {
		GATHERING, IN_PROGRESS, ARCHIVED;
	}

	public enum Outcome {
		UNDECIDED, FAVOR_STAFF, FAVOR_ACCUSED;
	}

	public enum Type {
		JUDGE, STAFF, ACCUSED;
	}

	private UUID judge;
	private UUID accused;
	private ArrayList<UUID> staff = new ArrayList<UUID>();
	private CourtCase.Status status;
	private CourtCase.Outcome outcome;
	private HashMap<String, Evidence> staffEvidence = new HashMap<String, Evidence>();
	private HashMap<String, Evidence> accusedEvidence = new HashMap<String, Evidence>();
	private HashMap<UUID, CourtCase.Type> participants = new HashMap<UUID, CourtCase.Type>();
	private int caseId = 0;
	

	private static TreeSet<Integer> usedCaseIds = new TreeSet<Integer>();
	
	public CourtCase(UUID judge, UUID accused) {
		this.judge = judge;
		this.accused = accused;
		this.participants.put(judge, CourtCase.Type.JUDGE);
		this.participants.put(accused, CourtCase.Type.ACCUSED);
		caseId = usedCaseIds.size() + 1;
		usedCaseIds.add(caseId);
	}
	
	public CourtCase(UUID judge, UUID accused, int caseId) {
		this.judge = judge;
		this.accused = accused;
		this.participants.put(judge, CourtCase.Type.JUDGE);
		this.participants.put(accused, CourtCase.Type.ACCUSED);
		this.caseId = caseId;
		usedCaseIds.add(caseId);
	}

	public UUID getJudge() {
		return judge;
	}
	
	public int getCaseId() {
		return caseId;
	}

	public UUID getAccused() {
		return accused;
	}

	public ArrayList<UUID> getStaff() {
		return new ArrayList<UUID>(staff);
	}

	public CourtCase.Status getStatus() {
		return status;
	}

	public CourtCase.Outcome getOutcome() {
		return outcome;
	}

	public HashMap<String, Evidence> getStaffEvidence() {
		return new HashMap<String, Evidence>(staffEvidence);
	}

	public HashMap<String, Evidence> getAccusedEvidence() {
		return new HashMap<String, Evidence>(accusedEvidence);
	}

	public HashMap<UUID, CourtCase.Type> getParticipants() {
		return new HashMap<UUID, CourtCase.Type>(participants);
	}

	public void addStaffMember(UUID uuid) {
		staff.add(uuid);
		participants.put(uuid, CourtCase.Type.STAFF);
	}

	public void removeStaffMember(UUID uuid) {
		staff.add(uuid);
		participants.remove(uuid);
	}

	public void setStatus(CourtCase.Status status) {
		this.status = status;
	}

	public void setOutcome(CourtCase.Outcome outcome) {
		this.outcome = outcome;
	}

	public void addStaffEvidence(Evidence evidence) {
		this.staffEvidence.put(evidence.getName(), evidence);
	}

	public void addAccusedEvidence(Evidence evidence) {
		this.accusedEvidence.put(evidence.getName(), evidence);
	}
	
	public boolean isStaff(UUID uuid) {
		return staff.contains(uuid);
	}
	
	public boolean isAccused(UUID uuid) {
		return accused.equals(uuid);
	}
	
	public boolean isJudge(UUID uuid) {
		return judge.equals(uuid);
	}
}