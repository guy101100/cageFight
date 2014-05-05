package co.nz.splashYay.cagefight.entities;

import co.nz.splashYay.cagefight.EntityState;
import co.nz.splashYay.cagefight.Team.ALL_TEAMS;

public class Base extends Entity{
	private static final long serialVersionUID = 1L;
	
	
	
	public Base(int xpos, int ypos, int maxhealth, int currenthealth, int id, ALL_TEAMS teamId) {
		super(xpos, ypos, maxhealth, currenthealth, id, teamId);
		this.speed = 0;
		
	}
	
	
	public void checkState(){
		boolean atackablePlayers = false;
		
		if (currenthealth <= 0) {
			state = EntityState.DEAD;
			
		} else if (atackablePlayers) { 
			state = EntityState.ATTACKING;
		} else {
			state = EntityState.IDLE;			
		}
	}
	
	
	

}
