package co.nz.splashYay.cagefight;

import java.io.Serializable;

public class Team implements Serializable {
	
	
	private static final long serialVersionUID = 1L;
	
	private int id;
	private ALL_TEAMS team;

	public Team(int id) {
		this.id = id;
		if (id == 1) {
			team = ALL_TEAMS.GOOD;
		} else {
			team = ALL_TEAMS.EVIL;
		}
	}
	
	public enum ALL_TEAMS {
		GOOD, EVIL
	}
	
	public ALL_TEAMS getTeam(){
		return team;
	}

	public int getTeamId() {
		return id;
	}

	public void setTeamId(int id) {
		this.id = id;
	}
	
	
	
	public void updateFromOtherTeamData(Team updateFrom) {
		this.id = updateFrom.getTeamId();
	}
	
	
	
	
}
