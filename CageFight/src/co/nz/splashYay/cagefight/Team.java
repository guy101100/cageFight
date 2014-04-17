package co.nz.splashYay.cagefight;

import java.io.Serializable;

public class Team implements Serializable {
	
	
	private static final long serialVersionUID = 1L;
	
	private int id;

	public Team(int id) {
		this.id = id;
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
