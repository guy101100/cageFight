package co.nz.splashYay.cagefight.entities;

import co.nz.splashYay.cagefight.Team.ALL_TEAMS;

public class AIunit extends Entity {
	
	
	private static final long serialVersionUID = 1L;
	private Entity target;
	
	
	
	public AIunit(int id, int maxhealth, int currenthealth, int xpos, int ypos, ALL_TEAMS teamId) {
		super(xpos, ypos, maxhealth, currenthealth, id, teamId);
		
		
	}
	
	
	public void updateAIDataFromAIUnit(AIunit ai){
		
		this.currenthealth = ai.getCurrenthealth();
		this.direction = ai.getDirection();
		this.maxhealth = ai.getMaxhealth();
		this.xpos = ai.getXPos();
		this.ypos  = ai.getYPos();
		this.speed = ai.getSpeed();
	}
	
	
}
