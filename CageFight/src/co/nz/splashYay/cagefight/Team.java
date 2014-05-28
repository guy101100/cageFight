package co.nz.splashYay.cagefight;

import java.io.Serializable;

public class Team implements Serializable {
	
	
	private static final long serialVersionUID = 1L;
	
	private int id;
	private ALL_TEAMS team;
	private int spawnXpos;
	private int spawnYpos;
	

	public Team(int id) {
		this.id = id;
		if (id == 1) {
			team = ALL_TEAMS.GOOD;
			spawnXpos = 3500;
			spawnYpos = 850;
		} else {
			team = ALL_TEAMS.BAD;
			spawnXpos = 215;
			spawnYpos = 850;
		}
	}
	
	
	
	public enum ALL_TEAMS {
		GOOD, BAD
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
	
	
	
	
	public int getSpawnXpos() {
		return spawnXpos;
	}

	public int getSpawnYpos() {
		return spawnYpos;
	}

	public void updateFromOtherTeamData(Team updateFrom) {
		//this.id = updateFrom.getTeamId();
	}
	
	
	
	
}
